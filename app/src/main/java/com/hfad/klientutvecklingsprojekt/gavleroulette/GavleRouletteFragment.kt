package com.hfad.klientutvecklingsprojekt.gavleroulette

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NavUtils
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentGavleRouletteBinding
import com.hfad.klientutvecklingsprojekt.gamestart.CharacterStatus
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import kotlinx.coroutines.delay
import kotlin.math.log
import kotlin.random.Random
/**
 *
 * GavleRouletteFragment:
 *
 * Roulette spelet med olika funktioner för spelets logik
 *
 * @author William
 *
 */
class GavleRouletteFragment : Fragment(){
    private var _binding: FragmentGavleRouletteBinding? = null
    private val binding get() = _binding!!
    private var meModel : MeModel?=null
    private var rouletteModel: RouletteModel? = null
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Roulette")
    val playerRef = database.getReference("Player Data")
    var localPlayerID =""
    var localGameID = ""
    private val handler = Handler()
    // BG MUSIC
    private var mediaPlayer: MediaPlayer? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGavleRouletteBinding.inflate(inflater, container, false)
        val view = binding.root
        RouletteData.fetchGameModel()

        mediaPlayer = MediaPlayer.create(
            requireContext(), R.raw.android_song5_140bpm
        )
        mediaPlayer?.isLooping = true // Disable built-in looping
        mediaPlayer?.start()


        RouletteData.rouletteModel.observe(viewLifecycleOwner) { rouletteModel ->
            rouletteModel?.let {
                if (rouletteModel.gameId == null) {
                    Log.e("GavleRouletteFragment", "rouletteModel är null")
                } else {
                    this@GavleRouletteFragment.rouletteModel = rouletteModel
                    doWhenObserv()
                }
            }
        }

        MeData.meModel.observe(this) { meModel ->
            meModel?.let {
                this@GavleRouletteFragment.meModel = it
                setText()
            } ?: run {
                // Handle the case when meModel is null
                Log.e("LobbyFragment", "meModel is null")
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rouletAction.setOnClickListener{
            onTriggerPulled()
        }
        super.onViewCreated(view, savedInstanceState)
    }
    //Anrops via observeraren på rouletteModel har i syfte att anropa efter händelser i modelen
    fun doWhenObserv(){
        setUi()
        setPlayerInfo()
        if (rouletteModel?.gameStatus == GameStatus.FINISHED) {
            handler.postDelayed({
                try {
                    RouletteData.removeListner()
                    setScore()
                    database.getReference("Board Data").child(localGameID).child("randomVal").setValue(-1)
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    view?.findNavController()?.navigate(R.id.action_gavleRouletteFragment_to_testBoardFragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 6000)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
    //används för att hämta lokala spel och spelar id
    fun setText(){
        localGameID = meModel?.gameID?:""
        localPlayerID = meModel?.playerID?:""
    }
    //Ändrar spelare interface beroende på vad olika variabler har för värde
    fun setUi() {
        handler.postDelayed({
            //hämtar koppling till spelare från databasen för att få tag i nickname
            playerRef.child(localGameID).child("players").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result

                    rouletteModel?.apply {
                        binding.gameStatusText.text = when (gameStatus) {
                            //Händer när statusen är INPROGRESS
                            GameStatus.INPROGRESS -> {
                                val text = if (localPlayerID == currentPlayer) {
                                    "Your turn"
                                } else {
                                    val currentPlayerNickname =
                                        snapshot.child(currentPlayer?:"").child("nickname").value.toString()
                                    "$currentPlayerNickname turn"
                                }
                                text
                            }

                            //Händer när statusen är FINISHED
                            GameStatus.FINISHED -> {
                                val text = if (localPlayerID == winner) {
                                    "You won"
                                } else {
                                    val winnerNickname =
                                        snapshot.child(winner ?: "").child("nickname").value.toString()
                                    "$winnerNickname won"
                                }
                                text
                            }

                            else -> ""
                        }
                    }
                } else {
                    Log.e("setUi", "Error fetching player data: ${task.exception}")
                }
            }
        }, 500)
    }

    //Anroppas när knappen för try your luck
    fun onTriggerPulled(){
        myRef.child(localGameID).child("currentPlayer").get().addOnSuccessListener {
            //kör om det lokala spelar id är lika med id för currentPlayer
            if(localPlayerID == it.value.toString()){
                rouletteModel?.apply {
                    pullTheTrigger()
                    checksForKill()
                    checkForWinner()
                    changePlayer()
                    updateGameData(this,localGameID)
                }
            }else{
                //annars ger den feedback att det inte är ens tur
                Toast.makeText(context?.applicationContext ?: context,"Not your turn",Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
        }
    }

    //sätter gubbare och namn för spelare beronde på statusen i spelet
    fun setPlayerInfo() {
        rouletteModel?.apply {
            val currentContext = context ?: return

            // Går igenom alla spelare och tar ut vad de har för namn och spelar färg
            playerRef.child(localGameID).child("players").get().addOnSuccessListener {
                players?.onEachIndexed { index, entry ->
                    val snapshot = it
                    val playerId = players?.keys?.elementAt(index) ?: ""
                    val status = players?.get(playerId) ?: PlayerStatus.DEAD // Default to DEAD if status is not available

                    val resId = if (status == PlayerStatus.ALIVE) {
                        // spelar bild om statusen är ALIVE
                        currentContext.resources.getIdentifier(
                            "astro_${snapshot.child(playerId).child("color").value.toString()}",
                            "drawable",
                            currentContext.packageName
                        )
                    } else {
                        // spelar bild om statusen är DEAD
                        currentContext.resources.getIdentifier("sceleton", "drawable", currentContext.packageName)
                    }

                    // Hämtar id för rätt imageview
                    val astroId = currentContext.resources.getIdentifier(
                        "player_${index + 1}",
                        "id",
                        currentContext.packageName
                    )

                    // ändra bilde i korrekt ImageView
                    val characterImageView = binding.root.findViewById<ImageView>(astroId)
                    characterImageView.setImageResource(resId)
                    characterImageView.visibility = View.VISIBLE

                    // hämtar id för textview ändrar till rätt nickname och gör dem synlig
                    val textId = currentContext.resources.getIdentifier(
                        "player_${index + 1}_text",
                        "id",
                        currentContext.packageName
                    )
                    val text = binding.root.findViewById<TextView>(textId)
                    text.text = snapshot.child(playerId).child("nickname").value.toString()
                    text.visibility = View.VISIBLE
                }
            }
        }
    }

    //får en position på en kulla som man sen jämför med kula i vapnet
    // håller även koll på hur många försök och varv som har gjorts då det är antalet varv som ger en poäng
    fun pullTheTrigger() {
        rouletteModel?.apply {
                currentBullet = Random.nextInt(6) + 1
                attempts = attempts?.plus(1)
                if (attempts == aliveCount) {
                    attempts = 0
                    laps = laps?.plus(1)
                }
                updateGameData(this, localGameID)
        }
    }

    //anropar funktionen i RouletteData som spara modellen lokalt och till datbasen
    fun updateGameData(model: RouletteModel,id : String) {
        RouletteData.saveGameModel(model,id)
    }
    //Ändra till nästa spelare som lever
    fun changePlayer() {
        rouletteModel?.apply {
            //Hämtar indexet för spelare som har samma id som currentPlayer
            var currentPlayerIndex = players?.keys?.indexOf(currentPlayer) ?: -1

            if (currentPlayerIndex != -1) {
                var newIndex = (currentPlayerIndex + 1) % players?.size!!

                // Hittar nästa spelare som lever
                while (players?.get(players?.keys?.elementAt(newIndex) ?: "") == PlayerStatus.DEAD) {
                    newIndex = (newIndex + 1) % players?.size!!
                    // Kontrollera om alla spelare är döda
                    if (newIndex == currentPlayerIndex) {
                        currentPlayerIndex = 10
                            break
                    }
                }
                    // spara den nya currentPlayer
                    if (newIndex != currentPlayerIndex) {
                        currentPlayer = players?.keys?.elementAt(newIndex) ?: ""
                        myRef.child(localGameID).child("currentPlayer").setValue(currentPlayer)
                        updateGameData(this, localGameID)
                    }
                }
        }
    }

    //Avgör om LuckyNumber och currentBullet är samma
    //Om de är det som ändras spelarens status till Dead
    //Samt spara deras poäng
    fun checksForKill() {
        rouletteModel?.apply {
            if (luckyNumber==currentBullet) {
                players?.put(currentPlayer?:"",PlayerStatus.DEAD)
                score?.put(currentPlayer?:"",laps ?:0)
                aliveCount = aliveCount?.minus(1)
                updateGameData(this,localGameID)
            }
        }
    }
    //Om endast en spelare har statusen ALIVE, skriver spelaren med statusen som ALIVE till vinanre och sparar den poäng
    fun checkForWinner(){
        rouletteModel?.apply {
            if (aliveCount == 1) {
                val size = nbrOfPlayers ?: 0
                for (i in 0 until size){
                    if (players?.get(players?.keys?.elementAt(i)) == PlayerStatus.ALIVE){
                        score?.put(players?.keys?.elementAt(i)?:"",laps?.plus(5) ?:0)
                        winner = players?.keys?.elementAt(i)
                        break
                    }
                }
                gameStatus = GameStatus.FINISHED
                updateGameData(this,localGameID)
            }
        }
    }
    //Sparar poäng för spelare till databasen, varje spelare anropar den när spelet är klart
    fun setScore() {
        playerRef.child(localGameID).child("players").child(localPlayerID).get().addOnSuccessListener {
            rouletteModel?.apply {
                val currentScore = it.child("score").value.toString()
                val newScore = score?.get(localPlayerID)?.plus(currentScore.toInt()) ?: 0
                playerRef.child(localGameID).child("players").child(localPlayerID).child("score").setValue(newScore)
            }
        }
    }
}

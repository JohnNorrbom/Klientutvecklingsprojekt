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
import kotlin.random.Random

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
            Log.d("GavleRouletteFragment", "Observerar rouletteModel: $rouletteModel")
            rouletteModel?.let {
                if (rouletteModel.gameId == null) {
                    Log.e("GavleRouletteFragment", "rouletteModel är null")
                } else {
                    this@GavleRouletteFragment.rouletteModel = rouletteModel
                    setUi()
                    setPlayerInfo()
                    if (rouletteModel.gameStatus == GameStatus.FINISHED) {
                        handler.postDelayed({
                            setScore()
                            database.getReference("Board Data").child(localGameID).child("randomVal").setValue(-1)
                            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            try {
                                view?.findNavController()?.navigate(R.id.action_gavleRouletteFragment_to_testBoardFragment)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, 6000)
                    }
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

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun setText(){
        //den här
        localGameID = meModel?.gameID?:""
        localPlayerID = meModel?.playerID?:""
        Log.d("meModel","player ${localGameID} Game ${localPlayerID}")
    }

    fun setUi() {
        playerRef.child(localGameID).child("players").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result

                rouletteModel?.apply {
                    binding.gameStatusText.text = when (gameStatus) {
                        GameStatus.INPROGRESS -> {
                            Log.d("localP", "${localPlayerID}")
                            Log.d("currentP", "${currentPlayer}")
                            val text = if (localPlayerID == currentPlayer) {
                                "Your turn"
                            } else {
                                val currentPlayerNickname =
                                    snapshot.child(currentPlayer ?: "").child("nickname").value.toString()
                                "$currentPlayerNickname turn"
                            }
                            text
                        }

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
    }

    fun onTriggerPulled(){
        myRef.child(localGameID).child("currentPlayer").get().addOnSuccessListener {
            Log.d("localPlayerID","${localPlayerID}")
            Log.d("localPlayerID","${it.value}")
            if(localPlayerID == it.value.toString()){
                rouletteModel?.apply {
                    pullTheTrigger()
                    checksForKill()
                    checkForWinner()
                    changePlayer()
                    updateGameData(this,localGameID)
                }
            }else{
                Toast.makeText(context?.applicationContext ?: context,"Not your turn",Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
        }
    }

    //sets the correct info for each player
    fun setPlayerInfo() {
        rouletteModel?.apply {
            val currentContext = context ?: return  // Null check for context

            playerRef.child(localGameID).child("players").get().addOnSuccessListener {
                players?.onEachIndexed { index, entry ->
                    val snapshot = it
                    val playerId = players?.keys?.elementAt(index) ?: ""
                    val status = players?.get(playerId) ?: PlayerStatus.DEAD // Default to DEAD if status is not available

                    val resId = if (status == PlayerStatus.ALIVE) {
                        Log.d("alive","${status}")
                        // Alive player image
                        currentContext.resources.getIdentifier(
                            "astro_${snapshot.child(playerId).child("color").value.toString()}",
                            "drawable",
                            currentContext.packageName
                        )
                    } else {
                        // Dead player image
                        Log.d("dead","${status}")
                        currentContext.resources.getIdentifier("sceleton", "drawable", currentContext.packageName)
                    }

                    // Get id for ImageView
                    val astroId = currentContext.resources.getIdentifier(
                        "player_${index + 1}",
                        "id",
                        currentContext.packageName
                    )

                    // Change ImageView
                    val characterImageView = binding.root.findViewById<ImageView>(astroId)
                    characterImageView.setImageResource(resId)
                    characterImageView.visibility = View.VISIBLE

                    // Get id for radio button and make it visible
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

    //adds a value to currentBullet and pluses on how many attempts and laps thier has been
    fun pullTheTrigger() {
        rouletteModel?.apply {
                currentBullet = Random.nextInt(6) + 1
                Log.d("currentBullet", "${currentBullet}")
                Log.d("attempts", "${attempts}")
                attempts = attempts?.plus(1)
                Log.d("attempts", "${attempts}")
                Log.d("nbrOfPlayers", "${this.nbrOfPlayers}")
                if (attempts == aliveCount) {
                    attempts = 0
                    laps = laps?.plus(1)
                }
                updateGameData(this, localGameID)
        }
    }

//saves the model
    fun updateGameData(model: RouletteModel,id : String) {
        RouletteData.saveGameModel(model,id)
    }
    //Changes to the next player
    fun changePlayer() {
                rouletteModel?.apply {
                var currentPlayerIndex = players?.keys?.indexOf(currentPlayer) ?: -1
                Log.d("player keys", "${players?.keys}")
                Log.d("currentPlayerIndex", "${players?.keys?.indexOf(currentPlayer)}")
                Log.d("currentPlayerIndex", "${currentPlayerIndex}")

                if (currentPlayerIndex != -1) {
                    var newIndex = (currentPlayerIndex + 1) % players?.size!!
                    Log.d("newIndex", "${newIndex}")

                    // Find the next alive player
                    while (players?.get(
                            players?.keys?.elementAt(newIndex) ?: ""
                        ) == PlayerStatus.DEAD
                    ) {
                        newIndex = (newIndex + 1) % players?.size!!

                        // Kontrollera om alla spelare är döda
                        if (newIndex == currentPlayerIndex) {
                            currentPlayerIndex = 2
                            Log.d("changePlayer", "Inga levande spelare hittades.")
                            break
                        }
                    }

                    if (newIndex != currentPlayerIndex) {
                        currentPlayer = players?.keys?.elementAt(newIndex) ?: ""
                        setUi()
                        updateGameData(this, localGameID)
                    } else {
                        Log.d("changePlayer", "Alla spelare är döda.")
                        // Eventuell ytterligare hantering här, t.ex. avsluta spelet eller vidta andra åtgärder.
                    }

                }
            }
    }

    //Looks if anny of the bullets is equal to the current bullet if it is equal it changes the status for currentPlayer
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
    //if only one player is alive then it means that we have a winner
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

    fun setScore() {
        playerRef.child(localGameID).child("players").child(localPlayerID).get().addOnSuccessListener {
            rouletteModel?.apply {
                val currentScore = it.child("score").value.toString()
                Log.d("score", " ${currentScore}")
                val newScore = score?.get(localPlayerID)?.plus(currentScore.toInt()) ?: 0
                Log.d("score", " ${newScore}")
                playerRef.child(localGameID).child("players").child(localPlayerID).child("score").setValue(newScore)
            }
        }
    }
}

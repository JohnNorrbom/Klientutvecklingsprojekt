package com.hfad.klientutvecklingsprojekt.gavleroulette

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentGavleRouletteBinding
import com.hfad.klientutvecklingsprojekt.gamestart.CharacterStatus
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGavleRouletteBinding.inflate(inflater, container, false)
        val view = binding.root
        RouletteData.fetchGameModel()


        RouletteData.rouletteModel.observe(viewLifecycleOwner) {rouletteModel ->
            Log.d("GavleRouletteFragment", "Observing rouletteModel: $rouletteModel")
            rouletteModel?.let {
                if (it.gameId == null){
                    Log.e("GavleRouletteFragment", "rouletteModel is null")
                }else{
                    this@GavleRouletteFragment.rouletteModel = it
                    setUi()
                    setPlayerInfo()
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
        _binding = null
    }
    fun setText(){
        //den här
        localGameID = meModel?.gameID?:""
        localPlayerID = meModel?.playerID?:""
        Log.d("meModel","player ${localGameID} Game ${localPlayerID}")
    }

    fun setUi() {
        rouletteModel?.apply {
            playerRef.child(localGameID).child("players").get().addOnSuccessListener {
                val snapshot = it
                var text = ""
            binding.gameStatusText.text =
                when (gameStatus) {
                    GameStatus.INPROGRESS -> {
                            setPlayerInfo()
                            binding.test.text = luckyNumber?.get(0)
                        when (localPlayerID) {
                            currentPlayer -> text = "Your turn"
                            else -> text = snapshot.child(currentPlayer ?: "").child("nickname").value.toString() + " turn"
                        }
                        text
                    }

                    GameStatus.FINISHED -> {
                        when (localPlayerID) {
                            winner -> text = "You won"
                            else -> text = snapshot.child(winner ?: "").child("nickname").value.toString() + " won"
                        }
                        text

                    }
                    else -> {return@addOnSuccessListener}
                }
                when (gameStatus) {
                    GameStatus.FINISHED ->{
                        setScore()
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        view?.findNavController()?.navigate(R.id.action_gavleRouletteFragment_to_testBoardFragment)
                    }

                    else -> {return@addOnSuccessListener}
                }
            }
        }
    }
    fun onTriggerPulled(){
        myRef.child(localGameID).child("currentPlayer").get().addOnSuccessListener {
            Log.d("localPlayerID","${localPlayerID}")
            Log.d("localPlayerID","${it.value}")
            if(localPlayerID != it.value){
                Toast.makeText(context?.applicationContext ?: context,"Not your turn",Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }else{
                changePlayer()
                pullTheTrigger()
                checksForKill()
                checkForWinner()
            }
        }
    }

    //sets the correct info for each player
    fun setPlayerInfo() {
        rouletteModel?.apply {
            playerRef.child(localGameID).child("players").get().addOnSuccessListener {
                players?.onEachIndexed { index, entry ->
                    val snapshot = it
                    val playerId = players?.keys?.elementAt(index) ?: ""
                    val status = players?.get(playerId) ?: PlayerStatus.DEAD // Default to DEAD if status is not available

                    val resId = if (status == PlayerStatus.ALIVE) {
                        Log.d("alive","${status}")
                        // Alive player image
                        resources.getIdentifier(
                            "astro_${snapshot.child(playerId).child("color").value.toString()}",
                            "drawable",
                            requireContext().packageName
                        )
                    } else {
                        // Dead player image
                        Log.d("dead","${status}")
                        resources.getIdentifier("sceleton", "drawable", requireContext().packageName)
                    }

                    // Get id for ImageView
                    val astroId = resources.getIdentifier(
                        "player_${index + 1}",
                        "id",
                        requireContext().packageName
                    )

                    // Change ImageView
                    val characterImageView = binding.root.findViewById<ImageView>(astroId)
                    characterImageView.setImageResource(resId)
                    characterImageView.visibility = View.VISIBLE

                    // Get id for radio button and make it visible
                    val textId = resources.getIdentifier(
                        "player_${index + 1}_text",
                        "id",
                        requireContext().packageName
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
            Log.d("currentBullet","${currentBullet}")
            Log.d("attempts","${attempts}")
            attempts = attempts?.plus(1)
            Log.d("attempts","${attempts}")
            Log.d("nbrOfPlayers","${this.nbrOfPlayers}")
            if (attempts == aliveCount) {
                attempts = 0
                laps = laps?.plus(1)
            }
            binding.test1.text = "current attempts" + attempts.toString()
            binding.test2.text = "total laps " + laps.toString()
            updateGameData(this,localGameID)
        }
    }

    fun startGame() {
        rouletteModel?.apply {
            updateGameData(
                RouletteModel(
                    gameStatus = GameStatus.INPROGRESS,
                ),localGameID

            )
        }
    }

//saves the model
    fun updateGameData(model: RouletteModel,id : String) {
        RouletteData.saveGameModel(model,id)
    }
    //Changes to the next player
    fun changePlayer() {
        rouletteModel?.apply {
            myRef.child(localGameID).child("currentPlayer").get().addOnSuccessListener { snapshot ->
                Log.d("snapshot","${snapshot.value}")
                val currentPlayerIndex = players?.keys?.indexOf(snapshot.value) ?: -1
                Log.d("player keys","${players?.keys}")
                Log.d("currentPlayerIndex","${players?.keys?.indexOf(snapshot.value)}")
                Log.d("currentPlayerIndex","${currentPlayerIndex}")

                if (currentPlayerIndex != -1) {
                    var newIndex = (currentPlayerIndex + 1) % players?.size!!
                    Log.d("newIndex","${newIndex}")

                    // Find the next alive player
                    while (players?.get(players?.keys?.elementAt(newIndex) ?: "") == PlayerStatus.DEAD) {
                        newIndex = (newIndex + 1) % players?.size!!
                    }

                    Log.d("newIndex","${newIndex}")
                    Log.d("currentPlayer","${currentPlayer}")
                    currentPlayer = players?.keys?.elementAt(newIndex) ?: ""
                    Log.d("newcurrentPlayer","${currentPlayer}")
                    Log.d("newcurrentPlayer","${this}")
                    updateGameData(this, localGameID)
                }
            }
        }
    }
    //Looks if anny of the bullets is equal to the current bullet if it is equal it changes the status for currentPlayer
    fun checksForKill() {
        rouletteModel?.apply {
            if (luckyNumber?.contains(currentBullet.toString()) == true) {
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
                for (i in 0 until nbrOfPlayers!!){
                    if (players?.get(players?.keys?.elementAt(i)) == PlayerStatus.ALIVE){
                        score?.put(players?.keys?.elementAt(i)?:"",laps ?:0)
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
        playerRef.child(localGameID).child("players").get().addOnSuccessListener {
            val snapshot = it
            rouletteModel?.apply {
                for (player in snapshot.children) {
                    val currentScore = player.child("score").value.toString()
                    val newScore =
                        rouletteModel?.score?.get(player.toString())?.plus(currentScore.toInt())
                            ?: 0
                    playerRef.child(localGameID).child("players").child(player.toString()).child("score").setValue(newScore)
                }
            }

        }
    }

}
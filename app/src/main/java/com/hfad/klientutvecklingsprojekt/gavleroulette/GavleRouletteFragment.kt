package com.hfad.klientutvecklingsprojekt.gavleroulette

import android.annotation.SuppressLint
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
import com.google.firebase.Firebase
import com.google.firebase.database.database
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
    val lobbyRef = database.getReference("Player Data")
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
                if (it.gameId.equals(null)){
                    Log.e("GavleRouletteFragment", "rouletteModel is null")
                }else{
                    this@GavleRouletteFragment.rouletteModel = it
                    setUi()
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
            lobbyRef.child(localGameID).child("players").get().addOnSuccessListener {


            binding.gameStatusText.text =
                when (gameStatus) {

                    GameStatus.INPROGRESS -> {
                        var text = ""
                        if (laps == 0) {
                            val resId = resources.getIdentifier(
                                "chamber",
                                "drawable",
                                requireContext().packageName
                            )
                            binding.magasinSlot.setImageResource(resId)
                            binding.test.text = luckyNumber?.get(0)
                            setPlayerInfo()
                        }
                        when (localPlayerID) {
                            currentPlayer -> text = "Your turn"
                            else -> text = it.child(currentPlayer ?: "").child("nickname").value.toString() + " turn"

                        }
                        text
                    }

                    GameStatus.FINISHED -> {
                        var text = ""
                        when (localPlayerID) {
                            winner -> text = "You won"
                            else -> text =
                                it.child(winner ?: "").child("nickname").value.toString() + " won"
                        }
                        text
                    }

                    else -> {return@addOnSuccessListener}
                }
            }
        }
    }
    fun onTriggerPulled(){
        rouletteModel?.apply {
            lobbyRef.child(localGameID).child("players").get().addOnSuccessListener {
                Log.d("localPlayerID","${localPlayerID}")
                Log.d("localPlayerID","${rouletteModel?.currentPlayer}")
                if(localPlayerID != rouletteModel?.currentPlayer){
                    Toast.makeText(context?.applicationContext ?: context,"Not your turn",Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }else{
                    addBullet()
                    pullTheTrigger()
                    checksForKill()
                    changePlayer()
                    checkForWinner()
                    updateGameData(this,localGameID)
                }
            }
        }
    }

    //sets the correct info for each player
    @SuppressLint("SuspiciousIndentation")
    fun setPlayerInfo(){
        rouletteModel?.apply {
            lobbyRef.child(localGameID).child("players").get().addOnSuccessListener {
                val snapshot = it
                   players?.onEachIndexed { index, entry ->
                       Log.d("player i setPlayerInfo","${snapshot.child(players?.keys?.elementAt(index) ?:"").child("color").value}")
                       Log.d("player index setPlayerInfo","${index+1}")
                       val resId = resources.getIdentifier(
                           "astro_${snapshot.child(players?.keys?.elementAt(index) ?:"").child("color").value.toString()}",
                           "drawable",
                           requireContext().packageName
                       )
                       //get id for Imageview
                       val astroId = resources.getIdentifier(
                           "player_${index+1}",
                           "id",
                           requireContext().packageName
                       )

                       // change imageView
                       val characterImageView =
                           binding.root.findViewById<ImageView>(astroId)
                       Log.d("resId", "${resId}")
                       Log.d("astroId", "${astroId}")
                       Log.d("characterImageView", "${characterImageView}")
                       characterImageView.setImageResource(resId)
                       characterImageView.visibility = View.VISIBLE

                       // get id for radio button and makes it visible
                       val textId = resources.getIdentifier(
                           "player_${index+1}_text",
                           "id",
                           requireContext().packageName
                       )
                       val text = binding.root.findViewById<TextView>(textId)
                       text.text = snapshot.child(players?.keys?.elementAt(index) ?:"").child("nickname").value.toString()
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
            if (attempts == nbrOfPlayers) {
                attempts = 0
                laps = laps?.plus(1)
            }
            binding.test1.text = "current attempts" + attempts.toString()
            binding.test2.text = "total laps " + laps.toString()
            updateGameData(this,localGameID)
        }
    }

    fun addBullet() {
        rouletteModel?.apply {
            if (laps == 2 && luckyNumber?.get(1)?.isEmpty() ?: false) {
                var temp: String
                do {
                    temp = (Random.nextInt(6) + 1).toString()
                } while (temp == luckyNumber?.get(0))
                luckyNumber?.add(temp)
                binding.test.text = (luckyNumber?.get(0) + " " + luckyNumber?.get(1) )
                val resId = resources.getIdentifier("chamber_2_bullet", "drawable", requireContext().packageName)
                binding.magasinSlot.setImageResource(resId)
            }
            if (laps == 3 && luckyNumber?.get(2)?.isEmpty() ?: false) {
                var temp: String
                do {
                    temp = (Random.nextInt(6) + 1).toString()
                } while (luckyNumber?.contains(temp) ?: true || temp == luckyNumber?.get(2))
                luckyNumber?.add(temp)
                binding.test.text = luckyNumber?.get(0) + " " + luckyNumber?.get(1) + " " + luckyNumber?.get(2)
                val resId = resources.getIdentifier("chamber_3_bullet", "drawable", requireContext().packageName)
                binding.magasinSlot.setImageResource(resId)
            }
            if (laps == 4 && luckyNumber?.get(3)?.isEmpty() ?: false) {
                var temp: String
                do {
                    temp = (Random.nextInt(6) + 1).toString()
                } while (luckyNumber?.contains(temp) ?: true || temp == luckyNumber?.get(3))
                luckyNumber?.add(temp)
                binding.test.text =
                    luckyNumber?.get(0) + " " + luckyNumber?.get(1) + " " + luckyNumber?.get(2) + " " + luckyNumber?.get(3)
                val resId = resources.getIdentifier("chamber_4_bullet", "drawable", requireContext().packageName)
                binding.magasinSlot.setImageResource(resId)
            }
            if (laps == 5 && luckyNumber?.get(4)?.isEmpty()?: false) {
                var temp: String
                do {
                    temp = (Random.nextInt(6) + 1).toString()
                } while (luckyNumber?.contains(temp) ?: true || temp == luckyNumber?.get(4))
                luckyNumber?.add(temp)
                binding.test.text =
                    luckyNumber?.get(0) + " " + luckyNumber?.get(1) + " " + luckyNumber?.get(2) + " " + luckyNumber?.get(3) + " " + luckyNumber?.get(4)
                val resId = resources.getIdentifier("chamber_5_bullet", "drawable", requireContext().packageName)
                binding.magasinSlot.setImageResource(resId)
            }
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
            lobbyRef.child(localGameID).child("players").get().addOnSuccessListener { snapshot ->
                val currentPlayerIndex = players?.keys?.indexOf(currentPlayer) ?: -1

                if (currentPlayerIndex != -1) {
                    var newIndex = (currentPlayerIndex + 1) % players?.size!!

                    // Find the next alive player
                    while (players?.get(players?.keys?.elementAt(newIndex) ?: "") == PlayerStatus.DEAD) {
                        newIndex = (newIndex + 1) % players?.size!!
                    }

                    currentPlayer = players?.keys?.elementAt(newIndex) ?: ""
                    updateGameData(this, localGameID)
                }
            }
        }
    }
    //Looks if anny of the bullets is equal to the current bullet if it is equal it changes the status for currentPlayer
    fun checksForKill() {
        rouletteModel?.apply {
            if (luckyNumber?.contains(currentBullet.toString()) == true) {
                val resId = resources.getIdentifier("sceleton", "drawable", requireContext().packageName)
                players?.put(currentPlayer?:"",PlayerStatus.DEAD)
                val playerImageViewId = resources.getIdentifier(
                    "player_${players?.keys?.indexOf(currentPlayer)?.plus(1)}",
                    "id",
                    requireContext().packageName
                )
                val playerImageView =
                    binding.root.findViewById<ImageView>(playerImageViewId)
                playerImageView.setImageResource(resId)
                aliveCount = aliveCount?.minus(1)
                updateGameData(this,localGameID)
            }
        }
    }
    //if only one player is alive then it means that we have a winner
    fun checkForWinner(){
        rouletteModel?.apply {
            if (aliveCount == 1) {
                changePlayer()
                winner = currentPlayer
                gameStatus = GameStatus.FINISHED
                updateGameData(this,localGameID)
            }
        }
    }

}
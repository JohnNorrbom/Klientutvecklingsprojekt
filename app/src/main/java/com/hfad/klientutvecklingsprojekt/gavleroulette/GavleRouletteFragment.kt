package com.hfad.klientutvecklingsprojekt.gavleroulette

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
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import kotlin.random.Random

class GavleRouletteFragment : Fragment(), View.OnClickListener{
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

        MeData.meModel.observe(this) { meModel ->
            meModel?.let {
                this@GavleRouletteFragment.meModel = it
                setText()
            } ?: run {
                // Handle the case when meModel is null
                Log.e("LobbyFragment", "meModel is null")
            }
        }

        binding.startGameBtn.setOnClickListener {
            startGame()

        }
        binding.rouletAction.setOnClickListener(this)

        RouletteData.rouletteModel.observe(this) {
            rouletteModel = it
            setUi()
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    fun setText(){
        //den här
        localGameID= meModel?.gameID?:""
        localPlayerID = meModel?.playerID?:""
        Log.d("meModel","player ${localGameID} Game ${localPlayerID}")
    }

    fun setUi() {
        rouletteModel?.apply {
            binding.startGameBtn.visibility = View.VISIBLE

            binding.gameStatusText.text =
                when (gameStatus) {
                    GameStatus.CREATED -> {
                        binding.startGameBtn.visibility = View.INVISIBLE
                        "Game ID :" + gameId
                    }


                    GameStatus.INPROGRESS -> {
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

                        binding.startGameBtn.visibility = View.INVISIBLE
                        currentPlayer + " turn"
                    }

                    GameStatus.FINISHED -> {
                        winner + " Won"
                    }

                    else -> {return}
                }
        }
    }

    fun setPlayerInfo(){
       lobbyRef.child(localGameID).child("players").get().addOnSuccessListener {
           val snapshot = it
           var i = 1
           for(player in snapshot.children){

               val resId = resources.getIdentifier(
                   "astro_${player.child("color").value}",
                   "drawable",
                   requireContext().packageName
               )
               //get id for Imageview
               val astroId = resources.getIdentifier(
                   "player_${i}}",
                   "id",
                   requireContext().packageName
               )
               // change imageView
               val characterImageView =
                   binding.root.findViewById<ImageView>(astroId)
               characterImageView.setImageResource(resId)

               // get id for radio button and makes it visible
               val textId = resources.getIdentifier(
                   "player_${i}_text",
                   "id",
                   requireContext().packageName
               )
               val text = binding.root.findViewById<TextView>(textId)
               text .visibility = View.VISIBLE
               i++
           }
       }
    }

    fun pullTheTrigger() {
        rouletteModel?.apply {
            currentBullet = Random.nextInt(6) + 1
            attempts = attempts?.plus(1)
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
                    nbrOfPlayers = offlineParticipants?.size,
                    aliveCount = offlineParticipants?.size
                ),localGameID

            )
        }
    }
    /*fun startGame() {
        gameModel?.apply {
            updateGameData(
                GameModel(
                    gameId = gameId,
                    gameStatus = GameStatus.INPROGRESS,
                )
            )
        }
    }

    fun initPlayers(){
        gameModel?.apply {
            offlineParticipants = mutableListOf(Pair(participants[0].first,PlayerStatus.ALIVE))
            currentPlayer = offlineParticipants[Random.nextInt(offlineParticipants.size)].first
            nbrOfPlayers = offlineParticipants.size
            aliveCount = offlineParticipants.size

            updateGameData(this)
        }
    }
*/

    fun updateGameData(model: RouletteModel,id : String) {
        RouletteData.saveGameModel(model,id)
    }
    fun changePlayer(){
        rouletteModel?.apply {
                updateGameData(this,localGameID)
            }
    }
    fun checkForRemainingPlayers() {
        rouletteModel?.apply {
            if (luckyNumber?.contains(currentBullet.toString()) == true) {
                        val resId = resources.getIdentifier("sceleton", "drawable", requireContext().packageName)
                        if(offlineParticipants?.keys?.contains(currentPlayer) == true) {
                            offlineParticipants?.put(currentPlayer?:"",PlayerStatus.DEAD)
                            val playerImageViewId = resources.getIdentifier(
                                "player_${offlineParticipants?.get(currentPlayer)?.ordinal}",
                                "id",
                                requireContext().packageName
                            )
                            val playerImageView =
                                binding.root.findViewById<ImageView>(playerImageViewId)
                            playerImageView.setImageResource(resId)
                            aliveCount = aliveCount?.minus(1)
                        }
            }
            updateGameData(this,localGameID)
        }
    }
    fun checkForWinner(){
        rouletteModel?.apply {
            if (aliveCount == 1) {
                gameStatus = GameStatus.FINISHED
            }
            updateGameData(this,localGameID)
        }
    }

    override fun onClick(v: View?) {
        rouletteModel?.apply {
            if (gameStatus != GameStatus.INPROGRESS) {
                Toast.makeText(requireContext().applicationContext, "Game not started", Toast.LENGTH_SHORT).show()
                return
            }
            //game is in progress
            addBullet()
            changePlayer()
            pullTheTrigger()
            checkForRemainingPlayers()
            checkForWinner()
            updateGameData(this,localGameID)

        }
    }
}
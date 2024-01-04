package com.hfad.klientutvecklingsprojekt.gavleroulette

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

                    GameStatus.JOINED -> {
                        "Click on start game"
                    }

                    GameStatus.INPROGRESS -> {
                        if (laps == 0) {
                            val resId = resources.getIdentifier("chamber", "drawable", requireContext().packageName)
                            binding.magasinSlot.setImageResource(resId)
                            binding.test.text = luckyNumber[0]
                            binding.playerOneText.text = offlineParticipants[0].first
                            binding.playerTwoText.text = offlineParticipants[1].first
                            binding.playerThreeText.text = offlineParticipants[2].first
                            val p1 = resources.getIdentifier("astro_white", "drawable", requireContext().packageName)
                            val p2 = resources.getIdentifier("astro_blue", "drawable", requireContext().packageName)
                            val p3 = resources.getIdentifier("astro_red", "drawable", requireContext().packageName)
                            binding.player1.setImageResource(p1)
                            binding.player2.setImageResource(p2)
                            binding.player3.setImageResource(p3)
                        }
                        binding.startGameBtn.visibility = View.INVISIBLE
                        var text = ""
                        for (i in 0 until offlineParticipants.size) {
                            if (currentPlayer == offlineParticipants[i].first) {
                                text = offlineParticipants[i].first + " turn"
                            }
                        }
                        text
                    }

                    GameStatus.FINISHED -> {
                        winner + " Won"
                    }
                }
        }
    }

    fun pullTheTrigger() {
        rouletteModel?.apply {
            currentBullet = Random.nextInt(6) + 1
            attempts += 1
            if (attempts == nbrOfPlayers) {
                attempts = 0
                laps += 1
            }
            binding.test1.text = "current attempts" + attempts.toString()
            binding.test2.text = "total laps " + laps.toString()
            updateGameData(this,localGameID)
        }
    }

    fun addBullet() {
        rouletteModel?.apply {
            if (laps == 2 && luckyNumber[1].isEmpty()) {
                var temp: String
                do {
                    temp = (Random.nextInt(6) + 1).toString()
                } while (temp == luckyNumber[0])
                luckyNumber[1] = temp
                binding.test.text = luckyNumber[0] + " " + luckyNumber[1]
                val resId = resources.getIdentifier("chamber_2_bullet", "drawable", requireContext().packageName)
                binding.magasinSlot.setImageResource(resId)
            }
            if (laps == 3 && luckyNumber[2].isEmpty()) {
                var temp: String
                do {
                    temp = (Random.nextInt(6) + 1).toString()
                } while (luckyNumber.contains(temp) || temp == luckyNumber[2])
                luckyNumber[2] = temp
                binding.test.text = luckyNumber[0] + " " + luckyNumber[1] + " " + luckyNumber[2]
                val resId = resources.getIdentifier("chamber_3_bullet", "drawable", requireContext().packageName)
                binding.magasinSlot.setImageResource(resId)
            }
            if (laps == 4 && luckyNumber[3].isEmpty()) {
                var temp: String
                do {
                    temp = (Random.nextInt(6) + 1).toString()
                } while (luckyNumber.contains(temp) || temp == luckyNumber[3])
                luckyNumber[3] = temp
                binding.test.text =
                    luckyNumber[0] + " " + luckyNumber[1] + " " + luckyNumber[2] + " " + luckyNumber[3]
                val resId = resources.getIdentifier("chamber_4_bullet", "drawable", requireContext().packageName)
                binding.magasinSlot.setImageResource(resId)
            }
            if (laps == 5 && luckyNumber[4].isEmpty()) {
                var temp: String
                do {
                    temp = (Random.nextInt(6) + 1).toString()
                } while (luckyNumber.contains(temp) || temp == luckyNumber[4])
                luckyNumber[4] = temp
                binding.test.text =
                    luckyNumber[0] + " " + luckyNumber[1] + " " + luckyNumber[2] + " " + luckyNumber[3] + " " + luckyNumber[4]
                val resId = resources.getIdentifier("chamber_5_bullet", "drawable", requireContext().packageName)
                binding.magasinSlot.setImageResource(resId)
            }
            updateGameData(this,localGameID)
        }
    }

    fun startGame() {
        rouletteModel?.apply {
            val initialParticipants = mutableListOf(
                Pair("1", PlayerStatus.ALIVE),
                Pair("2", PlayerStatus.ALIVE),
                Pair("3", PlayerStatus.ALIVE)
            )
            updateGameData(
                RouletteModel(
                    gameId = gameId,
                    gameStatus = GameStatus.INPROGRESS,
                    offlineParticipants = initialParticipants,
                    currentPlayer = initialParticipants[Random.nextInt(initialParticipants.size)].first,
                    nbrOfPlayers = initialParticipants.size,
                    aliveCount = initialParticipants.size
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
            for (i in 0 until offlineParticipants.size) {
                if (currentPlayer == offlineParticipants[i].first) {
                    var j = i + 1
                    while (j != i) {
                        if (j == offlineParticipants.size) {
                            j = 0
                        }

                        if (offlineParticipants[j].second != PlayerStatus.DEAD) {
                            currentPlayer = offlineParticipants[j].first
                            break
                        }

                        j += 1
                    }
                    break
                }
                updateGameData(this,localGameID)
            }
        }
    }
    fun checkForRemainingPlayers() {
        rouletteModel?.apply {
            if (luckyNumber.contains(currentBullet.toString())) {
                for (i in 0 until offlineParticipants.size) {
                        val resId = resources.getIdentifier("sceleton", "drawable", requireContext().packageName)
                        if(currentPlayer == offlineParticipants[i].first) {
                            offlineParticipants[i] =
                                Pair(offlineParticipants[i].first, PlayerStatus.DEAD)
                            val playerImageViewId = resources.getIdentifier(
                                "player_${i + 1}",
                                "id",
                                requireContext().packageName
                            )
                            val playerImageView =
                                binding.root.findViewById<ImageView>(playerImageViewId)
                            playerImageView.setImageResource(resId)
                            aliveCount--
                        }
                }
            }
            updateGameData(this,localGameID)
        }
    }
    fun checkForWinner(){
        rouletteModel?.apply {
            if (aliveCount == 1) {
                for (i in 0 until offlineParticipants.size) {
                    if (offlineParticipants[i].second == PlayerStatus.ALIVE){
                        winner = offlineParticipants[i].first
                    }
                }
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
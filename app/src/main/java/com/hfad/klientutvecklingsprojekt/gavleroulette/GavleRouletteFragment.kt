package com.hfad.wifeposijo_boardgame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hfad.klientutvecklingsprojekt.databinding.FragmentGavleRouletteBinding
import com.hfad.klientutvecklingsprojekt.gavleroulette.GameData
import com.hfad.klientutvecklingsprojekt.gavleroulette.GameModel
import com.hfad.klientutvecklingsprojekt.gavleroulette.GameStatus
import com.hfad.klientutvecklingsprojekt.gavleroulette.PlayerStatus
import kotlin.random.Random

class GavleRouletteFragment : Fragment(), View.OnClickListener{
    private var _binding: FragmentGavleRouletteBinding? = null
    private val binding get() = _binding!!
    private var gameModel: GameModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGavleRouletteBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.startGameBtn.setOnClickListener {
            startGame()
        }
        binding.rouletAction.setOnClickListener(this)

        GameData.gameModel.observe(this) {
            gameModel = it
            setUi()
        }

        return view;
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    fun setUi() {
        gameModel?.apply {
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
                        }
                        val p1 = resources.getIdentifier("astro_white", "drawable", requireContext().packageName)
                        val p2 = resources.getIdentifier("astro_blue", "drawable", requireContext().packageName)
                        binding.playerTwo.setImageResource(p2)
                        binding.playerOne.setImageResource(p1)
                        binding.startGameBtn.visibility = View.INVISIBLE
                        if (currentPlayer == offlineParticipants[0].first) offlineParticipants[0].first + " turn" else offlineParticipants[1].first + " turn"
                    }

                    GameStatus.FINISHED -> {
                        if (winner.isNotEmpty()) winner + " Won" else "DRAW"
                    }
                }
        }
    }

    fun pullTheTrigger() {
        gameModel?.apply {
            currentBullet = Random.nextInt(6) + 1
            attempts += 1
            if (attempts == 2) {
                attempts = 0
                laps += 1
            }
            binding.test1.text = "current attempts" + attempts.toString()
            binding.test2.text = "total laps " + laps.toString()
            updateGameData(this)
        }
    }

    fun addBullet() {
        gameModel?.apply {
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
            updateGameData(this)
        }
    }

    fun startGame() {
        gameModel?.apply {
            updateGameData(
                GameModel(
                    gameId = gameId,
                    gameStatus = GameStatus.INPROGRESS,
                    playerOne = Pair("Alex", PlayerStatus.ALIVE),
                    playerTwo = Pair("Kent", PlayerStatus.ALIVE)
                )
            )
        }
    }

    fun updateGameData(model: GameModel) {
        GameData.saveGameModel(model)
    }

    fun checkForRemainingPlayers() {
        gameModel?.apply {
            if (luckyNumber.contains(currentBullet.toString())) {
                for (participant in offlineParticipants) {
                    if (currentPlayer == participant.first) {
                        val resId = resources.getIdentifier("sceleton", "drawable", requireContext().packageName)
                        if (participant.first == playerOne.first) {
                            binding.playerOne.setImageResource(resId)
                            playerOne = Pair(playerOne.first, PlayerStatus.DEAD)
                        } else {
                            binding.playerTwo.setImageResource(resId)
                            playerTwo = Pair(playerTwo.first, PlayerStatus.DEAD)
                        }
                        aliveCount--
                    }
                }
            }
            if (aliveCount == 1) {
                for (participant in offlineParticipants) {
                    if (participant.first == playerOne.first) {
                        if (playerOne.second == PlayerStatus.ALIVE) {
                            winner = participant.first
                        }
                    } else {
                        if (playerTwo.second == PlayerStatus.ALIVE) {
                            winner = participant.first
                        }
                    }
                }
                gameStatus = GameStatus.FINISHED
            }
            updateGameData(this)
        }
    }

    override fun onClick(v: View?) {
        gameModel?.apply {
            if (gameStatus != GameStatus.INPROGRESS) {
                Toast.makeText(requireContext().applicationContext, "Game not started", Toast.LENGTH_SHORT).show()
                return
            }
            //game is in progress
            addBullet()
            currentPlayer =
                if (currentPlayer == offlineParticipants[0].first) offlineParticipants[1].first else offlineParticipants[0].first
            pullTheTrigger()
            checkForRemainingPlayers()
            updateGameData(this)

        }
    }
}
package com.hfad.klientutvecklingsprojekt.gamestart

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.board.BoardData
import com.hfad.klientutvecklingsprojekt.board.BoardModel
import com.hfad.klientutvecklingsprojekt.databinding.FragmentGameStartBinding
import kotlin.random.Random
import kotlin.random.nextInt
/**
 *
 * GameStartFragment:
 *
 * används för att skapa och gå med i online spel
 *
 * @author William
 *
 */
class GameStartFragment : Fragment() {
    private var _binding: FragmentGameStartBinding? = null
    private val binding get()  = _binding!!
    private lateinit var view : ConstraintLayout
    private val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Game Data")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameStartBinding.inflate(inflater, container, false)
        view = binding.root

        binding.createOnlineGameBtn.setOnClickListener {
            createOnlineGame()
        }

        binding.joinOnlineGameBtn.setOnClickListener() {
            joinOnlineGame()
        }
        return view;
    }
    //Skapar speldatan för hela spelet
    fun createOnlineGame(){
        var gameId = (Random.nextInt(1000..9999)).toString()
        GameData.saveGameModel(
            GameModel(
                gameID = gameId,
                status = Progress.INPROGRESS,
                takenPosition = mutableMapOf(
                    "white" to CharacterStatus.FREE,
                    "red" to CharacterStatus.FREE,
                    "blue" to CharacterStatus.FREE,
                    "green" to CharacterStatus.FREE,
                    "yellow" to CharacterStatus.FREE
                )
            )
        )
        BoardData.saveBoardModel(
            BoardModel(
                gameID = gameId,
                currentPlayerID = "",
                playerCount = 0,
                randomVal = -1
            )
        )
        joinLobby()
    }
    //Går med i spel om spelet existerar
    fun joinOnlineGame() {
        var gameID = binding.gameIdInput.text.toString()
        //  Checks if the user wrote anything
        if (gameID.isEmpty()) {
            binding.gameIdInput.error = (getText(R.string.please_enter_game_id))
            return
        }
        println("CHECKING DIFSN GAME ID " + gameID + " " + myRef.child(gameID).get().toString())
        myRef.child(gameID).get().addOnSuccessListener {
            val model = it?.getValue(GameModel::class.java)
            //  Ser om spelet existerar
            if (model == null) {
                binding.gameIdInput.error = (getText(R.string.please_enter_valid_game_id))
            } else {
                GameData.saveGameModel(model)
                model?.apply {
                    //  Should not check status, because that only check the current player not the
                    //  game/lobby.
                    if (status != Progress.FINISHED) {
                        joinLobby()
                    } else {
                        binding.gameIdInput.error = (getText(R.string.game_is_full))
                    }
                }
            }
        }.addOnFailureListener {
            binding.gameIdInput.error = (getText(R.string.please_enter_valid_game_id))
        }
    }
    //navigerar till skapandet av karaktärer
    fun joinLobby() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        view.findNavController().navigate(R.id.action_gameStartFragment_to_playerInfoFragment)
    }
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
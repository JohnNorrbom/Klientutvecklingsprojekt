package com.hfad.klientutvecklingsprojekt.gamestart

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentGameStartBinding
import kotlin.random.Random
import kotlin.random.nextInt

class GameStartFragment : Fragment() {
    private var _binding: FragmentGameStartBinding? = null
    private val binding get()  = _binding!!
    private lateinit var view : LinearLayout
    private val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Game Data")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameStartBinding.inflate(inflater, container, false)
        view = binding.root

        binding.playOfflineBtn.setOnClickListener {
            createOfflinGame()
        }

        binding.createOnlineGameBtn.setOnClickListener {
            createOnlineGame()
        }

        binding.joinOnlineGameBtn.setOnClickListener() {
            joinOnlineGame()
        }
        return view;
    }
    //  THIS WILL NOT BE INCLUDED IN THE FINAL PRODUCT. FOCUS ON ONLINE PLEASE
    fun createOfflinGame(){
        GameData.saveGameModel(
            GameModel(
                gameID = "-1",
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
        joinLobby()
    }
    fun createOnlineGame(){
        GameData.saveGameModel(
            GameModel(
                gameID = (Random.nextInt(1000..9999)).toString(),
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
        joinLobby()
    }

    fun joinOnlineGame() {
        var gameID = binding.gameIdInput.text.toString()
        //  Checks if the user wrote anything
        if (gameID.isEmpty()) {
            binding.gameIdInput.error = (getText(R.string.please_enter_game_id))
            return
        }
        println("CHECKING DIFSN GAME ID " + gameID + " " + myRef.child(gameID).get().toString())
        //  I guess we are talking with the database here?
        myRef.child(gameID).get().addOnSuccessListener {
            val model = it?.getValue(GameModel::class.java)
            //  Checks if the server has that gameID
            if (model == null) {
                Log.d("Om null", "den är null")
                binding.gameIdInput.error = (getText(R.string.please_enter_valid_game_id))
            } else {
                //  Create array of colors to compare them with the colors in the lobby to see
                //  which of them are are taken
                val color = listOf("white", "red", "blue", "green", "yellow")
                GameData.saveGameModel(model)
                Log.d("Om success","model: ${model}")
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
    //  Joins the lobby/Goes to PlayerInfoFragment/Character creation
    fun joinLobby() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        view.findNavController().navigate(R.id.action_gameStartFragment_to_playerInfoFragment)
    }
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
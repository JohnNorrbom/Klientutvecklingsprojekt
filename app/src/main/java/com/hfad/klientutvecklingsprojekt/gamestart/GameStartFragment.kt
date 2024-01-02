package com.hfad.klientutvecklingsprojekt.gamestart

import android.content.pm.ActivityInfo
import android.nfc.Tag
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
import com.hfad.klientutvecklingsprojekt.gavleroulette.PlayerStatus
import com.hfad.klientutvecklingsprojekt.lobby.LobbyData
import com.hfad.klientutvecklingsprojekt.lobby.LobbyModel
import com.hfad.klientutvecklingsprojekt.playerinfo.CharacterStatus
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel
import com.hfad.klientutvecklingsprojekt.playerinfo.Progress
import kotlin.random.Random
import kotlin.random.nextInt

class GameStartFragment : Fragment() {
    private var _binding: FragmentGameStartBinding? = null
    private val binding get()  = _binding!!
    private lateinit var view : LinearLayout
    private var playerModel : PlayerModel? = null
    private val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
   private val myRef = database.getReference("Space Party")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameStartBinding.inflate(inflater,container,false)
        view = binding.root

        binding.playOfflineBtn.setOnClickListener{
            createOfflinGame()
        }

        binding.createOnlineGameBtn.setOnClickListener{
            createOnlineGame()
        }

        binding.joinOnlineGameBtn.setOnClickListener(){
            joinOnlineGame()
        }

        return view;
    }

    fun createOfflinGame(){
        PlayerData.savePlayerModel(
            PlayerModel(
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
        startGame()
    }
    fun createOnlineGame(){
        PlayerData.savePlayerModel(
            PlayerModel(
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
        startGame()
    }

    fun joinOnlineGame(){
        var gameID = binding.gameIdInput.text.toString()
        if(gameID.isEmpty()){
            binding.gameIdInput.error=(getText(R.string.please_enter_game_id))
            return
        }

        myRef.child(gameID).get().addOnSuccessListener {
            val model = it?.getValue(PlayerModel::class.java)

            if (model == null){
                Log.d("Om null","den är null")
                binding.gameIdInput.error=(getText(R.string.please_enter_valid_game_id))
            }else {
                val color = listOf("white","red","blue","green","yellow")
                PlayerData.savePlayerModel(model)
                Log.d("Om success","model: ${model}")
                model?.apply {
                    if (status != Progress.FINISHED){
                        var count = 0
                        for (i in 0 until color.size){
                            if (takenPosition?.get(color[i]) == CharacterStatus.TAKEN){
                                count ++
                            }
                        }
                        if (count == 5) {
                            PlayerData.savePlayerModel(
                                PlayerModel(
                                    status = Progress.FINISHED
                                )
                            )
                        }
                        startGame()
                    }else{
                        binding.gameIdInput.error=(getText(R.string.game_is_full))
                    }
                }
            }
        }.addOnFailureListener{
            binding.gameIdInput.error=(getText(R.string.please_enter_valid_game_id))
        }

    }

    fun startGame(){
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        view.findNavController().navigate(R.id.action_gameStartFragment_to_playerInfoFragment)
    }
}
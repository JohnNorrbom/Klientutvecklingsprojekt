package com.hfad.klientutvecklingsprojekt.gamestart

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.findNavController
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentGameStartBinding
import com.hfad.klientutvecklingsprojekt.lobby.LobbyData
import com.hfad.klientutvecklingsprojekt.lobby.LobbyModel
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel
import com.hfad.klientutvecklingsprojekt.playerinfo.Progress
import kotlin.random.Random
import kotlin.random.nextInt

class GameStartFragment : Fragment() {
    private var _binding: FragmentGameStartBinding? = null
    private val binding get()  = _binding!!
    private lateinit var view : LinearLayout

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
                gameID = "-1"
            )
        )
        startGame()
    }
    fun createOnlineGame(){
        PlayerData.savePlayerModel(
            PlayerModel(
                gameID = (Random.nextInt(1000..9999)).toString()
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
    }

    fun startGame(){
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        view.findNavController().navigate(R.id.action_gameStartFragment_to_playerInfoFragment)
    }
}
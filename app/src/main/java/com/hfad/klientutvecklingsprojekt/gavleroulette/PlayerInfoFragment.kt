package com.hfad.klientutvecklingsprojekt.gavleroulette

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hfad.klientutvecklingsprojekt.databinding.FragmentPlayerInfoBinding

class PlayerInfoFragment : Fragment() {
    private var _binding: FragmentPlayerInfoBinding? = null
    private val binding get()  = _binding!!
    private var gameModel : GameModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerInfoBinding.inflate(inflater,container,false)
        val view = binding.root
        var nickName : String = binding.nicknameInput.text.toString()
        var status : PlayerStatus = PlayerStatus.ALIVE
        var player : Pair<String, PlayerStatus> = Pair(nickName,status)
        gameModel?.apply {
            onlineParticipants = mutableListOf(player)
            updateGameData(this)
        }
        return view;
    }

    fun updateGameData(model: GameModel){
        GameData.saveGameModel(model)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
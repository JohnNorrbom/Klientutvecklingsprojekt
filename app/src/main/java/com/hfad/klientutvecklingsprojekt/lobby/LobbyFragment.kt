package com.hfad.klientutvecklingsprojekt.lobby

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.navigation.findNavController
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentLobbyBinding
import com.hfad.klientutvecklingsprojekt.databinding.FragmentPlayerInfoBinding
import com.hfad.klientutvecklingsprojekt.gavleroulette.GameData
import com.hfad.klientutvecklingsprojekt.gavleroulette.GameModel
import com.hfad.klientutvecklingsprojekt.gavleroulette.GameStatus
import com.hfad.klientutvecklingsprojekt.playerinfo.CharacterStatus
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel
import kotlin.random.Random


class LobbyFragment : Fragment() {
    private var _binding: FragmentLobbyBinding? = null
    private val binding get()  = _binding!!
    private var lobbyModel : LobbyModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLobbyBinding.inflate(inflater,container,false)
        val view = binding.root
        binding.testBtn.setOnClickListener{
            addPlayer()
        }
        LobbyData.lobbyModel.observe(this){
            lobbyModel = it
            init()
        }

        return view;
    }

    fun addPlayer(){
        lobbyModel?.apply {
            participants.add(Pair(nickname,color))
            updateLobbyData(this)
        }
    }
    fun init() {
        lobbyModel?.apply {
            for(i in 0 until participants.size){
                var resId = resources.getIdentifier(
                    "astro_${participants[i].second}",
                    "drawable",
                    requireContext().packageName
                )
                val playerId = resources.getIdentifier(
                    "player_${i+1}",
                    "id",
                    requireContext().packageName
                )
                val playerImageView =
                    binding.root.findViewById<ImageView>(playerId)
                playerImageView.setImageResource(resId)
                playerImageView.visibility = View.VISIBLE

                val playerTextId = resources.getIdentifier(
                    "player_${i+1}_text",
                    "id",
                    requireContext().packageName
                )
                val playerTextView =
                    binding.root.findViewById<TextView>(playerTextId)
                playerTextView.text = participants[i].first
                playerTextView.visibility = View.VISIBLE
            }
        }
    }


    fun updateLobbyData(model: LobbyModel){
        LobbyData.saveLobbyModel(model)
    }

}

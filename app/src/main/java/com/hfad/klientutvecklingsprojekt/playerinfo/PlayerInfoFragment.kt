package com.hfad.klientutvecklingsprojekt.playerinfo

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import androidx.navigation.findNavController
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentPlayerInfoBinding
import com.hfad.klientutvecklingsprojekt.lobby.LobbyData
import com.hfad.klientutvecklingsprojekt.lobby.LobbyModel
import kotlin.random.Random


class PlayerInfoFragment : Fragment() {
    private var _binding: FragmentPlayerInfoBinding? = null
    private val binding get()  = _binding!!
    private var playerModel : PlayerModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerInfoBinding.inflate(inflater,container,false)
        val view = binding.root
       binding.confirmBtn.setOnClickListener{
           setPlayerInfo()
           val playerId = Random.nextInt(9999).toString()
           val playerName = binding.nicknameInput.text.toString()
           var playerColor = ""
           playerModel?.apply {
               playerColor = takenPosition[takenPosition.size-1].first
           }
           LobbyData.saveLobbyModel(
               LobbyModel(
                   nickname = playerName,
                   color = playerColor
               )
           )
           activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            //  Crashes after executing the line below
            view.findNavController().navigate(R.id.action_playerInfoFragment_to_lobbyFragment)
        }

        PlayerData.playerModel.observe(this){
            playerModel = it
            setUI()
        }

        return view
    }
    fun setUI(){
        playerModel?.apply {
            for (i in 0 until takenPosition.size){
                if(takenPosition[i].second == CharacterStatus.TAKEN){
                    val resId = resources.getIdentifier("astro_${takenPosition[i].first}_taken", "drawable", requireContext().packageName)
                    val astroId = resources.getIdentifier(
                        "astro_${takenPosition[i].first}",
                        "id",
                        requireContext().packageName
                    )
                    val characterImageView =
                        binding.root.findViewById<ImageView>(astroId)
                    characterImageView.setImageResource(resId)

                    val radioId = resources.getIdentifier(
                        "radio_btn_${takenPosition[i].first}",
                        "id",
                        requireContext().packageName
                    )
                    val radioBtn = binding.root.findViewById<RadioButton>(radioId)
                        radioBtn.visibility = View.INVISIBLE
                }
            }
        }
    }
fun setPlayerInfo(){
    val colors = mutableListOf(
        Pair("white", binding.radioBtnWhite)
        ,Pair("blue",binding.radioBtnBlue),
        Pair("red",binding.radioBtnRed),
        Pair("green",binding.radioBtnGreen),
        Pair("yellow",binding.radioBtnYellow)
    )
    var characterColor = ""
    for (color in colors){
        if (color.second.isChecked){
            playerModel?.apply {
                takenPosition.add(Pair(color.first,CharacterStatus.TAKEN))
                updatePlayerData(this)
            }
        }

    }

}
    fun updatePlayerData(model: PlayerModel){
        PlayerData.savePlayerModel(model)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
package com.hfad.klientutvecklingsprojekt.soccer

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentSoccerBinding
import com.hfad.klientutvecklingsprojekt.databinding.FragmentSoccerChooseBinding
import com.hfad.klientutvecklingsprojekt.gamestart.CharacterStatus
import com.hfad.klientutvecklingsprojekt.gamestart.GameData
import com.hfad.klientutvecklingsprojekt.gamestart.GameModel
import com.hfad.klientutvecklingsprojekt.gamestart.Progress
import kotlin.random.Random
import kotlin.random.nextInt


class SoccerChooseFragment : Fragment() {

    private var mediaPlayer: MediaPlayer? = null

    private var _binding: FragmentSoccerChooseBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoccerChooseBinding.inflate(inflater,container,false)
        binding.astroBlue.setOnClickListener {

            val gameId = createSoccerGame()
            SoccerData.setP2Color("blue",gameId)
            view?.findNavController()?.navigate(R.id.action_soccerChooseFragment_to_soccerFragment)
        }
        val view = binding.root

        return view
    }

//TODO MÅSTE ÄNDRAS
    fun createSoccerGame(): String{
        var gameId: String = Random.nextInt(1000..9999).toString()
        SoccerData.saveSoccerModel(
            SoccerModel(gameId,0,0,"","", "white","blue",false),gameId
        )
    return gameId
    }


}
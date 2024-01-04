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

    //Here you should get your color from meDataModel
    private var yourColor: String  = "white"

    //Here you should get the other colors from board/gameModel
    private var otherColors = arrayListOf("blue", "red", "yellow", "green")

    //here you should get the id from board/gameModel
    private var gameId: String = Random.nextInt(1000..9999).toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoccerChooseBinding.inflate(inflater,container,false)

        binding.astroBlue.visibility = View.GONE
        binding.astroWhite.visibility = View.GONE
        binding.astroGreen.visibility = View.GONE
        binding.astroRed.visibility = View.GONE
        binding.astroYellow.visibility = View.GONE
        for (color: String in otherColors){
            if(color == "blue"){
                binding.astroBlue.visibility = View.VISIBLE
            }
            if(color == "white"){
                binding.astroWhite.visibility = View.VISIBLE
            }
            if(color == "green"){
                binding.astroGreen.visibility = View.VISIBLE
            }
            if(color == "red"){
                binding.astroRed.visibility = View.VISIBLE
            }
            if(color == "yellow"){
                binding.astroYellow.visibility = View.VISIBLE
            }
        }

        binding.astroBlue.setOnClickListener {
            if(yourColor != "blue"){
                createSoccerGame("blue", gameId)
                view?.findNavController()?.navigate(R.id.action_soccerChooseFragment_to_soccerFragment)
            }

        }
        binding.astroRed.setOnClickListener {
            if(yourColor != "red"){
            createSoccerGame("red" , gameId)
            view?.findNavController()?.navigate(R.id.action_soccerChooseFragment_to_soccerFragment)
            }
        }
        binding.astroYellow.setOnClickListener {
            if(yourColor != "yellow"){
            createSoccerGame("yellow" , gameId)
            view?.findNavController()?.navigate(R.id.action_soccerChooseFragment_to_soccerFragment)
            }
        }
        binding.astroGreen.setOnClickListener {
            if(yourColor != "green"){
            createSoccerGame("green" , gameId)
            view?.findNavController()?.navigate(R.id.action_soccerChooseFragment_to_soccerFragment)
            }
        }
        binding.astroWhite.setOnClickListener {
            if (yourColor != "white"){
            createSoccerGame("white" , gameId)
            view?.findNavController()?.navigate(R.id.action_soccerChooseFragment_to_soccerFragment)
        }
    }
        val view = binding.root

        return view
    }

//TODO MÅSTE ÄNDRAS
    fun createSoccerGame(p2Color: String, gameId: String){
        SoccerData.saveSoccerModel(
            SoccerModel(gameId,0,0,"","", yourColor,p2Color,false)
        )
    }


}
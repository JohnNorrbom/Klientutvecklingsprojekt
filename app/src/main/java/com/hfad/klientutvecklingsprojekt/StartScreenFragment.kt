package com.hfad.klientutvecklingsprojekt

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.hfad.klientutvecklingsprojekt.databinding.FragmentStartScreenBinding
import com.hfad.klientutvecklingsprojekt.gavleroulette.GameData
import com.hfad.klientutvecklingsprojekt.gavleroulette.GameModel
import com.hfad.klientutvecklingsprojekt.gavleroulette.GameStatus
import java.util.zip.Inflater

class StartScreenFragment : Fragment() {
    private var _binding: FragmentStartScreenBinding? = null
    private val binding get()  = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStartScreenBinding.inflate(inflater,container,false)
        val view = binding.root
        //  Changes view when button is clicked
        //  board button
        binding.startButton.setOnClickListener {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            view.findNavController().navigate(R.id.action_startScreenFragment_to_boardFragment)
        }
        //  soccer button
        binding.soccerMiniGameButton.setOnClickListener {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            view.findNavController().navigate(R.id.action_startScreenFragment_to_soccerFragment)
        }
        //  stensaxpase button
        binding.stensaxpaseMiniGameButton.setOnClickListener {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            view.findNavController().navigate(R.id.action_startScreenFragment_to_stensaxpaseFragment)
        }
        //  roulette button
        binding.rouletteMiniGameButton.setOnClickListener{
            GameData.saveGameModel(
                GameModel(
                    gameStatus = GameStatus.JOINED
                )
            )
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            //  Crashes after executing the line below
            view.findNavController().navigate(R.id.action_startScreenFragment_to_gavleRouletteFragment)
        }
        return view
    }
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
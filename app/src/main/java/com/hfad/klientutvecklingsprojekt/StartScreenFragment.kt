package com.hfad.klientutvecklingsprojekt

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hfad.klientutvecklingsprojekt.databinding.FragmentStartScreenBinding
import com.hfad.klientutvecklingsprojekt.gavleroulette.RouletteData
import com.hfad.klientutvecklingsprojekt.gavleroulette.GameStatus
import com.hfad.klientutvecklingsprojekt.gavleroulette.RouletteModel


class StartScreenFragment : Fragment() {

    private var mediaPlayer: MediaPlayer? = null

    private var _binding: FragmentStartScreenBinding? = null
    private val binding get()  = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStartScreenBinding.inflate(inflater,container,false)
        val view = binding.root

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.android_song1_140bpm)
        mediaPlayer?.isLooping = true // Disable built-in looping
        mediaPlayer?.start()

        //  Changes view when button is clicked
        //  board button
        binding.startButton.setOnClickListener {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            view.findNavController().navigate(R.id.action_startScreenFragment_to_testBoardFragment)
        }
        //  soccer button
        binding.soccerMiniGameButton.setOnClickListener {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            view.findNavController().navigate(R.id.action_startScreenFragment_to_soccerFragment)
        }
        //  stensaxpase button
        binding.stensaxpaseMiniGameButton.setOnClickListener {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            view.findNavController().navigate(R.id.action_startScreenFragment_to_stenSaxPaseChooseFragment)
        }

        binding.playerCreateButton.setOnClickListener {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            view.findNavController().navigate(R.id.action_startScreenFragment_to_gameStartFragment)
        }
        //  quiz button
        binding.quizButton.setOnClickListener {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            view.findNavController().navigate(R.id.action_startScreenFragment_to_quizFragment)
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
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
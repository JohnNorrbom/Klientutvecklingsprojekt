package com.hfad.klientutvecklingsprojekt

import android.R
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.hfad.klientutvecklingsprojekt.databinding.FragmentBoardBinding
import com.hfad.klientutvecklingsprojekt.databinding.FragmentSoccerBinding
import com.hfad.klientutvecklingsprojekt.databinding.FragmentStartScreenBinding
import com.hfad.klientutvecklingsprojekt.lobby.LobbyFragmentArgs
import com.hfad.klientutvecklingsprojekt.lobby.LobbyFragmentDirections


class BoardFragment : Fragment() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var gameView: GameView // Replace with your custom game view
    private lateinit var gameLoopThread: Thread
    private var isRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mediaPlayer = MediaPlayer.create(requireContext(), com.hfad.klientutvecklingsprojekt.R.raw.android_song2_140bpm)
        mediaPlayer?.isLooping = true // Disable built-in looping
        mediaPlayer?.start()



        // For safeargs
        val gameID = BoardFragmentArgs.fromBundle(requireArguments()).gameID

        gameView = GameView(requireContext(), gameID) // Replace with your custom game view

        return gameView
    }
    override fun onResume() {
        super.onResume()
        startGameLoop()
    }
    override fun onPause() {
        stopGameLoop()
        super.onPause()
    }
    private fun startGameLoop() {
        isRunning = true
        gameLoopThread = Thread {
            while (isRunning) {
                updateGame()
                renderGame()
                try {
                    Thread.sleep(16) // Adjust as needed for your desired frame rate
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        gameLoopThread.start()
    }
    private fun stopGameLoop() {
        isRunning = false
        try {
            gameLoopThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
    private fun updateGame() {
        // Update game logic here

    }
    private fun renderGame() {
        Handler(Looper.getMainLooper()).post {
            // Render game graphics on the UI thread
            gameView.invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}


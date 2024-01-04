package com.hfad.klientutvecklingsprojekt.board

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel


class BoardFragment : Fragment() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var gameView: GameView // Replace with your custom game view
    private lateinit var gameLoopThread: Thread
    private var isRunning = false
    private var playerModel : PlayerModel? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        PlayerData.playerModel.observe(this) {
            playerModel = it
        }

        gameView = GameView(requireContext()) // Replace with your custom game view
        mediaPlayer = MediaPlayer.create(requireContext(),
            com.hfad.klientutvecklingsprojekt.R.raw.android_song2_140bpm
        )
        mediaPlayer?.isLooping = true // Disable built-in looping
        mediaPlayer?.start()

        gameView = GameView(requireContext()) // Replace with your custom game view
        gameView.setPlayerModel(playerModel)

        return gameView
    }
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
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


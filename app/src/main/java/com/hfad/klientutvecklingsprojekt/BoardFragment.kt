package com.hfad.klientutvecklingsprojekt

import android.R
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment


class BoardFragment : Fragment() {
    private lateinit var gameView: GameView // Replace with your custom game view
    private lateinit var gameLoopThread: Thread
    private var isRunning = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gameView = GameView(requireContext()) // Replace with your custom game view

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
}


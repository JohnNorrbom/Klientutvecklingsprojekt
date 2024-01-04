package com.hfad.klientutvecklingsprojekt.board

import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentBoardBinding
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel
import com.hfad.klientutvecklingsprojekt.stensaxpase.StenSaxPaseData
import com.hfad.klientutvecklingsprojekt.stensaxpase.StenSaxPaseModel
import kotlin.random.Random


class BoardFragment : Fragment() {

    private var currentImageViewIndex: Int = 0
    private lateinit var view : ConstraintLayout
    private var player: ImageView? = null
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var gameLoopThread: Thread
    private var isRunning = false
    private var playerModel : PlayerModel? = null
    private var meModel : MeModel? = null
    var currentGameID = ""
    var currentPlayerID = ""
    private val database =
        Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Player Data")
    var playerRef = database.getReference("Player Data").child("")
    var playersRef = playerRef.child("players")
    lateinit var _binding: FragmentBoardBinding
    private val binding get() = _binding!!
    private var navigateCallback: (() -> Unit)? = null
    val maxStreams = 5 // Number of simultaneous sounds
    val soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        SoundPool.Builder().setMaxStreams(maxStreams).build()
    } else {
        SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0)
    }
    val soundId = soundPool.load(requireContext(), R.raw.dice_sound, 1)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)
        view = binding.root
        PlayerData.playerModel.observe(this) {
            playerModel = it
        }
        //den här
        MeData.meModel.observe(this) { meModel ->
            meModel?.let {
                this@BoardFragment.meModel = it
            } ?: run {
                // Handle the case when meModel is null
                Log.e("LobbyFragment", "meModel is null")
            }
        }
        println("TESTESTETSETSET " + meModel)
        mediaPlayer = MediaPlayer.create(requireContext(),
            R.raw.android_song2_140bpm
        )
        mediaPlayer?.isLooping = true // Disable built-in looping
        mediaPlayer?.start()

        MeData.meModel.observe(this) { meModel ->
            meModel?.let {
                this@BoardFragment.meModel = it
                setText()
            } ?: run {
                // Handle the case when meModel is null
                Log.e("LobbyFragment", "meModel is null")
            }
        }
        playerRef = database.getReference("Player Data").child(currentGameID)
        playersRef = playerRef.child("players")
        //  POST gör så att man kör på mainthread

        binding.playerBlue.visibility = View.GONE
        binding.playerWhite.visibility = View.GONE
        binding.playerRed.visibility = View.GONE
        binding.playerYellow.visibility = View.GONE
        binding.playerGreen.visibility = View.GONE

        paintPlayers()
        val dice = binding.diceButton
        //  DICE BUTTON LISTENER
        dice?.setOnClickListener {
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
            var randomInt = Random.nextInt(6) + 1
            var destination = "dice" + randomInt
            val resourceId = resources.getIdentifier(
                destination,
                "drawable",
                "com.hfad.klientutvecklingsprojekt"
            )
            binding.diceButton?.setImageResource(resourceId)
            currentImageViewIndex += randomInt

            playerModel?.apply {
                position = currentImageViewIndex
            }

            val tile = resources.getIdentifier(
                "tile${(currentImageViewIndex % 20) + 1}",
                "id",
                requireContext().packageName
            )
            val tileImage = binding.root.findViewById<ImageView>(tile)
            movePlayer(tileImage)
        }

        //  ONE STEP BUTTON LISTENER
        val mB: Button? = _binding.moveButton
        mB?.setOnClickListener {
            // Increment the index
            currentImageViewIndex++
            // If index is greater than the array size, reset to 0
            val tile = resources.getIdentifier(
                "tile${(currentImageViewIndex % 20) + 1}",
                "id",
                requireContext().packageName
            )
            val tileImage = binding.root.findViewById<ImageView>(tile)
            println("current tileImage " + tileImage + " current tile" + tile)
            movePlayer(tileImage)
        }
        return view
    }
    fun setText() {
        //den här
        currentGameID = meModel?.gameID ?: ""
        currentPlayerID = meModel?.playerID ?: ""
        Log.d("meModelView", "playerID: ${currentPlayerID} GameID: ${currentGameID}")
    }
    fun paintPlayers() {
        var color = ""
        Log.d("color", "${currentGameID}")
        myRef.child(currentGameID).child("players").child(currentPlayerID).get()
            .addOnSuccessListener {
                val snapshot = it
                Log.d("color", "${snapshot.child("color").value}")
                color = snapshot.child("color").value.toString()

                if (color == "blue") {
                    Log.d("color", "färgen är ${color}")
                    binding.playerBlue.visibility = View.VISIBLE
                    player = binding.playerBlue

                }
                if (color == "red") {
                    Log.d("color", "färgen är ${color}")
                    binding.playerBlue.visibility = View.VISIBLE
                    player = binding.playerRed
                }
                if (color == "green") {
                    Log.d("color", "färgen är ${color}")
                    binding.playerBlue.visibility = View.VISIBLE
                    player = binding.playerGreen
                }
                if (color == "yellow") {
                    Log.d("color", "färgen är ${color}")
                    binding.playerBlue.visibility = View.VISIBLE
                    player = binding.playerYellow
                }
                if (color == "white") {
                    Log.d("color", "färgen är ${color}")
                    binding.playerBlue.visibility = View.VISIBLE
                    player = binding.playerWhite
                }

            }
    }
    private fun movePlayer(targetedImageView: ImageView) {
        // Update constraints to move player to next tile
        val layoutParams = player?.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToTop = targetedImageView.id
        layoutParams.endToEnd = targetedImageView.id
        player!!.layoutParams = layoutParams
        //  Check what type of tile player is standing on
        if (targetedImageView.tag == _binding.tile2.tag) {
            println("Plus 1")
        } else if (targetedImageView.tag == _binding.tile3.tag) {
            println("Plus 2")
        } else if (targetedImageView.tag == _binding.tile4.tag) {
            println("Plus 3")
        } else if (targetedImageView.tag == _binding.tile5.tag) {
            println("Minus 5")
        } else if (targetedImageView.tag == _binding.tile6.tag) {
            //  Change to portrait view
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            //  Pick random game
            val randomVal = Random.nextInt(4)

            if (randomVal == 0) {
                println("STEN SAX PÅSE")
                var id = meModel?.gameID
                Log.d("Game ID", "${id}")
                Log.d("Player ID", "${meModel?.playerID}")
                StenSaxPaseData.saveGameModel(
                    StenSaxPaseModel(
                        gameID = id,
                        status = false,
                    )
                )
//                view?.findNavController()?.navigate(R.id.action_boardFragment_to_stensaxpaseFragment)
            } else if (randomVal == 1) {
                println("SOCCER GAME FERDINAND")
//                view?.findNavController()?.navigate(R.id.action_boardFragment_to_soccerChooseFragment)
            } else if (randomVal == 2) {
                println("QUIZ GAME PONTUS")
//                view?.findNavController()?.navigate(R.id.action_boardFragment_to_quizFragment)
            } else {
                println("ROULETTE WILLIAM")
//                view?.findNavController()?.navigate(R.id.action_boardFragment_to_gavleRouletteFragment)
            }
        }
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}


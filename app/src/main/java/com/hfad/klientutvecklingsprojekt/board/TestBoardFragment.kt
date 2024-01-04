package com.hfad.klientutvecklingsprojekt.board

import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentGameStartBinding
import com.hfad.klientutvecklingsprojekt.databinding.FragmentTestBoardBinding
import com.hfad.klientutvecklingsprojekt.gamestart.GameData.gameModel
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData.gameID
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData.playerModel
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel
import com.hfad.klientutvecklingsprojekt.stensaxpase.StenSaxPaseData
import com.hfad.klientutvecklingsprojekt.stensaxpase.StenSaxPaseModel
import kotlin.random.Random
// TODO GIVE PLAYING PLAYER CORRECT COLOR
// TODO GIVE EVERYONE CORRECT COLOR
// TODO MAKE PLAYERS TAKE TURNS
class TestBoardFragment : Fragment() {
    //  VIEWBINDING
    private var _binding: FragmentTestBoardBinding? = null
    private val binding get() = _binding!!
    private lateinit var view: ConstraintLayout

    //  DATABASE
    private val database =
        Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Player Data")
    private var playerRef = database.getReference("Player Data").child(gameID)
    private var playersRef = playerRef.child("players")

    //  meModel
    private var currentGameID = ""
    private var currentPlayerID = ""
    private var meModel: MeModel? = null

    // PLAYER
    private var playerModel: PlayerModel? = null
    private lateinit var player: ImageView
    private var currentImageViewIndex: Int = 0

    // BG MUSIC
    private var mediaPlayer: MediaPlayer? = null

    //  DICE SOUND
    private val maxStreams = 5 // Number of simultaneous sounds
    private var soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        SoundPool.Builder().setMaxStreams(maxStreams).build()
    } else {
        SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0)
    }

    //    val soundId = soundPool.load(context, R.raw.dice_sound, 1)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTestBoardBinding.inflate(inflater, container, false)
        view = binding.root
        PlayerData.playerModel.observe(this) {
            playerModel = it
        }
        mediaPlayer = MediaPlayer.create(
            requireContext(),
            R.raw.android_song2_140bpm
        )
        mediaPlayer?.isLooping = true // Disable built-in looping
        mediaPlayer?.start()

        MeData.meModel.observe(this) { meModel ->
            meModel?.let {
                this@TestBoardFragment.meModel = it
                setText()
            } ?: run {
                // Handle the case when meModel is null
                Log.e("LobbyFragment", "meModel is null")
            }
        }


        Log.d("color", "EFTER SETTEXT I ONCREATEVIEW playerID: ${currentPlayerID} GameID: ${currentGameID}")

        //  POST gör så att man kör på mainthread

        binding.playerBlue.visibility = View.GONE
        binding.playerWhite.visibility = View.GONE
        binding.playerRed.visibility = View.GONE
        binding.playerYellow.visibility = View.GONE
        binding.playerGreen.visibility = View.GONE


        player = binding.playerBlue
        binding.playerBlue.visibility = View.VISIBLE

        diceButton()
        // Inflate the layout for this fragment
        return view
    }
    private fun setText() {
        meModel?.apply{
            currentGameID = gameID ?: ""
            currentPlayerID = playerID ?: ""
            Log.d("color", "playerID: ${currentPlayerID} GameID: ${currentGameID}")
        }
        playerRef = database.getReference("Player Data").child(currentGameID)
        playersRef = playerRef.child("players")
        assertColorAndName()
        paintPlayer()
        Log.d("color", "UTANFÖR APPLY playerID: ${currentPlayerID} GameID: ${currentGameID}")
    }
    private fun paintPlayer() {
        var color = ""
        Log.d("color", "playerID: ${currentPlayerID} GameID: ${currentGameID}")
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


    private fun assertColorAndName(){
        playersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (playerSnapshot in dataSnapshot.children) {
                    val playerId = playerSnapshot.child("playerID").value
                    val playerColor = playerSnapshot.child("color").value
                    val playerName = playerSnapshot.child("nickname").value
                    val playerPosition = playerSnapshot.child("position").value
                    val playerScore = playerSnapshot.child("score").value
                    println(playerId)
                    println(playerColor)
                    println(playerName)
                    println(playerPosition)
                    println(playerScore)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Failed to fetch player data: ${databaseError.message}")
            }
        })
    }

    private fun movePlayer(targetedImageView: ImageView) {
        // Update constraints to move player to next tile
        val layoutParams = player?.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToTop = targetedImageView.id
        layoutParams.endToEnd = targetedImageView.id
        player!!.layoutParams = layoutParams
        //  Check what type of tile player is standing on
        if (targetedImageView.tag == _binding?.tile2?.tag) {
            println("Plus 1")
        } else if (targetedImageView.tag == _binding?.tile3?.tag) {
            println("Plus 2")
        } else if (targetedImageView.tag == _binding?.tile4?.tag) {
            println("Plus 3")
        } else if (targetedImageView.tag == _binding?.tile5?.tag) {
            println("Minus 5")
        } else if (targetedImageView.tag == _binding?.tile6?.tag) {
            //  Change to portrait view
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            //  Pick random game
            val randomVal = Random.nextInt(4)

            if (randomVal == 0) {
//                println("STEN SAX PÅSE")
//                var id = meModel?.gameID
//                Log.d("Game ID", "${id}")
//                Log.d("Player ID", "${meModel?.playerID}")
//                StenSaxPaseData.saveGameModel(
//                    StenSaxPaseModel(
//                        gameID = id,
//                        status = false,
//                    )
//                )
//                view?.findNavController()?.navigate(R.id.action_testBoardFragment_to_stensaxpaseFragment)
            } else if (randomVal == 1) {
                println("SOCCER GAME FERDINAND")
                view?.findNavController()
                    ?.navigate(R.id.action_testBoardFragment_to_soccerChooseFragment)
            } else if (randomVal == 2) {
                println("QUIZ GAME PONTUS")
                view?.findNavController()?.navigate(R.id.action_testBoardFragment_to_quizFragment)
            } else {
                println("ROULETTE WILLIAM")
                view?.findNavController()
                    ?.navigate(R.id.action_testBoardFragment_to_gavleRouletteFragment)
            }
        }
    }

    fun diceButton() {
        //  DICE BUTTON
        val dice = binding.diceButton
        //  DICE BUTTON LISTENER
        dice?.setOnClickListener {
//            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
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
    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool?.release()
        soundPool = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
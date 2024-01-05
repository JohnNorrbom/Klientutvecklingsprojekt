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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentTestBoardBinding
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData.gameID
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel
import kotlinx.coroutines.launch
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
        diceButton()
        // Inflate the layout for this fragment
        return view
    }
    private fun setText() {
        meModel?.apply{
            currentGameID = gameID ?: ""
            currentPlayerID = playerID ?: ""
            playerRef = database.getReference("Player Data").child(currentGameID)
            playersRef = playerRef.child("players")
            binding.playerBlue.visibility = View.GONE
            binding.playerWhite.visibility = View.GONE
            binding.playerRed.visibility = View.GONE
            binding.playerYellow.visibility = View.GONE
            binding.playerGreen.visibility = View.GONE
        }
        paintPlayers()
    }
    /*
    this also calls setplayeronrightposition. and is thought to be called everytime something happens
     */
    private fun paintPlayers() {
        myRef.child(currentGameID).child("players").get()
            .addOnSuccessListener { dataSnapshot ->
                dataSnapshot.children.forEach { playerSnapshot ->
                    val color = playerSnapshot.child("color").value.toString()
                    val position = Integer.valueOf(playerSnapshot.child("position").value.toString())

                    println(position)

                    Log.d("paintPlayers", "Player color: $color")
                    val imageView = when (color) {
                        "blue" -> binding.playerBlue
                        "red" -> binding.playerRed
                        "green" -> binding.playerGreen
                        "yellow" -> binding.playerYellow
                        "white" -> binding.playerWhite
                        else -> null // Handle any other colors if needed
                    }

                    imageView?.let { view ->
                        //make player imageView visible
                        view.visibility = View.VISIBLE


                        //take player imageView same pos as corresponding tile
                        val tileId = position % 20 + 1
                        val tileName = "tile$tileId"

                        var tile = when (tileName){
                            "tile1" -> binding.tile1
                            "tile2" -> binding.tile2
                            "tile3" -> binding.tile3
                            "tile4" -> binding.tile4
                            "tile5" -> binding.tile5
                            "tile6" -> binding.tile6
                            "tile7" -> binding.tile7
                            "tile8" -> binding.tile8
                            "tile9" -> binding.tile9
                            "tile10" -> binding.tile10
                            "tile11" -> binding.tile11
                            "tile12" -> binding.tile12
                            "tile13" -> binding.tile13
                            "tile14" -> binding.tile14
                            "tile15" -> binding.tile15
                            "tile16" -> binding.tile16
                            "tile17" -> binding.tile17
                            "tile18" -> binding.tile18
                            "tile19" -> binding.tile19
                            "tile20" -> binding.tile20
                            else -> null
                        }

                        if (tile != null){
                            val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
                            layoutParams.topToTop = tile.id
                            layoutParams.endToEnd = tile.id
                            view.layoutParams = layoutParams
                            println(layoutParams)


                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("paintPlayers", "Error fetching players", exception)
            }
    }


    private fun movePlayer(newPosition: Int){

    }
/*
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
                println("STEN SAX PÅSE")
                view?.findNavController()?.navigate(R.id.action_testBoardFragment_to_stensaxpaseFragment)
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
    */


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
                playersRef.child(currentPlayerID).child("position").setValue(position)
            }
            paintPlayers()
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
package com.hfad.klientutvecklingsprojekt.board

import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentBoardBinding
import kotlin.random.Random

class GameView : ConstraintLayout {
    private lateinit var player: ImageView
    private var currentImageViewIndex: Int = 0
    lateinit var view: ConstraintLayout
    lateinit var _binding: FragmentBoardBinding
    private val database =
        Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Space Party")
    private val binding get() = _binding!!
    private var navigateCallback: (() -> Unit)? = null


    //soundPool for dice
    val maxStreams = 5 // Number of simultaneous sounds
    val soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        SoundPool.Builder().setMaxStreams(maxStreams).build()
    } else {
        SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0)
    }
    val soundId = soundPool.load(context, R.raw.dice_sound, 1)

    //random generator
    var random: Random = Random(6)

    fun setNavigateCallback(callback: () -> Unit) {
        this.navigateCallback = callback
    }

    // Constructors for creating the view programmatically
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context)
    }

    private fun init(context: Context) {
        _binding = FragmentBoardBinding.inflate(LayoutInflater.from(context), this, true)
        view = binding.root
        // Now you can access the views using the binding
        player = _binding.player1

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
            val tile = resources.getIdentifier(
                "tile${currentImageViewIndex%20}",
                "id",
                context.packageName
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
                "tile${currentImageViewIndex%20}",
                "id",
                context.packageName
            )
            val tileImage = binding.root.findViewById<ImageView>(tile)
            movePlayer(tileImage)
        }
    }

    private fun movePlayer(targetedImageView: ImageView) {
        // Update constraints to move player to next tile
        val layoutParams = player.layoutParams as LayoutParams
        layoutParams.topToTop = targetedImageView.id
        layoutParams.endToEnd = targetedImageView.id
        player.layoutParams = layoutParams
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
            val activity: AppCompatActivity? = context as? AppCompatActivity
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            //  Pick random game
            val randomVal = Random.nextInt(4)

            if (randomVal == 0) {
                println("STEN SAX PÅSE")
                view.findNavController().navigate(R.id.action_boardFragment_to_stensaxpaseFragment)
            } else if (randomVal == 1) {
                println("SOCCER GAME FERDINAND")
                view.findNavController().navigate(R.id.action_boardFragment_to_soccerFragment)
            } else if (randomVal == 2) {
                println("QUIZ GAME PONTUS")
                view.findNavController().navigate(R.id.action_boardFragment_to_quizFragment)
            } else {
                println("ROULETTE WILLIAM")
                view.findNavController()
                    .navigate(R.id.action_boardFragment_to_gavleRouletteFragment)
            }
        }
    }
}
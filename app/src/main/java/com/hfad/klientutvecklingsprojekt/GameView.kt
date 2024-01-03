package com.hfad.klientutvecklingsprojekt

import android.content.Context
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.databinding.FragmentBoardBinding
import com.hfad.klientutvecklingsprojekt.databinding.FragmentStartScreenBinding
import com.hfad.klientutvecklingsprojekt.lobby.LobbyFragmentArgs
import com.hfad.klientutvecklingsprojekt.lobby.LobbyFragmentDirections
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

    fun setNavigateCallback(callback: () -> Unit) {
        this.navigateCallback = callback
    }

    // For safeargs
    var gameID = ""

    // Constructors for creating the view programmatically
    constructor(context: Context, gameID: String) : super(context) {
        this.gameID = gameID
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
        //  Walk button
        val mB: Button? = _binding.moveButton
        mB?.setOnClickListener {
            // Increment the index
            currentImageViewIndex++
            val tile = resources.getIdentifier(
                "tile${currentImageViewIndex}",
                "id",
                context.packageName
            )
            //  Apply changes to ImageView
            val characterImageView = binding.root.findViewById<ImageView>(tile)
            movePlayer(characterImageView)
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
                // For safeargs
                val action =
                    BoardFragmentDirections.actionBoardFragmentToStensaxpaseFragment(gameID)

                view.findNavController().navigate(action)
            } else if (randomVal == 1) {
                // For safeargs
                val action = BoardFragmentDirections.actionBoardFragmentToSoccerFragment(gameID)

                view.findNavController().navigate(action)
            } else if (randomVal == 2) {
                // For safeargs
                val action =
                    BoardFragmentDirections.actionBoardFragmentToGavleRouletteFragment(gameID)

                view.findNavController().navigate(action)
            } else {
                // For safeargs
                val action = BoardFragmentDirections.actionBoardFragmentToQuizFragment(gameID)

                view.findNavController().navigate(action)
            }
        }
    }
}

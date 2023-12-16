package com.hfad.klientutvecklingsprojekt

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.hfad.klientutvecklingsprojekt.databinding.FragmentBoardBinding

class GameView : ConstraintLayout {
    private lateinit var player: ImageView
    private var currentImageViewIndex: Int = 0
    lateinit var binding: FragmentBoardBinding
    // Constructors for creating the view programmatically
    constructor(context: Context) : super(context) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }
    private fun init(context: Context) {
        binding = FragmentBoardBinding.inflate(LayoutInflater.from(context), this, true)

        // Now you can access the views using the binding
        player = binding.player1

        val mB: Button? = binding.moveButton
        mB?.setOnClickListener {
            // Increment the index
            currentImageViewIndex++

            // If index is greater than the array size, reset to 0
            if (currentImageViewIndex > 20) {
                currentImageViewIndex = 1
                movePlayer(binding.tile1)
            } else if(currentImageViewIndex == 1) {
                movePlayer(binding.tile1)
            } else if(currentImageViewIndex == 2) {
                movePlayer(binding.tile2)
            } else if(currentImageViewIndex == 3) {
                movePlayer(binding.tile3)
            } else if(currentImageViewIndex == 4) {
                movePlayer(binding.tile4)
            } else if(currentImageViewIndex == 5) {
                movePlayer(binding.tile5)
            } else if(currentImageViewIndex == 6) {
                movePlayer(binding.tile6)
            } else if(currentImageViewIndex == 7) {
                movePlayer(binding.tile7)
            } else if(currentImageViewIndex == 8) {
                movePlayer(binding.tile8)
            } else if(currentImageViewIndex == 9) {
                movePlayer(binding.tile9)
            } else if(currentImageViewIndex == 10) {
                movePlayer(binding.tile10)
            } else if(currentImageViewIndex == 11) {
                movePlayer(binding.tile11)
            } else if(currentImageViewIndex == 12) {
                movePlayer(binding.tile12)
            } else if(currentImageViewIndex == 13) {
                movePlayer(binding.tile13)
            } else if(currentImageViewIndex == 14) {
                movePlayer(binding.tile14)
            } else if(currentImageViewIndex == 15) {
                movePlayer(binding.tile15)
            } else if(currentImageViewIndex == 16) {
                movePlayer(binding.tile16)
            } else if(currentImageViewIndex == 17) {
                movePlayer(binding.tile17)
            } else if(currentImageViewIndex == 18) {
                movePlayer(binding.tile18)
            } else if(currentImageViewIndex == 19) {
                movePlayer(binding.tile19)
            } else if(currentImageViewIndex == 20) {
                movePlayer(binding.tile20)
            }
            // Move player to the current target ImageView
        }
    }
    fun movePlayer(targetedImageView: ImageView) {
        // Update constraints to move player to tile2
        println("before layoutParams")
        val layoutParams = player.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToTop = targetedImageView.id
        layoutParams.endToEnd = targetedImageView.id
        player.layoutParams = layoutParams
        println("Target Tile ID: ${targetedImageView.id}")
    }
}
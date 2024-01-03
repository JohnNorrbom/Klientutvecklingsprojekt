package com.hfad.klientutvecklingsprojekt

import android.content.Context
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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
    private val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Space Party")
    private val binding get() = _binding!!
    private var navigateCallback: (() -> Unit)? = null

    fun setNavigateCallback(callback: () -> Unit) {
        this.navigateCallback = callback
    }

    // For safeargs
    var gameID = ""

    // Constructors for creating the view programmatically
    constructor(context: Context, gameID:String) : super(context) {
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

        val mB: Button? = _binding.moveButton
        mB?.setOnClickListener {
            // Increment the index
            currentImageViewIndex++
            // If index is greater than the array size, reset to 0
            if (currentImageViewIndex > 20) {
                currentImageViewIndex = 1
                movePlayer(_binding.tile1)
            } else if (currentImageViewIndex == 1) {
                movePlayer(_binding.tile1)
            } else if (currentImageViewIndex == 2) {
                movePlayer(_binding.tile2)
            } else if (currentImageViewIndex == 3) {
                movePlayer(_binding.tile3)
            } else if (currentImageViewIndex == 4) {
                movePlayer(_binding.tile4)
            } else if (currentImageViewIndex == 5) {
                movePlayer(_binding.tile5)
            } else if (currentImageViewIndex == 6) {
                movePlayer(_binding.tile6)
            } else if (currentImageViewIndex == 7) {
                movePlayer(_binding.tile7)
            } else if (currentImageViewIndex == 8) {
                movePlayer(_binding.tile8)
            } else if (currentImageViewIndex == 9) {
                movePlayer(_binding.tile9)
            } else if (currentImageViewIndex == 10) {
                movePlayer(_binding.tile10)
            } else if (currentImageViewIndex == 11) {
                movePlayer(_binding.tile11)
            } else if (currentImageViewIndex == 12) {
                movePlayer(_binding.tile12)
            } else if (currentImageViewIndex == 13) {
                movePlayer(_binding.tile13)
            } else if (currentImageViewIndex == 14) {
                movePlayer(_binding.tile14)
            } else if (currentImageViewIndex == 15) {
                movePlayer(_binding.tile15)
            } else if (currentImageViewIndex == 16) {
                movePlayer(_binding.tile16)
            } else if (currentImageViewIndex == 17) {
                movePlayer(_binding.tile17)
            } else if (currentImageViewIndex == 18) {
                movePlayer(_binding.tile18)
            } else if (currentImageViewIndex == 19) {
                movePlayer(_binding.tile19)
            } else if (currentImageViewIndex == 20) {
                movePlayer(_binding.tile20)
            }
            // Move player to the current target ImageView
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
            val randomVal = Random.nextInt(3)

            if (randomVal == 0) {
                // For safeargs
                val action = BoardFragmentDirections.actionBoardFragmentToStensaxpaseFragment(gameID)

                view.findNavController().navigate(action)
            } else if (randomVal == 1) {
                // For safeargs
                val action = BoardFragmentDirections.actionBoardFragmentToSoccerFragment(gameID)

                view.findNavController().navigate(action)
            } else {
                // For safeargs
                val action = BoardFragmentDirections.actionBoardFragmentToGavleRouletteFragment(gameID)

                view.findNavController().navigate(action)
            }
        }
    }

    //  Alternative solution WIP
    fun initTileBlocks(size: Int, geometry: Int) {
        var tileBlockArray = arrayOf(TileBlock(0, 0, TileTypes.TileStart, 0, 0, 100, 100))

        for (i in 1..<size) {
            tileBlockArray += TileBlock(i, i, TileTypes.TileAdd1Point, i, 0, 100, 100)
        }
    }

    //  New solution
    fun plotTiles() {
        //  first tile
        /*<ImageView
        android:id="@+id/tile1"
        android:layout_width="56dp"
        android:layout_height="56dp"
        app:layout_constraintBottom_toTopOf="@+id/tile20"
        app:layout_constraintEnd_toStartOf="@+id/tile2"
        app:layout_constraintHorizontal_chainStyle="packed"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_chainStyle="packed"
        app:srcCompat="@drawable/tile_go" />*/
        //  horizontal tile
        /*<ImageView
        android:id="@+id/tile2"
        android:layout_width="56dp"
        android:layout_height="56dp"
        app:layout_constraintTop_toTopOf="@+id/tile1"
        app:layout_constraintEnd_toStartOf="@+id/tile3"
        app:layout_constraintStart_toEndOf="@+id/tile1"
        app:layout_constraintHorizontal_bias="0.5"
        app:srcCompat="@drawable/tile_plus_1" />*/
        //  vertical tile
        /*<ImageView
        android:id="@+id/tile9"
        android:layout_width="56dp"
        android:layout_height="56dp"
        app:layout_constraintBottom_toTopOf="@+id/tile10"
        app:layout_constraintTop_toBottomOf="@+id/tile8"
        app:layout_constraintEnd_toEndOf="@+id/tile8"
        app:srcCompat="@drawable/tile_plus_3" />*/
    }

    //  New solution
    private var gamePoints: Int = 0
    private fun addPoints(addPoint: Int) {
        gamePoints += addPoint
    }

    //  New solution
    fun getPoints(): Int {
        return gamePoints
    }

    //  New solution
    fun moveEvent(tileBlockType: TileTypes) {

        when (tileBlockType) {
            TileTypes.TileStart -> addPoints(0)

            TileTypes.TileAdd1Point -> addPoints(1)

            TileTypes.TileAdd2Point -> addPoints(2)

            TileTypes.TileAdd3Point -> addPoints(3)

            TileTypes.TileSub1Point -> addPoints(-1)

            TileTypes.TileSub2Point -> addPoints(-2)

            TileTypes.TileSub3Point -> addPoints(-3)

            TileTypes.TileMiniGame -> {
                navigateCallback?.invoke()
            }

            TileTypes.TileTreasure -> addPoints(1)

            else -> throw Exception("Invalid tileBlock type")
        }
    }


}


// New solution
enum class TileTypes {
    TileStart,
    TileAdd1Point, TileAdd2Point, TileAdd3Point,
    TileSub1Point, TileSub2Point, TileSub3Point,
    TileMiniGame,
    TileTreasure,
}

//  New solution
class TileBlock(i: Int, i1: Int, tileAdd1Point: TileTypes, i2: Int, i3: Int, i4: Int, i5: Int) {
    val idNumber: Int = 0
    val tileNumber: Int = 0
    val blockType = TileTypes.TileStart
    val coordX: Int = 0
    val coordY: Int = 0
    val imageSizeX: Int = 100
    val imageSizeY: Int = 100
}


package com.hfad.klientutvecklingsprojekt.board

import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import com.google.android.gms.common.api.internal.LifecycleActivity
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentBoardBinding
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel
import com.hfad.klientutvecklingsprojekt.stensaxpase.StenSaxPaseData
import com.hfad.klientutvecklingsprojekt.stensaxpase.StenSaxPaseModel
import kotlin.random.Random

//  TODO add more ImageViews for each player and set it as GONE         //John
//  TODO connect each ImageView with it's corresponding player/color
//  TODO keep track of local player in order to keep track of turns

class GameView : ConstraintLayout {
    private lateinit var player: ImageView
    private var currentImageViewIndex: Int = 0
    private var meModel : MeModel ? =null
    private var playerModel: PlayerModel ? = null
    lateinit var view: ConstraintLayout
    lateinit var _binding: FragmentBoardBinding
    var currentGameID =""
    var currentPlayerID =""
    private val database =
        Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Space Party")



    //player references
    var playerRef = database.getReference("Player Data").child("7496")
    val playersRef = playerRef.child("players")

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

    private fun init(context: Context) {
    // characters players
        _binding = FragmentBoardBinding.inflate(LayoutInflater.from(context), this, true)
        view = binding.root

        MeData.meModel.observe(context as LifecycleOwner) { meModel ->
            meModel?.let {
                this@GameView.meModel = it
                setText()
            } ?: run {
                // Handle the case when meModel is null
                Log.e("LobbyFragment", "meModel is null")
            }
        }

        binding.playerWhite.visibility = View.GONE
        binding.playerRed.visibility = View.GONE
        binding.playerYellow.visibility = View.GONE
        binding.playerGreen.visibility = View.GONE
        binding.playerBlue.visibility = View.GONE
        getPlayerToBoard()



        // Now you can access the views using the binding
        player = _binding.playerWhite


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
                "tile${(currentImageViewIndex%20) + 1}",
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
                "tile${(currentImageViewIndex%20) + 1}",
                "id",
                context.packageName
            )
            val tileImage = binding.root.findViewById<ImageView>(tile)
            println("current tileImage " + tileImage + " current tile" + tile)
            movePlayer(tileImage)
        }
    }

    fun setText(){
        //den här
        currentGameID= meModel?.gameID?:""
        currentPlayerID = meModel?.playerID?:""
        Log.d("meModelView","playerID: ${currentPlayerID} GameID: ${currentGameID}")
    }

    fun getPlayerToBoard(){
        playersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (playerSnapshot in dataSnapshot.children) {
                    val playerColor = playerSnapshot.child("color").value
                    val playerName = playerSnapshot.child("nickname").value
                    println(playerColor)
                    println(playerName)
                    if(playerColor == "green"){
                        binding.playerGreen.visibility = View.VISIBLE
                    }
                    if(playerColor == "red"){
                        binding.playerRed.visibility = View.VISIBLE
                    }
                    if(playerColor == "white"){
                        binding.playerWhite.visibility = View.VISIBLE
                    }
                    if(playerColor == "yellow"){
                        binding.playerYellow.visibility = View.VISIBLE
                    }
                    if(playerColor == "blue"){
                        binding.playerBlue.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Failed to fetch player data: ${databaseError.message}")
            }
        })
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
                var id = meModel?.gameID
                Log.d("Game ID", "${id}")
                Log.d("Player ID", "${meModel?.playerID}")
                StenSaxPaseData.saveGameModel(
                    StenSaxPaseModel(
                        gameID = id,
                        status = false,
                    )
                )
                view.findNavController().navigate(R.id.action_boardFragment_to_stensaxpaseFragment)
            } else if (randomVal == 1) {
                println("SOCCER GAME FERDINAND")
                view.findNavController().navigate(R.id.action_boardFragment_to_soccerChooseFragment)
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
}

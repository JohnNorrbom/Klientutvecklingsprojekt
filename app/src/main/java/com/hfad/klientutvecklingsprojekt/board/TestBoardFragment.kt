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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentTestBoardBinding
import com.hfad.klientutvecklingsprojekt.gavleroulette.GameStatus
import com.hfad.klientutvecklingsprojekt.gavleroulette.PlayerStatus
import com.hfad.klientutvecklingsprojekt.gavleroulette.RouletteData
import com.hfad.klientutvecklingsprojekt.gavleroulette.RouletteModel
import com.hfad.klientutvecklingsprojekt.lobby.LobbyData
import com.hfad.klientutvecklingsprojekt.lobby.LobbyModel
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData.gameID
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel
import kotlin.random.Random


//TODO fixa minigame variabel till board så att minigame startar för alla (förutom de med choose)
//TODO fixa så att score sparas lokalt innan man slår tärning så att inte spelaren börjar från början. (när fragment startas om)
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
    private var localGameID = ""
    private var localPlayerID = ""
    private var meModel: MeModel? = null

    //boardModel
    private var boardModel: BoardModel? = null
    private val boardRef = database.getReference("Board Data")

    // PLAYER
    private var playerModel: PlayerModel? = null
    private var currentImageViewIndex: Int = 0
    private var localScore: Int = 0

    // BG MUSIC
    private var mediaPlayer: MediaPlayer? = null

    //  minigame
    private var localRandomVal = -1

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
        BoardData.boardModel.observe(this) { boardModel ->
            boardModel?.let {
                this@TestBoardFragment.boardModel = it
            } ?: run {
                Log.e("LobbyFragment", "meModel is null")
            }
        }
        PlayerData.playerModel.observe(this) { playerModel ->
            playerModel?.let {
                this@TestBoardFragment.playerModel = it
            } ?: run {
                // Handle the case when meModel is null
                Log.e("LobbyFragment", "meModel is null")
            }
        }

        boardRef.addValueEventListener(boardListener)
        diceButton()
        playerRef.addValueEventListener(positionListener)
        //  leader button
        binding.leaderBoardButton.setOnClickListener {
//            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//            view.findNavController().navigate(R.id.action_testBoardFragment_to_leaderBoardFragment)
//            binding.root.rootView.setBackgroundResource(R.drawable.leaderboard)
        }
        // Inflate the layout for this fragment
        return view
    }

    private fun setText() {
        meModel?.apply {
            localGameID = gameID ?: ""
            localPlayerID = playerID ?: ""
            playerRef = database.getReference("Player Data").child(localGameID)
            playersRef = playerRef.child("players")
            binding.playerBlue.visibility = View.GONE
            binding.playerWhite.visibility = View.GONE
            binding.playerRed.visibility = View.GONE
            binding.playerYellow.visibility = View.GONE
            binding.playerGreen.visibility = View.GONE
            paintPlayers()
        }
    }


    /*
    this also calls setplayeronrightposition. and is thought to be called everytime something happens
     */
    private fun paintPlayers() {
        myRef.child(localGameID).child("players").get()
            .addOnSuccessListener { dataSnapshot ->
                dataSnapshot.children.forEach { playerSnapshot ->
                    val playerId = playerSnapshot.child("playerId").value.toString()
                    val color = playerSnapshot.child("color").value.toString()
                    val position = Integer.valueOf(playerSnapshot.child("position").value.toString())
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
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("paintPlayers", "Error fetching players", exception)
            }
    }
    fun diceButton() {
        //  DICE BUTTON
        val dice = binding.diceButton
        //  DICE BUTTON LISTENER
        dice?.setOnClickListener {
            //soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
            var randomInt = Random.nextInt(6) + 1
            var destination = "dice" + randomInt
            val resourceId = resources.getIdentifier(
                destination,
                "drawable",
                "com.hfad.klientutvecklingsprojekt"
            )
            binding.diceButton?.setImageResource(resourceId)
            currentImageViewIndex += randomInt

            if(currentImageViewIndex%20 == 1 || currentImageViewIndex%20 == 6 || currentImageViewIndex%20 == 11 || currentImageViewIndex%20 == 16 ){
                localScore += 1
            }
            if(currentImageViewIndex%20 == 2 || currentImageViewIndex%20 == 7 || currentImageViewIndex%20 == 12 || currentImageViewIndex%20 == 17 ){
                localScore += 2
            }
            if(currentImageViewIndex%20 == 3 || currentImageViewIndex%20 == 8 || currentImageViewIndex%20 == 13 || currentImageViewIndex%20 == 18 ){
                localScore += 3
            }
            if(currentImageViewIndex%20 == 4 || currentImageViewIndex%20 == 9 || currentImageViewIndex%20 == 14 || currentImageViewIndex%20 == 19 ){
                localScore += -5
            }
            if(currentImageViewIndex%20 == 5 || currentImageViewIndex%20 == 10 || currentImageViewIndex%20 == 15){
                //minigame
                //  Pick random game
                localRandomVal = Random.nextInt(4)
                //laddauppminigamesiffra,
                //gör en listener som kallar på setMinigame
                // currentPlayer startar minigame
                boardModel?.apply {
                    boardRef.child(localGameID).child("randomVal").setValue(localRandomVal)
                }
            }
            playerModel?.apply {
                position = currentImageViewIndex
                playersRef.child(localPlayerID).child("position").setValue(position)
                playersRef.child(localPlayerID).child("score").setValue(localScore)
            }
            paintPlayers()
            assignNextCurrentPlayer()
            binding.diceButton.visibility = View.GONE
        }
    }
    override fun onPause() {
        super.onPause()
    }
    override fun onResume() {
        super.onResume()

        // currentImageViewIndex = playerModel localPlayerID position
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun setMiniGame(randomVal: Int) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (randomVal == 0) {
            if (isAdded && view != null) {
                view.findNavController().navigate(R.id.action_testBoardFragment_to_stenSaxPaseChooseFragment)
            }
        } else if (randomVal == 1) {
            if (isAdded && view != null) {
                view.findNavController().navigate(R.id.action_testBoardFragment_to_soccerChooseFragment)

            }
                println("SOCCER GAME FERDINAND")
        } else if (randomVal == 2) {
            println("QUIZ GAME PONTUS")
            if (isAdded && view != null) {
                view.findNavController().navigate(R.id.action_testBoardFragment_to_quizFragment)

            }
        } else {
            println("ROULETTE WILLIAM")
            if (isAdded && view != null) {
                var myPlayers : MutableMap<String, PlayerStatus> = mutableMapOf()
                myRef.child(localGameID).child("players").get().addOnSuccessListener {
                    val snapshot = it
                    for (player in snapshot.children){
                        Log.d("player","${player}")
                        myPlayers?.put(player.key.toString(), PlayerStatus.ALIVE)
                        Log.d("players","${myPlayers}")
                    }

                    Log.d("currentPlayer","${myPlayers.keys.elementAt(Random.nextInt(myPlayers.size))}")

                    if (myPlayers.size>1){
                        RouletteData.saveGameModel(
                            RouletteModel(
                                gameId = localGameID,
                                players = myPlayers,
                                gameStatus = GameStatus.INPROGRESS,
                                attempts = 0,
                                laps = 0,
                                nbrOfPlayers = myPlayers.size,
                                aliveCount = myPlayers.size,
                                luckyNumber = mutableListOf((Random.nextInt(6)+1).toString()),
                                currentPlayer = myPlayers.keys.elementAt(Random.nextInt(myPlayers.size))
                            ),localGameID
                        )
                    }
                }

                view.findNavController().navigate(R.id.action_testBoardFragment_to_gavleRouletteFragment)
            }
        }
    }
    private val positionListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            paintPlayers()
        }
        override fun onCancelled(error: DatabaseError) {
        }
    }

    private val boardListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            boardRef.child(localGameID).child("currentPlayerId").get().addOnSuccessListener { dataSnapshot ->
                val currentPlayerId = dataSnapshot.value
                if (currentPlayerId == localPlayerID){
                    binding.diceButton.visibility = View.VISIBLE
                }else{
                    binding.diceButton.visibility  = View.INVISIBLE
                }
                if(localRandomVal != -1) {
                    setMiniGame(localRandomVal)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool?.release()
        soundPool = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
    fun assignNextCurrentPlayer() {
        var playerIDarr = arrayListOf<String>()
        myRef.child(localGameID).child("players").get()
            .addOnSuccessListener { dataSnapshot ->
                dataSnapshot.children.forEach { playerSnapshot ->
                    val playerID = playerSnapshot.child("playerID").value.toString()
                    playerIDarr.add(playerID)

                    var index = playerIDarr.indexOf(localPlayerID)

                    if (index != -1) {
                        index = if (index < playerIDarr.size - 1) index + 1 else 0
                        boardRef.child(localGameID).child("currentPlayerId").setValue(playerIDarr[index])
                    } else {
                        println("Error: currentPlayerID not found in arrList")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("assignNextCurrentPlayer", "Error fetching players", exception)
            }

    }
}

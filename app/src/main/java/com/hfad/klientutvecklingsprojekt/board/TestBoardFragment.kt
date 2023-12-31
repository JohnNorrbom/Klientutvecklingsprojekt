package com.hfad.klientutvecklingsprojekt.board

import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData.gameID
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel
import kotlin.random.Random

/**
 *
 * @author John, Simon, Pontus, William & Ferdinand
 *
 * TestBoardFragment:
 *
 * målar spelplanen
 * målar rankningslistan
 * målar tärningen
 * målar spelarna
 *
 * läser av Player Data, Board Data & Quiz tabellen från databasen
 *
 * startar minigames (hoppar till andra fragments)
 *
 * avslutar spelet när en spelare har 30 poäng (navigerar till winnerFragment)
 *
 */
class TestBoardFragment : Fragment() {
    //  VIEWBINDING
    private var _binding: FragmentTestBoardBinding? = null
    private val binding get() = _binding!!
    private lateinit var view: ConstraintLayout
    private var handler = Handler()

    //  DATABASE
    private val database =
        Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Player Data")
    private var gameRef = database.getReference("Player Data").child(gameID)
    private var playersRef = gameRef.child("players")

    //  meModel den lokala spelaren, alltså du
    private var localGameID = ""
    private var localPlayerID = ""
    private var meModel: MeModel? = null

    // boardModel
    private var boardModel: BoardModel? = null
    private val boardRef = database.getReference("Board Data")

    // PLAYER
    private var playerModel: PlayerModel? = null
    private var currentImageViewIndex: Int = 0
    private var localScore: Int = 0

    // BG MUSIC
    private var mediaPlayer: MediaPlayer? = null

    // Misc sounds
    private val maxStreams = 5 // Number of simultaneous sounds
    private var soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        SoundPool.Builder().setMaxStreams(maxStreams).build()
    } else {
        SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0)
    }

    // bestämmer minigame
    private var localRandomVal = -1

    // håller koll på vems tur det är
    private var localCurrentPlayerTest = ""

    // LEADERBOARD
    val leaderboardList = mutableListOf<Pair<String, Int>>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTestBoardBinding.inflate(inflater, container, false)
        view = binding.root
        diceButton()
        // startar bg musiken
        mediaPlayer = MediaPlayer.create(
            requireContext(), R.raw.android_song2_140bpm
        )
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        MeData.meModel.observe(this) { meModel ->
            meModel?.let {
                this@TestBoardFragment.meModel = it
                setText()

            } ?: run {
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
                Log.e("LobbyFragment", "meModel is null")
            }
        }

        boardRef.addValueEventListener(boardListener)
        gameRef.addValueEventListener(positionListener)
        boardRef.addValueEventListener(gameStatusListener)

        // Inflate the layout for this fragment
        return view
    }

    private fun setText() {


        meModel?.apply {
            localGameID = gameID ?: ""
            localPlayerID = playerID ?: ""
            gameRef = database.getReference("Player Data").child(localGameID)
            playersRef = gameRef.child("players")
            binding.playerBlue.visibility = View.GONE
            binding.playerWhite.visibility = View.GONE
            binding.playerRed.visibility = View.GONE
            binding.playerYellow.visibility = View.GONE
            binding.playerGreen.visibility = View.GONE


            println("local player id: " + localPlayerID)
            playersRef.child(localPlayerID).get()
                .addOnSuccessListener { dataSnapshot ->
                    currentImageViewIndex = dataSnapshot.child("position").value.toString().toInt()
                    paintPlayers()
                }
        }
    }
    private fun updateScore() {
        playersRef.child(localPlayerID).get()
            .addOnSuccessListener { dataSnapshot ->
                this.localScore += dataSnapshot.child("score").value.toString().toInt()
                playersRef.child(localPlayerID).child("score").setValue(this.localScore)
                this.localScore = 0
            }
    }

    // returnerar spelaren och dess poäng som en sträng
    fun getLeaderText(index: Int): String {
        return if (index in 0 until leaderboardList.size) {
            "${leaderboardList[index].first} ${leaderboardList[index].second}"
        } else {
            "N/A" // Värde för tom plats i leaderboarden
        }
    }

    private fun updateLeaderboard(nickname: String, number: Int) {
        if (leaderboardList.isEmpty()) {
            // lägger till första paret i listan
            leaderboardList.add(nickname to number)
            Log.d("score", "leaderboardList ${nickname} ${number}")
        } else {
            // kollar om spelaren finns med i listan
            val existingIndex = leaderboardList.indexOfFirst { it.first == nickname }

            if (existingIndex != -1) {
                // uppdaterar spelarens poäng
                leaderboardList[existingIndex] = nickname to number
            } else {
                // lägger till ett nytt par i listan
                leaderboardList.add(nickname to number)
            }
            Log.d(
                "score",
                "leaderboard: ${getLeaderText(0)}, ${getLeaderText(1)}, ${getLeaderText(2)}, ${
                    getLeaderText(3)
                }, ${getLeaderText(4)}"
            )
        }
        // Sorterar leaderboarden
        leaderboardList.sortByDescending { it.second }
        // Sätter text på leaderboarden
        binding.textViewLeader1.text = getLeaderText(0)
        binding.textViewLeader2.text = getLeaderText(1)
        binding.textViewLeader3.text = getLeaderText(2)
        binding.textViewLeader4.text = getLeaderText(3)
        binding.textViewLeader5.text = getLeaderText(4)
        checkForWinner(leaderboardList)
    }

    private fun checkForWinner(leaderboard: MutableList<Pair<String, Int>>) {
        if (leaderboard[0].second > 29) {
            try {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                // skickar vinnar namn och poäng till nästa fragment med safeArgs
                val action = TestBoardFragmentDirections.actionTestBoardFragmentToWinnerFragment(
                    leaderboard[0].first,
                    leaderboard[0].second
                )
                view.findNavController().navigate(action)
            } catch (e: Exception) {
                println(e.stackTrace)
            }
        }
    }

    private fun paintPlayers() {

        myRef.child(localGameID).child("players").get().addOnSuccessListener { dataSnapshot ->
            dataSnapshot.children.forEach { playerSnapshot ->
                val playerId = playerSnapshot.child("playerId").value.toString()
                val color = playerSnapshot.child("color").value.toString()
                val position =
                    Integer.valueOf(playerSnapshot.child("position").value.toString())

                val imageView = when (color) {
                    "blue" -> binding.playerBlue
                    "red" -> binding.playerRed
                    "green" -> binding.playerGreen
                    "yellow" -> binding.playerYellow
                    "white" -> binding.playerWhite
                    else -> null // Handle any other colors if needed
                }

                val nickname = playerSnapshot.child("nickname").value.toString()
                val number = playerSnapshot.child("score").value.toString().toInt()
                Log.d("score", "testing: $number")
                updateLeaderboard(nickname, number)
                imageView?.let { view ->
                    //make player imageView visible
                    view.visibility = View.VISIBLE
                    //take player imageView same pos as corresponding tile
                    val tileId = position % 20 + 1
                    val tileName = "tile$tileId"

                    var tile = when (tileName) {
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
                    if (tile != null) {
                        val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
                        layoutParams.topToTop = tile.id
                        layoutParams.endToEnd = tile.id
                        view.layoutParams = layoutParams
                    }
                }
            }
        }.addOnFailureListener { exception ->
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
//            var randomInt = 10

            var destination = "dice" + randomInt
            var resourceId = resources.getIdentifier(
                destination,
                "drawable",
                "com.hfad.klientutvecklingsprojekt"
            )
            binding.diceButton?.setImageResource(resourceId)
            currentImageViewIndex += randomInt
            println("LOCALSCORE INNAN IF STATEMENTS:" + localScore)
            if (currentImageViewIndex % 20 == 0) {
                soundPool.load(context, R.raw.dice_sound, 5)
                soundPool.setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) {
                        soundPool.play(sampleId, 1F, 1F, 5, 0, 1.0F)
                    } else {
                        Log.e("SoundPool", "Failed to load sound")
                    }
                }
            }
            if (currentImageViewIndex % 20 == 1 || currentImageViewIndex % 20 == 6 || currentImageViewIndex % 20 == 11 || currentImageViewIndex % 20 == 15) {
                localScore += 1
                soundPool.load(context, R.raw.dice_sound, 5)
                soundPool.setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) {
                        soundPool.play(sampleId, 1F, 1F, 5, 0, 1.0F)
                    } else {
                        Log.e("SoundPool", "Failed to load sound")
                    }
                }
            }
            if (currentImageViewIndex % 20 == 2 || currentImageViewIndex % 20 == 7 || currentImageViewIndex % 20 == 12 || currentImageViewIndex % 20 == 16) {
                localScore += 2
                soundPool.load(context, R.raw.dice_sound, 5)
                soundPool.setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) {
                        soundPool.play(sampleId, 1F, 1F, 5, 0, 1.0F)
                    } else {
                        Log.e("SoundPool", "Failed to load sound")
                    }
                }
            }
            if (currentImageViewIndex % 20 == 3 || currentImageViewIndex % 20 == 8 || currentImageViewIndex % 20 == 13 || currentImageViewIndex % 20 == 17) {
                localScore += 3
                soundPool.load(context, R.raw.dice_sound, 5)
                soundPool.setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) {
                        soundPool.play(sampleId, 1F, 1F, 5, 0, 1.0F)
                    } else {
                        Log.e("SoundPool", "Failed to load sound")
                    }
                }
            }
            if (currentImageViewIndex % 20 == 4 || currentImageViewIndex % 20 == 9 || currentImageViewIndex % 20 == 14 || currentImageViewIndex % 20 == 18) {
                localScore += -5
                soundPool.load(context, R.raw.dice_sound, 5)
                soundPool.setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) {
                        soundPool.play(sampleId, 1F, 1F, 5, 0, 1.0F)
                    } else {
                        Log.e("SoundPool", "Failed to load sound")
                    }
                }
                soundPool.load(context, R.raw.android_blunder, 4)
                soundPool.setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) {
                        soundPool.play(sampleId, 1F, 1F, 5, 0, 1.0F)
                    } else {
                        Log.e("SoundPool", "Failed to load sound")
                    }
                }
            }
            if (currentImageViewIndex % 20 == 5 || currentImageViewIndex % 20 == 10 || currentImageViewIndex % 20 == 19) {
                soundPool.load(context, R.raw.dice_sound, 5)
                soundPool.setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) {
                        soundPool.play(sampleId, 1F, 1F, 5, 0, 1.0F)
                    } else {
                        Log.e("SoundPool", "Failed to load sound")
                    }
                }
                //minigame
                //  Pick random game
                localRandomVal = Random.nextInt(4)

                //laddauppminigamesiffra,
                //gör en listener som kallar på setMinigame
                // currentPlayer startar minigame
                println("gameID: $localGameID entering mini-game: $localRandomVal")
                boardRef.child(localGameID).child("randomVal").setValue(localRandomVal)
            }
            playerModel?.apply {
                position = currentImageViewIndex
                playersRef.child(localPlayerID).child("position").setValue(position)
                println("Uppdaterar databasen med följande poäng:" + localScore)
                updateScore()
            }
            paintPlayers()
            assignNextCurrentPlayer()
            destination = "dice" + randomInt + "grayed"
            resourceId = resources.getIdentifier(
                destination,
                "drawable",
                "com.hfad.klientutvecklingsprojekt"
            )
            binding.diceButton?.setImageResource(resourceId)
            binding.diceButton.isEnabled = false
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun setMiniGame(randomVal: Int) {
        try {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            if (randomVal == 0) {
                if (isAdded && view != null) {
                    if (localCurrentPlayerTest == localPlayerID) {
                        view.findNavController()
                            .navigate(R.id.action_testBoardFragment_to_stenSaxPaseChooseFragment)
                    } else {
                        view.findNavController()
                            .navigate(R.id.action_testBoardFragment_to_stenSaxPaseWaitFragment)
                    }

                }
            } else if (randomVal == 1) {
                if (isAdded && view != null) {
                    if (localCurrentPlayerTest == localPlayerID) {
                        view.findNavController()
                            .navigate(R.id.action_testBoardFragment_to_soccerChooseFragment)
                    } else {
                        view.findNavController()
                            .navigate(R.id.action_testBoardFragment_to_waitingSoccerFragment)
                    }
                }
                println("SOCCER GAME FERDINAND")
            } else if (randomVal == 2) {
                println("QUIZ GAME PONTUS")
                if (isAdded && view != null) {
                    database.getReference().child("Quiz").child(localGameID).child("seed")
                        .setValue(Random.nextInt(1000))
                    view.findNavController()
                        .navigate(R.id.action_testBoardFragment_to_quizWaitingFragment)

                }
            } else if (randomVal == 3) {
                println("ROULETTE WILLIAM")
                if (isAdded && view != null) {
                    handler.postDelayed({
                        try {
                            println("roulette vald")
                            if (localCurrentPlayerTest == localPlayerID) {
                                playersRef.get().addOnSuccessListener {
                                    val snapshot = it
                                    var gamePlayer: MutableMap<String, PlayerStatus> =
                                        mutableMapOf()
                                    var scorePlayers: MutableMap<String, Int> = mutableMapOf()
                                    for (player in snapshot.children) {
                                        Log.d("player", "${player}")
                                        gamePlayer?.put(player.key.toString(), PlayerStatus.ALIVE)
                                        scorePlayers?.put(player.key.toString(), 0)
                                        Log.d("players", "${gamePlayer}")
                                    }
                                    Log.d(
                                        "currentPlayer",
                                        "${gamePlayer.keys.elementAt(Random.nextInt(gamePlayer.size))}"
                                    )

                                    Log.d("game ID", "${localGameID}")

                                    RouletteData.saveGameModel(
                                        RouletteModel(
                                            gameId = localGameID,
                                            players = gamePlayer,
                                            gameStatus = GameStatus.INPROGRESS,
                                            attempts = 0,
                                            laps = 0,
                                            currentBullet = 0,
                                            winner = "",
                                            score = scorePlayers,
                                            nbrOfPlayers = gamePlayer.size,
                                            aliveCount = gamePlayer.size,
                                            luckyNumber = Random.nextInt(6) + 1,
                                            currentPlayer = gamePlayer.keys.elementAt(
                                                Random.nextInt(
                                                    gamePlayer.size
                                                )
                                            )
                                        ), localGameID

                                    )

                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, 3000)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
            boardRef.child(localGameID).child("currentPlayerId").get()
                .addOnSuccessListener { dataSnapshot ->
                    val currentPlayerId = dataSnapshot.value
                    if (currentPlayerId == localPlayerID) {
                        binding.diceButton.setImageResource(R.drawable.dice1)
                        binding.diceButton.isEnabled = true
                        binding.diceButton.visibility = View.VISIBLE
                        localCurrentPlayerTest = currentPlayerId.toString()
                    } else {
                        binding.diceButton.setImageResource(R.drawable.dice1grayed)
                        binding.diceButton.isEnabled = false
                    }
                    if (localRandomVal != -1) {
                        setMiniGame(localRandomVal)
                    }
                }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    private val gameStatusListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            boardRef.child(localGameID).child("randomVal").get()
                .addOnSuccessListener { dataSnapshot ->
                    try {
                        if (dataSnapshot.exists() && localCurrentPlayerTest != localPlayerID) {
                            val miniGameNmbr = dataSnapshot.getValue().toString().toInt()
                            if (miniGameNmbr == 0) {
                                println("sten sax pase vald")
                                activity?.requestedOrientation =
                                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                println("currentPlayer: $localCurrentPlayerTest , localPlayerID: $localPlayerID")
                                if (localCurrentPlayerTest == localPlayerID) {
                                    view?.findNavController()
                                        ?.navigate(R.id.action_testBoardFragment_to_stenSaxPaseChooseFragment)
                                } else {
                                    view?.findNavController()
                                        ?.navigate(R.id.action_testBoardFragment_to_stenSaxPaseWaitFragment)
                                }
                            } else if (miniGameNmbr == 1) {
                                println("soccer vald")
                                activity?.requestedOrientation =
                                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                view?.findNavController()
                                    ?.navigate(R.id.action_testBoardFragment_to_waitingSoccerFragment)
                            } else if (miniGameNmbr == 2) {
                                println("quiz vald")
                                activity?.requestedOrientation =
                                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                view?.findNavController()
                                    ?.navigate(R.id.action_testBoardFragment_to_quizWaitingFragment)
                            } else if (miniGameNmbr == 3) {
                                println("roulette vald")
                                Log.d("localCurrentPlayerTest", "${localCurrentPlayerTest}")
                                Log.d("localPlayerID", "${localPlayerID}")
                                activity?.requestedOrientation =
                                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                view?.findNavController()
                                    ?.navigate(R.id.action_testBoardFragment_to_gavleRouletteWaitFragment)

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onStop() {
        super.onStop()
        boardRef.removeEventListener(boardListener)
        gameRef.removeEventListener(positionListener)
        boardRef.removeEventListener(gameStatusListener)
        println("GAMEBOARD: JAG HAR STOP")
    }

    fun assignNextCurrentPlayer() {
        var playerIDarr = arrayListOf<String>()
        myRef.child(localGameID).child("players").get()
            .addOnSuccessListener {
                val snapshot = it
                for (player in snapshot.children) {
                    val playerID = player.child("playerID").value.toString()
                    playerIDarr.add(playerID)
                }

                var index = playerIDarr.indexOf(localPlayerID)

                if (index != -1) {
                    index = if (index < playerIDarr.size - 1) index + 1 else 0
                    localCurrentPlayerTest = playerIDarr[index]
                    boardRef.child(localGameID).child("currentPlayerId")
                        .setValue(playerIDarr[index])
                } else {
                    println("Error: currentPlayerID not found in arrList")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("assignNextCurrentPlayer", "Error fetching players", exception)
            }
    }
}

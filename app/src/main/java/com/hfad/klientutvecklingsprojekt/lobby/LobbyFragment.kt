package com.hfad.klientutvecklingsprojekt.lobby

import android.content.ContentValues
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentLobbyBinding
import com.hfad.klientutvecklingsprojekt.gamestart.CharacterStatus
import com.hfad.klientutvecklingsprojekt.gamestart.GameData
import com.hfad.klientutvecklingsprojekt.gamestart.GameModel
import com.hfad.klientutvecklingsprojekt.gamestart.Progress
import com.hfad.klientutvecklingsprojekt.gavleroulette.GameStatus
import com.hfad.klientutvecklingsprojekt.gavleroulette.PlayerStatus
import com.hfad.klientutvecklingsprojekt.gavleroulette.RouletteData
import com.hfad.klientutvecklingsprojekt.gavleroulette.RouletteModel
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel
import com.hfad.klientutvecklingsprojekt.soccer.SoccerData
import kotlin.random.Random


class LobbyFragment : Fragment() {
    private var _binding: FragmentLobbyBinding? = null
    private val binding get()  = _binding!!
    private var lobbyModel : LobbyModel? = null
    private var playerModel : PlayerModel? = null
    private var meModel : MeModel?= null//den här
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Player Data")
    val lobbyRef = database.getReference("Lobby Data")
    val rouRef = database.getReference("Roulette")
    var localGameID =""
    var localPlayerID =""

    // BG MUSIC
    private var mediaPlayer: MediaPlayer? = null

    //host - den som kommer först in
    var playerIsHost: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentLobbyBinding.inflate(inflater,container,false)
        val view = binding.root

        mediaPlayer = MediaPlayer.create(
            requireContext(), R.raw.android_song7_130bpm
        )
        mediaPlayer?.isLooping = true // Disable built-in looping
        mediaPlayer?.start()

        LobbyData.fetchLobbyModel()
        PlayerData.fetchPlayerModel()



        //host (first in) will only see startButton
        binding.startButton.visibility = View.GONE
        setHost()
        if (playerIsHost){
            binding.startButton.visibility = View.VISIBLE
        }

        //  Button for starting game, loading BoardFragment. Everyone can click it right now.
        binding.startButton.setOnClickListener {
            startGame()
        }
//        binding.rouletteButton.setOnClickListener{
//            startRoulette()
//        }
//        binding.joinButton.setOnClickListener{
//            changeScreen()
//        }
//
//        binding.hostSoccer.setOnClickListener {
//            startSoccer()
//        }
//
//        binding.joinSoccer.setOnClickListener {
//            joinSoccer()
//        }

        LobbyData.lobbyModel.observe(this) { lobbyModel ->
            lobbyModel?.let {
                this@LobbyFragment.lobbyModel = it
                changeScreen()
            } ?: run {
                // Handle the case when meModel is null
                Log.e("LobbyFragment", "meModel is null")
            }
        }

        PlayerData.playerModel.observe(this){
            setUI()
        }
        //den här
        MeData.meModel.observe(this) { meModel ->
            meModel?.let {
                this@LobbyFragment.meModel = it
                setText()
                setUI()

            } ?: run {
                // Handle the case when meModel is null
                Log.e("LobbyFragment", "meModel is null")
            }
        }
        println("Me model in LobbyFragment"+meModel)
        println("GameId in LobbyFragment: " + localGameID)

        lobbyRef.addValueEventListener(gameStatusListener)

        return view
    }

    fun setText(){
        //den här
        localGameID= meModel?.gameID?:""
        localPlayerID = meModel?.playerID?:""
        //  Changes text for TextView to the lobby gameID
        val text = "Game ID: ${localGameID}"
        val spannableString = SpannableString(text)
        // Get the length of the text
        val textLength = text.length
        // Set the color for the first half of the text to green
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#6AFF00")), 0, textLength - 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        // Set the color for the second half of the text to red
        spannableString.setSpan(ForegroundColorSpan(Color.RED), textLength - 4, textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.lobbyId.text = spannableString
        binding.lobbyId.visibility = View.VISIBLE
        Log.d("meModel","player ${localPlayerID} Game ${localGameID}")
    }

    fun changeScreen(){
        // The observer is triggered when lobbyModel changes
        lobbyRef.child(localGameID).get().addOnSuccessListener {
            if(it.child("btnPressed").value == true){
                rouRef.child(localGameID).get().addOnSuccessListener {
                    val snapshot = it
                    Log.d("localGameID","${snapshot}")
                    val model = snapshot.getValue(RouletteModel::class.java)
                    Log.d("localGameID","${model}")
                    //  Create array of colors to compare them with the colors in the lobby to see
                    //  which of them are are taken
                    if (model!=null){
                        RouletteData.saveGameModel(model,localGameID)
                        Log.d("Om success","model: ${model}")
                        // btnPressed is true, navigate to the next fragment
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        view?.findNavController()?.navigate(R.id.action_lobbyFragment_to_gavleRouletteFragment)
                    }
                }
            }
        }
    }

    private val gameStatusListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            lobbyRef.child(localGameID).child("btnPressed").get().addOnSuccessListener { dataSnapshot ->
                try {
                    if (dataSnapshot.exists()) {
                        val lobbyData = dataSnapshot.getValue()
                        println("VÄRDET PÅ LOBBYDATA " + lobbyData)
                        println(lobbyData == "true")
                        if(lobbyData == true){
                            view?.findNavController()?.navigate(R.id.action_lobbyFragment_to_testBoardFragment)
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


    fun startSoccer(){
        view?.findNavController()?.navigate(R.id.action_lobbyFragment_to_soccerChooseFragment)
    }

    fun joinSoccer(){
        view?.findNavController()?.navigate(R.id.action_lobbyFragment_to_waitingSoccerFragment)

    }

    fun startGame(){
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        lobbyRef.child(localGameID).child("btnPressed").setValue(true)
        view?.findNavController()?.navigate(R.id.action_lobbyFragment_to_testBoardFragment)
    }
    fun startRoulette(){
        var gamePlayer : MutableMap<String, PlayerStatus> = mutableMapOf()
        var scorePlayers : MutableMap<String, Int> = mutableMapOf()
        myRef.child(localGameID).child("players").get().addOnSuccessListener {
            val snapshot = it
            for (player in snapshot.children){
                Log.d("player","${player}")
                gamePlayer?.put(player.key.toString(),PlayerStatus.ALIVE)
                scorePlayers?.put(player.key.toString(),0)
                Log.d("players","${gamePlayer}")
            }

            Log.d("currentPlayer","${gamePlayer.keys.elementAt(Random.nextInt(gamePlayer.size))}")

            if (gamePlayer.size>1){
                RouletteData.saveGameModel(
                    RouletteModel(
                        gameId = localGameID,
                        players = gamePlayer,
                        gameStatus = GameStatus.INPROGRESS,
                        attempts = 0,
                        laps = 0,
                        score = scorePlayers,
                        nbrOfPlayers = gamePlayer.size,
                        aliveCount = gamePlayer.size,
                        luckyNumber = Random.nextInt(6) + 1,
                        currentPlayer = gamePlayer.keys.elementAt(Random.nextInt(gamePlayer.size))
                    ),localGameID
                )
                LobbyData.saveLobbyModel(
                    LobbyModel(
                        gameID = localGameID,
                        btnPressed = true
                    ),localGameID
                )
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                view?.findNavController()?.navigate(R.id.action_lobbyFragment_to_gavleRouletteFragment)
                // set the btnPressed to true if anny player presses it then every player goes to the same game
            }
        }
    }
    fun setUI() {
        Log.d("setUI","I setUI")
        myRef.child(localGameID).child("players").get().addOnSuccessListener {
            val dataSnapshot = it
            var i = 1
            for (player in dataSnapshot.children) {
                var index = i.toString()
                var resId = resources.getIdentifier(
                    "astro_${player.child("color").value}",
                    "drawable",
                    requireContext().packageName)

                val playerId = resources.getIdentifier(
                    "player_${index}",
                    "id",
                    requireContext().packageName
                )
                val playerImageView =
                    binding.root.findViewById<ImageView>(playerId)
                playerImageView.setImageResource(resId)
                playerImageView.visibility = View.VISIBLE

                val playerTextId = resources.getIdentifier(
                    "player_${index}_text",
                    "id",
                    requireContext().packageName
                )
                val playerTextView =
                    binding.root.findViewById<TextView>(playerTextId)
                playerTextView.text = player.child("nickname").value.toString()
                playerTextView.visibility = View.VISIBLE
                i++
            }
        }
    }
    fun updateLobby(model: LobbyModel, id : String){
        LobbyData.saveLobbyModel(model, id)
    }
    /*
    sets the players that are in the lobby visible
     */
    fun setVisiblePlayers(){

    }

    fun setHost(){



    }

    //hämtar alla spelare från databasen och lägger till dem i lobby och spara deras spelarID för
    //enkelt kunna ändra UI beronde på spelare i lobbyn
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onStop() {
        super.onStop()
        lobbyRef.removeEventListener(gameStatusListener)
        println("LOBBYFRAG: JAG HAR STOP")
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

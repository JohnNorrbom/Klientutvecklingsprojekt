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
/**
 *
 * LobbyFragment:
 *
 * Lobby när man väntar på att spelare ska joina
 *
 * @author William
 *
 */

class LobbyFragment : Fragment() {
    private var _binding: FragmentLobbyBinding? = null
    private val binding get()  = _binding!!
    private var lobbyModel : LobbyModel? = null
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

        LobbyData.lobbyModel.observe(this) { lobbyModel ->
            lobbyModel?.let {
                this@LobbyFragment.lobbyModel = it
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

    fun startGame(){
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        lobbyRef.child(localGameID).child("btnPressed").setValue(true)
        view?.findNavController()?.navigate(R.id.action_lobbyFragment_to_testBoardFragment)
    }
    //Lägger in alla spelare som är med i lobbyn
    fun setUI() {
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

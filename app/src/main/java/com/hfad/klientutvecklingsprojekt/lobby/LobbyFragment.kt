package com.hfad.klientutvecklingsprojekt.lobby

import android.content.ContentValues
import android.content.pm.ActivityInfo
import android.os.Bundle
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
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel
import com.hfad.klientutvecklingsprojekt.soccer.SoccerData


class LobbyFragment : Fragment() {
    private var _binding: FragmentLobbyBinding? = null
    private val binding get()  = _binding!!
    private var lobbyModel : LobbyModel? = null
    private var meModel : MeModel?= null//den här
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Lobby Data")
    val myRefPlayer = database.getReference("Player Data")
    var currentGameID =""
    var currentPlayerID =""

    //host - den som kommer först in
    var playerIsHost: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentLobbyBinding.inflate(inflater,container,false)
        val view = binding.root

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
        //den här
        MeData.meModel.observe(this) { meModel ->
            meModel?.let {
                this@LobbyFragment.meModel = it
                setText()
            } ?: run {
                // Handle the case when meModel is null
                Log.e("LobbyFragment", "meModel is null")
            }
        }
        println("Me model in LobbyFragment"+meModel)
        println("GameId in LobbyFragment: " + currentGameID)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        super.onViewCreated(view, savedInstanceState)
    }
    fun setText(){
        //den här
        currentGameID= meModel?.gameID?:""
        currentPlayerID = meModel?.playerID?:""
        Log.d("meModel","player ${currentPlayerID} Game ${currentGameID}")
    }

    fun startGame(){
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        view?.findNavController()?.navigate(R.id.action_lobbyFragment_to_testBoardFragment)
    }
    fun setUI() {
        Log.d("setUI","I setUI")
        myRef.child(currentGameID).get().addOnSuccessListener {
            val dataSnapshot = it
            var i = 1
            for (player in dataSnapshot.children) {
                var resId = resources.getIdentifier(
                    "astro_${player.child("color").value}",
                    "drawable",
                    requireContext().packageName)

                val playerId = resources.getIdentifier(
                    "player_${i}",
                    "id",
                    requireContext().packageName
                )
                val playerImageView =
                    binding.root.findViewById<ImageView>(playerId)
                playerImageView.setImageResource(resId)
                playerImageView.visibility = View.VISIBLE

                val playerTextId = resources.getIdentifier(
                    "player_${i}_text",
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
    fun updatePlayerData(model: PlayerModel,gameID : String) {
        PlayerData.savePlayerModel(model,gameID)
    }
    fun updateLobbyData(model: LobbyModel) {
        LobbyData.saveLobbyModel(model)
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
}

package com.hfad.klientutvecklingsprojekt.lobby

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.databinding.FragmentLobbyBinding
import com.hfad.klientutvecklingsprojekt.gamestart.CharacterStatus
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel


class LobbyFragment : Fragment() {
    private var _binding: FragmentLobbyBinding? = null
    private val binding get()  = _binding!!
    private var lobbyModel : LobbyModel? = null
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val lobbyRef = database.getReference("Lobby")
    val playerRef = database.getReference("PLayers")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLobbyBinding.inflate(inflater,container,false)
        val view = binding.root

        LobbyData.lobbyModel.observe(this){
            lobbyModel = it
            setUI()
        }
        lobbyRef.addValueEventListener(lobbyListener)
        playerRef.addValueEventListener(playerListener)
        addPlayersToLobby()
        return view;
    }
    val lobbyListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            lobbyModel?.apply {
                val gameModel = dataSnapshot.child(gameID ?: "").getValue(LobbyModel::class.java)
                if (gameModel != null) {
                    updateLobbyData(gameModel)
                    setUI()
                }
            }
            // Get Post object and use the values to update the UI

            // ...
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
        }
    }

    val playerListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            lobbyModel?.apply {
                val playerIdsList = mutableListOf<String>()

                for (childSnapshot in dataSnapshot.children) {
                    val gameModel = childSnapshot.getValue(PlayerModel::class.java)
                    if (gameModel != null && gameModel.gameID == gameID) {
                        // Add the playerId to the list if the gameID matches
                        playerIdsList.add(gameModel?.playerID ?: "")
                    }
                }
                lobby = playerIdsList
                updateLobbyData(this)
                setUI()
                }
            }
            // Get Post object and use the values to update the UI

            // ...
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

    fun setUI() {
        lobbyModel?.apply {
            addPlayersToLobby()
            val size = lobby?.size ?: 0
            for (i in 0 until size){
                playerRef.child(lobby?.get(i) ?: "").get().addOnSuccessListener {
                    val snapshot = it
                    for(i in 0 until size){
                        var resId = resources.getIdentifier(
                            "astro_${snapshot.child("color").value}",
                            "drawable",
                            requireContext().packageName
                        )
                        val playerId = resources.getIdentifier(
                            "player_${i+1}",
                            "id",
                            requireContext().packageName
                        )
                        val playerImageView =
                            binding.root.findViewById<ImageView>(playerId)
                        playerImageView.setImageResource(resId)
                        playerImageView.visibility = View.VISIBLE

                        val playerTextId = resources.getIdentifier(
                            "player_${i+1}_text",
                            "id",
                            requireContext().packageName
                        )
                        val playerTextView =
                            binding.root.findViewById<TextView>(playerTextId)
                        playerTextView.text = snapshot.child("nickname").value.toString()
                        playerTextView.visibility = View.VISIBLE
                    }
                }
            }


        }
    }
    //hämtar alla spelare från databasen och lägger till dem i lobby och spara deras spelarID för
    //enkelt kunna ändra UI beronde på spelare i lobbyn
    fun addPlayersToLobby(){
        lobbyModel?.apply {
            playerRef.get().addOnSuccessListener {
                val snapshot = it
                var playerList = mutableListOf<String>()
                for (player in snapshot.children) {
                    if (player.child("gameID").value == gameID) {
                        playerList.add(player.child("playerID").value.toString())
                    }
                }
                lobby = playerList
                updateLobbyData(this)
            }
        }
    }


    fun updateLobbyData(model: LobbyModel){
        LobbyData.saveLobbyModel(model)
    }

}

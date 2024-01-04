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
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel


class LobbyFragment : Fragment() {
    private var _binding: FragmentLobbyBinding? = null
    private val binding get()  = _binding!!
    private var lobbyModel : LobbyModel? = null
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val playerRef = database.getReference("Game Lobby")
    val currentGameID = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentLobbyBinding.inflate(inflater,container,false)
        val view = binding.root

        //  Button for starting game, loading BoardFragment. Everyone can click it right now.
        binding.startButton.setOnClickListener {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            view.findNavController().navigate(R.id.action_lobbyFragment_to_boardFragment)
        }
        return view
    }

    fun setUI() {
        playerRef.get().addOnSuccessListener {
            val dataSnapshot = it
            val players = dataSnapshot.child(lobbyModel?.gameID?:"")
            var i = 1
            for (player in players.children) {
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
    //hämtar alla spelare från databasen och lägger till dem i lobby och spara deras spelarID för
    //enkelt kunna ändra UI beronde på spelare i lobbyn

}

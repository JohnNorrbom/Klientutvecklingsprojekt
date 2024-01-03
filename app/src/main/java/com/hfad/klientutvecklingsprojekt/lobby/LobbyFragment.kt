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
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerInfoFragmentArgs
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerInfoFragmentDirections
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel


class LobbyFragment : Fragment() {
    private var _binding: FragmentLobbyBinding? = null
    private val binding get()  = _binding!!
    private var lobbyModel : LobbyModel? = null
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Space Party").child("Players")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLobbyBinding.inflate(inflater,container,false)
        val view = binding.root
        addPlayer()

        LobbyData.lobbyModel.observe(this){
            lobbyModel = it
            setUI()
        }
        myRef.addValueEventListener(lobbyListener)
        //  Button for starting game, loading BoardFragment. Everyone can click it right now.
        binding.testBtn.setOnClickListener {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            // For safeargs
            val gameID = LobbyFragmentArgs.fromBundle(requireArguments()).gameID
            val action = LobbyFragmentDirections.actionLobbyFragmentToBoardFragment(gameID)
            view.findNavController().navigate(action)

            //view.findNavController().navigate(R.id.action_lobbyFragment_to_boardFragment)
        }
        return view;
    }
    val lobbyListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            lobbyModel?.apply {
                val gameModel = dataSnapshot.child(playerID).getValue(LobbyModel::class.java)
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
    fun addPlayer(){
        lobbyModel?.apply {
            participants.add(Pair(nickname,color))
            updateLobbyData(this)
        }
    }
    fun setUI() {
        lobbyModel?.apply {
            for(i in 0 until participants.size){
                var resId = resources.getIdentifier(
                    "astro_${participants[i].second}",
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
                playerTextView.text = participants[i].first
                playerTextView.visibility = View.VISIBLE
            }
        }
    }


    fun updateLobbyData(model: LobbyModel){
        LobbyData.saveLobbyModel(model)
    }

}

package com.hfad.klientutvecklingsprojekt.playerinfo

import android.content.ContentValues.TAG
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentPlayerInfoBinding
import com.hfad.klientutvecklingsprojekt.lobby.LobbyData
import com.hfad.klientutvecklingsprojekt.lobby.LobbyModel
import kotlin.random.Random
import kotlin.random.nextInt


class PlayerInfoFragment : Fragment() {
    private var _binding: FragmentPlayerInfoBinding? = null
    private val binding get() = _binding!!
    private var playerModel: PlayerModel? = null
    private val characterColors = listOf("white", "red", "blue", "green", "yellow")
    var playerColor = ""
    val database =
        Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Space Party")
    val gameListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            playerModel?.apply {
                val gameModel = dataSnapshot.child(gameID ?: "").getValue(PlayerModel::class.java)
                if (gameModel != null) {
                    updatePlayerData(gameModel)
                    setUI()
                }
            }
            // Get Post object and use the values to update the UI
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerInfoBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.confirmBtn.setOnClickListener {
            setPlayerInfo()
            confirmCharacter()
        }
        PlayerData.playerModel.observe(this) {
            playerModel = it
            setUI()
        }
        myRef.addValueEventListener(gameListener)
        return view
    }

    fun confirmCharacter() {
        val currentPlayerID = Random.nextInt(1000..9999).toString()
        val playerName = binding.nicknameInput.text.toString()
        if (playerName == null) {
            binding.nicknameInput.error = (getText(R.string.enter_user_name))
        }
        var currentGameID = ""
        playerModel?.apply {
            currentGameID = gameID ?: ""
        }
        if (playerColor == "") {
            binding.confirmBtn.error = (getText(R.string.choose_character))
        }
        LobbyData.saveLobbyModel(
            LobbyModel(
                gameID = currentGameID,
                playerID = currentPlayerID,
                nickname = playerName,
                color = playerColor
            )
        )
//        var newPlayer = ArrayList<LobbyModel>()
//        newPlayer.add(LobbyModel())
//        for(i in 0 until (playerModel?.players?.size ?: 1)) {
//            newPlayer.add(playerModel.players.get(i))
//        }
//        PlayerModel(
//            //  var players: ArrayList<LobbyModel>? = null
//            players = newPlayer
//        )
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        //  Crashes after executing the line below
        view?.findNavController()?.navigate(R.id.action_playerInfoFragment_to_lobbyFragment)
    }

    fun setUI() {
        playerModel?.apply {
            if ((takenPosition?.size ?: 1) <= 5) {
                //  Changes text for TextView to the lobby gameID
                binding.gameId.text = "${getText(R.string.game_ID)}${gameID}"
                //  Loops through all 5 characters to see which of them are taken
                for (i in 0 until characterColors.size) {
                    if (takenPosition?.get(characterColors[i]) == CharacterStatus.TAKEN) {
                        //  Find Id for new picture
                        val resId = resources.getIdentifier(
                            "astro_${characterColors[i]}_taken",
                            "drawable",
                            requireContext().packageName
                        )
                        //  Find Id for picture
                        val astroId = resources.getIdentifier(
                            "astro_${characterColors[i]}",
                            "id",
                            requireContext().packageName
                        )
                        //  Apply changes to ImageView
                        val characterImageView = binding.root.findViewById<ImageView>(astroId)
                        characterImageView.setImageResource(resId)
                        //  Get id of radio button
                        val radioId = resources.getIdentifier(
                            "radio_btn_${characterColors[i]}",
                            "id",
                            requireContext().packageName
                        )
                        //  Removes radio button
                        val radioBtn = binding.root.findViewById<RadioButton>(radioId)
                        radioBtn.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    fun setPlayerInfo() {
        val colors = mutableListOf(
            Pair("white", binding.radioBtnWhite),
            Pair("blue", binding.radioBtnBlue),
            Pair("red", binding.radioBtnRed),
            Pair("green", binding.radioBtnGreen),
            Pair("yellow", binding.radioBtnYellow)
        )
        for (color in colors) {
            if (color.second.isChecked) {
                playerModel?.apply {
                    Log.d("inför Apply", "bra")
                    //  Set position to taken
                    takenPosition?.set(color.first, CharacterStatus.TAKEN)
                    playerColor = color.first
                    Log.d("takenPosition", "taken: ${takenPosition}")
                    Log.d("this", "this: ${this}")
                    updatePlayerData(this)
                }
            }
        }
    }

    fun updatePlayerData(model: PlayerModel) {
        PlayerData.savePlayerModel(model)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
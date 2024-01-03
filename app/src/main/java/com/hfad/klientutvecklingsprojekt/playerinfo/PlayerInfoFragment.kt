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
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentPlayerInfoBinding
import com.hfad.klientutvecklingsprojekt.gamestart.CharacterStatus
import com.hfad.klientutvecklingsprojekt.gamestart.GameData
import com.hfad.klientutvecklingsprojekt.gamestart.GameModel
import com.hfad.klientutvecklingsprojekt.gamestart.GameStartFragment
import com.hfad.klientutvecklingsprojekt.gamestart.GameStartFragmentDirections
import com.hfad.klientutvecklingsprojekt.lobby.LobbyData
import com.hfad.klientutvecklingsprojekt.lobby.LobbyModel
import kotlin.random.Random
import kotlin.random.nextInt


class PlayerInfoFragment : Fragment() {
    private var _binding: FragmentPlayerInfoBinding? = null
    private val binding get()  = _binding!!
    private var playerModel : PlayerModel? = null
    private var gameModel : GameModel? = null
    private val characterColors = listOf("white","red","blue","green","yellow")
    private var playerColor = ""
    private var playerName = ""
    private var currentGameID = ""
    private var currentPlayerID = ""
    private val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Game Lobby")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerInfoBinding.inflate(inflater, container, false)
        val view = binding.root
       binding.confirmBtn.setOnClickListener{
           confirmCharacter()
       }

        GameData.gameModel.observe(this) {
            gameModel = it
            setUI()
        }

        myRef.addValueEventListener(gameListener)
        return view
    }

    fun confirmCharacter() {
        currentPlayerID = Random.nextInt(1000..9999).toString()
        playerName = binding.nicknameInput.text.toString()
        if (playerName == "") {
            binding.nicknameInput.error = (getText(R.string.enter_user_name))
            return
        }

        checkName { isNameTaken ->
            if (isNameTaken) {
                binding.nicknameInput.error = getText(R.string.enter_valid_user_name)
                return@checkName
            }

            setPlayerInfo()

            if (playerColor == "") {
                Toast.makeText(
                    requireContext().applicationContext,
                    getText(R.string.choose_character),
                    Toast.LENGTH_SHORT
                ).show()
                return@checkName
            }

            playerModel?.apply {
                playerID = currentPlayerID
                nickname = playerName
                color = playerColor

                updatePlayerData(this)
            }

            Log.d("playermodel","playerModel ${playerModel}")
            LobbyData.saveLobbyModel(
                LobbyModel(
                    gameID = gameModel?.gameID,
                    players = playerModel
                )
            )
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            view?.findNavController()?.navigate(R.id.action_playerInfoFragment_to_lobbyFragment)
        }

        //  Crashes after executing the line below
        // view?.findNavController()?.navigate(R.id.action_playerInfoFragment_to_lobbyFragment)
    }
    fun checkName(callback: (Boolean) -> Unit) {
        myRef.child(gameModel?.gameID?:"").get().addOnSuccessListener {
            val snapshot = it
            Log.d("snap children", "children: ${snapshot.children}")
            for(player in snapshot.children){
                if (player.child("nickname").value == playerName){
                    callback(true)
                    return@addOnSuccessListener
                }
            }
            callback(false)
        }.addOnFailureListener {
            callback(false)
        }
    }

    //update UI
    fun setUI() {
        gameModel?.apply {
            //  Changes text for TextView to the lobby gameID
            binding.gameId.text = "${getText(R.string.game_ID)}${gameID}"
            //  Loops through all 5 character colors to see which of them are taken
            for (i in 0 until characterColors.size) {
                // if a player color is taken
                if (takenPosition?.get(characterColors[i]) == CharacterStatus.TAKEN) {
                    //  Find picture
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
                    //  Makes radio button Invisible
                    val radioBtn = binding.root.findViewById<RadioButton>(radioId)
                    radioBtn.visibility = View.INVISIBLE
                }

                // if player color is free
                if (takenPosition?.get(characterColors[i]) == CharacterStatus.FREE) {
                    // get picture
                    val resId = resources.getIdentifier(
                        "astro_${characterColors[i]}_free",
                        "drawable",
                        requireContext().packageName
                    )
                    //get id for Imageview
                    val astroId = resources.getIdentifier(
                        "astro_${characterColors[i]}",
                        "id",
                        requireContext().packageName
                    )
                    // change imageView
                    val characterImageView =
                        binding.root.findViewById<ImageView>(astroId)
                    characterImageView.setImageResource(resId)

                    // get id for radio button and makes it visible
                    val radioId = resources.getIdentifier(
                        "radio_btn_${characterColors[i]}",
                        "id",
                        requireContext().packageName
                    )
                    val radioBtn = binding.root.findViewById<RadioButton>(radioId)
                    radioBtn.visibility = View.VISIBLE
                }
            }
        }
    }

    val gameListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            gameModel?.apply {
                val gameModel = dataSnapshot.child(gameID ?: "").getValue(PlayerModel::class.java)
                if (gameModel != null) {
                    // Check if the data has changed before updating and setting UI
                    if (!gameModel.equals(this)) {
                        updatePlayerData(gameModel)
                        setUI()
                    }
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
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
                gameModel?.apply {
                    Log.d("inför Apply", "bra")
                    for (color in colors) {
                        if (color.second.isChecked) {
                            gameModel?.apply {
                                Log.d("inför Apply", "bra")
                                //  Set position to taken
                                takenPosition?.set(color.first, CharacterStatus.TAKEN)
                                playerColor = color.first
                                Log.d("takenPosition", "taken: ${takenPosition}")
                                Log.d("this", "this: ${this}")
                                updateGameData(this)
                            }
                        }
                    }
                }
            }
        }
    }

    fun updatePlayerData(model: PlayerModel) {
        PlayerData.savePlayerModel(model)
    }
    fun updateGameData(model: GameModel){
        GameData.saveGameModel(model)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
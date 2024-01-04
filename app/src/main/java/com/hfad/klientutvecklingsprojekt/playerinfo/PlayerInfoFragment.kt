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
import com.hfad.klientutvecklingsprojekt.gamestart.GameData.gameModel
import com.hfad.klientutvecklingsprojekt.gamestart.GameModel
import com.hfad.klientutvecklingsprojekt.gamestart.Progress
import com.hfad.klientutvecklingsprojekt.lobby.LobbyData
import com.hfad.klientutvecklingsprojekt.lobby.LobbyModel
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import kotlin.random.Random
import kotlin.random.nextInt


class PlayerInfoFragment : Fragment() {
    private var _binding: FragmentPlayerInfoBinding? = null
    private val binding get()  = _binding!!
    private var playerModel : PlayerModel? = null
    private var meModel : MeModel?= null
    private var gameModel : GameModel? = null
    private val characterColors = listOf("white","red","blue","green","yellow")
    private var playerColor = ""
    private var playerName = ""
    private var currentGameID = ""
    private var currentPlayerID = ""
    private val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Game Data")
    private val lobbyRef = database.getReference("Lobby Data")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerInfoBinding.inflate(inflater, container, false)
        val view = binding.root
        GameData.fetchGameModel()
       binding.confirmBtn.setOnClickListener{
           confirmCharacter()
       }

        GameData.gameModel.observe(this) {
            gameModel = it
            setUI()
        }

        return view
    }

    fun confirmCharacter() {
        currentGameID = gameModel?.gameID?:""
        myRef.child(currentGameID).child("status").get().addOnSuccessListener {
            if (it.value == Progress.FINISHED) {
                Toast.makeText(
                requireContext().applicationContext,
                getText(R.string.game_is_full),
                Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
        }
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
            gameModel?.apply {
                takenPosition?.put(playerColor, CharacterStatus.TAKEN)
                updateGameData(this)
            }
            PlayerData.savePlayerModel(
                PlayerModel(
                    playerID = currentPlayerID,
                    nickname = playerName,
                    color = playerColor
                ), currentGameID
            )

            LobbyData.saveLobbyModel(
                LobbyModel(
                    gameID = currentGameID
                )
            )
            checkSizeOfLobby()


            MeData.saveMeModel(
                MeModel(
                    gameID = currentGameID,
                    playerID = currentPlayerID
                )
            )
            }



            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            view?.findNavController()?.navigate(R.id.action_playerInfoFragment_to_lobbyFragment)
        }

        //  Crashes after executing the line below
        // view?.findNavController()?.navigate(R.id.action_playerInfoFragment_to_lobbyFragment)
    fun checkName(callback: (Boolean) -> Unit) {
        lobbyRef.child(gameModel?.gameID?:"").get().addOnSuccessListener {
            var check = false
            Log.d("player","${it.child("players").child(playerName).child("nickname").value}")
           Log.d("realName","${playerName}")
            if (it.child("players").child(playerName).child("nickname").value == playerName){
                check = true
            }
            callback(check)
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

    fun checkSizeOfLobby() {
        gameModel?.apply {
            if (status != Progress.FINISHED) {
                var allPositionsTaken = true

                for (i in 0 until characterColors.size) {
                    if (takenPosition?.get(characterColors[i]) != CharacterStatus.TAKEN) {
                        allPositionsTaken = false
                        break
                    }
                }

                Log.d("checkSizeOfLobby", "allPositionsTaken: $allPositionsTaken")

                if (allPositionsTaken) {
                    // Kontrollera om statusen faktiskt ändras innan du uppdaterar
                    if (status != Progress.FINISHED) {
                            GameData.saveGameModel(
                                GameModel(
                                    gameID = gameID,
                                    status = Progress.FINISHED,
                                    takenPosition= takenPosition
                                )
                            )
                        Log.d("checkSizeOfLobby", "Status updated to FINISHED")
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
                gameModel?.apply {
                    Log.d("inför Apply", "bra")
                    for (color in colors) {
                        if (color.second.isChecked) {
                            Log.d("inför Apply", "bra")
                            //  Set position to taken
                            playerColor = color.first
                            Log.d("takenPosition", "taken: ${takenPosition}")
                            Log.d("this", "this: ${this}")
                        }
                    }
                }
            }
        }
    }


    fun updateGameData(model: GameModel){
        GameData.saveGameModel(model)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
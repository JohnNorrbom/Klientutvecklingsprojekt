package com.hfad.klientutvecklingsprojekt.playerinfo

import android.content.ContentValues.TAG
import android.content.pm.ActivityInfo
import android.graphics.Color
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
import android.widget.RadioButton
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.values
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.board.BoardData
import com.hfad.klientutvecklingsprojekt.board.BoardModel
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

/**
 *
 * PlayerInfoFragment:
 *
 * Används för att spara valen som en spelare gör om vad de vill heta och vilken färg på gubbe de vill ha
 *
 * @author William
 *
 */
//
//  PLAYERDATA IS SAVED AT LOBBY DATA
//

class PlayerInfoFragment : Fragment() {
    private var _binding: FragmentPlayerInfoBinding? = null
    private val binding get()  = _binding!!
    private var gameModel : GameModel? = null
    private val characterColors = listOf("white","red","blue","green","yellow")
    private var playerColor = ""
    private var playerName = ""
    private var currentGameID = ""
    private var currentPlayerID = ""
    private val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Game Data")
    private val playerRef = database.getReference("Player Data")
    private val boardRef = database.getReference("Board Data")

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

    //Spara valen för spelaren om de klarar alla tester och navigerar till lobbyfragment
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
            currentPlayerID = Random.nextInt(1000..9999).toString()
            playerName = binding.nicknameInput.text.toString()
            Log.d("playerName","${playerName}")
            if (playerName == "") {
                binding.nicknameInput.error = (getText(R.string.enter_user_name))
                return@addOnSuccessListener
            }
            checkName { isNameTaken ->
                Log.d("isNameTaken","${isNameTaken}")
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
                LobbyData.saveLobbyModel(
                    LobbyModel(
                        gameID = currentGameID,
                        btnPressed = false
                    ),currentGameID
                )
                PlayerData.savePlayerModel(
                    PlayerModel(
                        playerID = currentPlayerID,
                        nickname = playerName,
                        color = playerColor,
                        position = 0,
                        score = 0

                    ), currentGameID
                )

                checkSizeOfLobby()
                MeData.saveMeModel(
                    MeModel(
                        gameID = currentGameID,
                        playerID = currentPlayerID
                    )
                )


                var playerCountRef = boardRef.child(currentGameID).child("playerCount")
                playerCountRef.get().addOnSuccessListener {
                    var playerCount = it.value.toString().toInt()
                    if (playerCount == 0){
                        boardRef.child(currentGameID).child("currentPlayerId").setValue(currentPlayerID)
                    }
                    playerCount++
                    boardRef.child(currentGameID).child("playerCount").setValue(playerCount)
                }
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                view?.findNavController()?.navigate(R.id.action_playerInfoFragment_to_lobbyFragment)
            }

        }
    }

    //Avgör om ett nickname redan existerar i lobbyn
    fun checkName(callback: (Boolean) -> Unit) {
        playerRef.child(gameModel?.gameID?:"").child("players").get().addOnSuccessListener {
            val snapshot = it
            var check = false
            for(player in snapshot.children){
                if (player.child("nickname").value.toString() == playerName) {
                    Log.d(
                        "player",
                        "${player.child("nickname").value.toString()}"
                    )
                    Log.d("realName", "${playerName}")
                    check = true
                }
            }
            callback(check)
        }.addOnFailureListener {
            callback(false)
        }
    }

    //updaterar Ui för gubbar som är tagna
    fun setUI() {
        gameModel?.apply {
            //  Changes text for TextView to the lobby gameID
            val text = "${getText(R.string.game_ID)}${gameID}"
            val spannableString = SpannableString(text)
            // Get the length of the text
            val textLength = text.length
            // Set the color for the first half of the text to green
            spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#6AFF00")), 0, textLength - 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            // Set the color for the second half of the text to red
            spannableString.setSpan(ForegroundColorSpan(Color.RED), textLength - 4, textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.gameId.text = spannableString
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

    //Avgör storleken på lobbyn
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

    //avgör vilken gubbe som spelaren har valt
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
                    for (color in colors) {
                        if (color.second.isChecked) {
                            //  Set position to taken
                            playerColor = color.first

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
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
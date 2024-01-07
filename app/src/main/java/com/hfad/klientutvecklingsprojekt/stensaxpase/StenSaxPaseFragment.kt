package com.hfad.klientutvecklingsprojekt.stensaxpase

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.databinding.FragmentStenSaxPaseBinding
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import android.os.Handler
import androidx.navigation.findNavController
import com.hfad.klientutvecklingsprojekt.R

class StenSaxPaseFragment : Fragment() {

    private var _binding: FragmentStenSaxPaseBinding? = null
    private val binding get() = _binding!!

    private var meModel : MeModel? = null

    var currentGameID : String = ""
    var currentPlayerID : String = ""

    val handler = Handler()

    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val stenSaxPaseRef = database.getReference("Sten Sax Pase")
    private val playerDataRef = database.getReference("Player Data")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // establish binding
        _binding = FragmentStenSaxPaseBinding.inflate(inflater, container, false)
        val view = binding.root

        MeData.meModel.observe(context as LifecycleOwner) { meModel ->
            meModel?.let {
                this@StenSaxPaseFragment.meModel = it
                setText()

                setID(currentGameID, currentPlayerID)

            } ?: run {
                // Handle the case when meModel is null
                Log.e("StenSaxPaseFragment", "meModel is null")
            }
        }

        // initialize game
        initGame()

        // add view code here
        val sten = binding.sten
        val sax = binding.sax
        val pase = binding.pase

        sten.setOnClickListener {
            sax.visibility = View.INVISIBLE
            pase.visibility = View.INVISIBLE
            sten.isClickable = false
            setChoice("sten", currentPlayerID)
        }
        sax.setOnClickListener {
            sten.visibility = View.INVISIBLE
            pase.visibility = View.INVISIBLE
            sax.isClickable = false
            setChoice("sax", currentPlayerID)
        }
        pase.setOnClickListener {
            sten.visibility = View.INVISIBLE
            sax.visibility = View.INVISIBLE
            pase.isClickable = false
            setChoice("pase", currentPlayerID)
        }

        stenSaxPaseRef.addValueEventListener(gameStartListener)
        stenSaxPaseRef.addValueEventListener(gameStatusListener)

        return view
    }

    fun setText() {
        currentGameID = meModel?.gameID ?: ""
        currentPlayerID = meModel?.playerID ?: ""
        Log.d("StenSaxPaseFragment", "playerID: ${currentPlayerID} GameID: ${currentGameID}")
    }

    private fun setActionText(text: String) {
        binding.actionText.text = text
    }

    private fun setPlayerScore(score:String) {
        binding.scorePlayerID.text = score
    }

    private fun setOpponentScore(score:String) {
        binding.scoreOpponentID.text = score
    }

    private fun setVsText(text:String) {
        binding.vsText.text = text
    }
/*
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

 */

    // Code starting from here was originally in view model

    private var stenSaxPaseModel : StenSaxPaseModel? = null

    var playerID: String? = ""
    var gameID: String = ""
    var opponentID: String? = ""

    fun setID(currentGameID:String, currentPlayerID:String) {
        gameID = currentGameID
        playerID = StenSaxPaseFragmentArgs.fromBundle(requireArguments()).playerID
        opponentID = StenSaxPaseFragmentArgs.fromBundle(requireArguments()).opponentID
        println("opp id: $opponentID")
    }

    fun initGame() {
        println("Currently in game: $gameID , using playerID: $playerID")
        // Load players from lobby
        if(playerID == "null") {
            stenSaxPaseRef.child(gameID).get().addOnSuccessListener {
                for(elem in it.children) {
                    if(elem.key == "playerID") opponentID = elem.value.toString()
                    if(elem.key == "opponentID") playerID = elem.value.toString()
                }
                println("!!PLAYERID WAS NULL!! Currently in game: $gameID , using playerID: $playerID")
                resetUI()
                loadPlayersFromGameID()
            }
        } else {
            resetUI()
            loadPlayersFromGameID()
        }
    }

    var player1:String? = null
    var player2:String? = null

    var player1Score:Int? = null
    var player2Score:Int? = null

    fun loadPlayersFromGameID() {
        playerDataRef.child(gameID).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            // Prepare Map which players will go into
            val players : MutableMap<String,MutableMap<String,String>> = mutableMapOf()
            // Loop through all players and add to local 'players' Map
            for(player in it.child("players").children) {
                if(player.key == playerID) player1 = player.child("nickname").value.toString()
                else if(player.key == opponentID) player2 = player.child("nickname").value.toString()
                if(player.key == playerID) player1Score = 0
                else if(player.key == opponentID) player2Score = 0
                players.put("${player.child("playerID").value}", mutableMapOf(
                    "nickname" to "${player.child("nickname").value}",
                    "color" to "${player.child("color").value}",
                    "choice" to "null",
                    "score" to "0"
                ))
            }
            savePlayersToDatabase(players!!)
            setPlayers()
        }
    }

    fun savePlayersToDatabase(players : MutableMap<String,MutableMap<String,String>>) {
        if(playerID != "null") {
            println("$playerID __VS__ $opponentID")
            stenSaxPaseModel = StenSaxPaseModel(gameID, true, players, playerID, opponentID)
            stenSaxPaseRef.child(gameID).setValue(stenSaxPaseModel)
        }
    }

    // Maybe remove this whole setPlayers thing and instead use solution where a player chooses who to compete with



    fun setPlayers() {
        println("player1_id: $player1 -vs- player2_id: $player2")
        setVsText("$player1 -vs- $player2")
        setPlayerScore("$player1 score: $player1Score")
        setOpponentScore("$player2 score: $player2Score")
    }

    // End of setPlayers

    private val gameStartListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            stenSaxPaseRef.child(gameID).child("players").get().addOnSuccessListener { dataSnapshot ->
                if(dataSnapshot.exists()){
                    getChoiceFromDatabase()
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    private val gameStatusListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            stenSaxPaseRef.child(gameID).child("status").get().addOnSuccessListener { dataSnapshot ->
                try {
                    if (dataSnapshot.exists()) {
                        println("CHECK IF STATUS IS FALSE FOR: ${dataSnapshot.getValue()} , ${dataSnapshot.getValue() == false}")
                        if(dataSnapshot.getValue() == false) {
                            stenSaxPaseRef.child(gameID).removeValue()
                            view?.findNavController()?.navigate(R.id.action_stensaxpaseFragment_to_testBoardFragment)
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

    fun setChoice(choice:String, player:String) {
        //playerMap.forEach { entry -> if(entry.value.containsValue("choice\"") ) }
        println("$player chose: $choice")
        if(choice != "null") {
            if (player == playerID) setChoiceInDatabase(choice, playerID!!)
            else if (player == opponentID) setChoiceInDatabase(choice, opponentID!!)
        }
    }

    fun setChoiceInDatabase(choice:String, player:String) {
        stenSaxPaseRef.child(gameID).child("players").child(player).child("choice").setValue(choice)
    }

    fun getChoiceFromDatabase() {
        val player1choiceRef = stenSaxPaseRef.child(gameID).child("players").child(playerID!!).child("choice")
        val player2choiceRef = stenSaxPaseRef.child(gameID).child("players").child(opponentID!!).child("choice")
        var player1choice:Any?
        var player2choice:Any?
        player1choiceRef.get().addOnSuccessListener {
            player1choice = it.value
            player2choiceRef.get().addOnSuccessListener {
                player2choice = it.value
                if( (player1choice != "null" && player2choice != "null")  ) {
                    println(">>>>>>>>>player1 chose: $player1choice , player2 chose: $player2choice")
                    checkOutcome(player1choice, player2choice)
                }
            }
        }
    }

    fun checkOutcome(player1choice:Any?,player2choice:Any?) {
        var outcome = "round not started"
        println("$player1choice mot $player2choice")
        if(player1choice != "null" && player2choice != "null") {
            if(player1choice == player2choice) outcome = "even"
            else if(player1choice == "sten" && player2choice == "sax") outcome = playerID!!
            else if(player1choice == "sax" && player2choice == "pase") outcome = playerID!!
            else if(player1choice == "pase" && player2choice == "sten") outcome = playerID!!
            else outcome = opponentID!!
        }
        println("-outcome: $outcome")
        if(outcome == playerID) {

            player1Score = player1Score!! + 1

            if(player1Score == 2) {
                setActionText("$player1 WON THE WHOLE GAME WITH $player1choice BEATING $player2choice")
                setVsText("$player1 WON :D")
                setPlayerScore("")
                setOpponentScore("")
            } else {
                setActionText("$player1 won round with $player1choice beating $player2choice")
                setPlayerScore("$player1 score: $player1Score")
            }

                // Fördröjning
                handler.postDelayed({
                    setScoreInDatabase(playerID!!)
                }, 4000)

        }
        else if(outcome == opponentID) {

            player2Score = player2Score!! + 1

            if(player2Score == 2) {
                setActionText("$player2 WON THE WHOLE GAME WITH $player2choice BEATING $player1choice")
                setVsText("$player2 WON :D")
                setOpponentScore("")
                setPlayerScore("")
            } else {
                setActionText("$player2 won round with $player2choice beating $player1choice")
                setOpponentScore("$player2 score: $player2Score")
            }

                // Fördröjning
                handler.postDelayed({
                    setScoreInDatabase(opponentID!!)
                }, 4000)

        } else if(outcome == "even"){
            setActionText("round was $outcome")

            // Fördröjning
            handler.postDelayed({
                resetGame()
            }, 4000)
        }
    }

    fun setScoreInDatabase(player:String) {
        if(player == playerID) {
            if(player1Score!! < 2)stenSaxPaseRef.child(gameID).child("players").child(player).child("score").setValue(player1Score!!)
            println("-score: $player1Score , for player: $player")
            println("-updated score: $player1Score , for player: $player")
            resetGame()
            checkForWin(player, player1Score!!)
        } else if(player == opponentID) {
            if(player2Score!! < 2)stenSaxPaseRef.child(gameID).child("players").child(player).child("score").setValue(player2Score!!)
            println("-score: $player2Score , for player: $player")
            println("-updated score: $player2Score , for player: $player")
            resetGame()
            checkForWin(player, player2Score!!)
        }
    }

    fun resetGame() {
        if(player1Score!! < 2 && player2Score!! < 2) {

            stenSaxPaseRef.child(gameID).child("players").child(playerID!!).child("choice").setValue("null")
            stenSaxPaseRef.child(gameID).child("players").child(opponentID!!).child("choice").setValue("null")
            resetUI()
        }
    }

    fun resetUI() {
        if(currentPlayerID == playerID || currentPlayerID == opponentID) {
            binding.sten.isClickable = true
            binding.sax.isClickable = true
            binding.pase.isClickable = true

            binding.sten.visibility = View.VISIBLE
            binding.sax.visibility = View.VISIBLE
            binding.pase.visibility = View.VISIBLE
        } else {
            binding.sten.isClickable = false
            binding.sax.isClickable = false
            binding.pase.isClickable = false

            binding.sten.visibility = View.INVISIBLE
            binding.sax.visibility = View.INVISIBLE
            binding.pase.visibility = View.INVISIBLE
        }
    }

    fun checkForWin(player:String,score:Int) {
        if(score == 2) {
            println("player: $player wins")
            // Get 'score' of player who won
            playerDataRef.child(currentGameID).child("players").child(player).child("score").get().addOnSuccessListener {
                // Save score and add 10
                var boardScore = it.value.toString().toInt()
                boardScore += 10
                // Set new value
                playerDataRef.child(currentGameID).child("players").child(player).child("score").setValue(boardScore)

                stenSaxPaseRef.child(gameID).child("status").setValue(false)
                try {
                    database.getReference().child("Board Data").child(currentGameID).child("randomVal").setValue(-1)
                    stenSaxPaseRef.child(gameID).removeValue()
                    view?.findNavController()?.navigate(R.id.action_stensaxpaseFragment_to_testBoardFragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        else println("no win yet for player: $player")
    }
    override fun onStop() {
        super.onStop()
        stenSaxPaseRef.removeEventListener(gameStartListener)
        stenSaxPaseRef.removeEventListener(gameStatusListener)
        println("STENSAXPASE: JAG HAR STOP")
    }
}
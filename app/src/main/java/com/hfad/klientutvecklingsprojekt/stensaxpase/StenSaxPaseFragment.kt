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
    //private lateinit var viewModel: StenSaxPaseViewModel
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

        // establish viewModel
        //viewModel = ViewModelProvider(this).get(StenSaxPaseViewModel::class.java)

        MeData.meModel.observe(context as LifecycleOwner) { meModel ->
            meModel?.let {
                this@StenSaxPaseFragment.meModel = it
                setText()
                //viewModel.setID(currentGameID, currentPlayerID)
                setID(currentGameID, currentPlayerID)

                var stenSaxPaseStatusRef = stenSaxPaseRef.child(currentGameID).child("status")
                stenSaxPaseStatusRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        println("------------->STATUS OF LOBBY: ${dataSnapshot.getValue()}")
                        try {
                            if (dataSnapshot.exists()) {
                                println("CHECK IF STATUS IS FALSE FOR: ${dataSnapshot.getValue()} , ${dataSnapshot.getValue() == false}")
                                if(dataSnapshot.getValue() == false) {
                                    stenSaxPaseRef.child(gameID).setValue(null)
                                    view?.findNavController()?.navigate(R.id.action_stensaxpaseFragment_to_testBoardFragment)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Failed to read value
                        Log.w("YourTag", "Failed to read status of sten sax pase game.", databaseError.toException())
                    }
                })

            } ?: run {
                // Handle the case when meModel is null
                Log.e("StenSaxPaseFragment", "meModel is null")
            }
        }
        /*
        // For testing when mini-game is launched from start screen button
        if(currentGameID == "") {
            currentGameID = "5369"
            currentPlayerID = "1986"
            setID(currentGameID, currentPlayerID)
        }
         */

        // initialize game
        //viewModel.initGame()
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
                loadPlayersFromGameID()
            }
        } else loadPlayersFromGameID()
    }

    fun loadPlayersFromGameID() {
        playerDataRef.child(gameID).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            // Prepare Map which players will go into
            val players : MutableMap<String,MutableMap<String,String>> = mutableMapOf()
            // Loop through all players and add to local 'players' Map
            for(player in it.child("players").children) {
                players.put("${player.child("playerID").value}", mutableMapOf(
                    "nickname" to "${player.child("nickname").value}",
                    "color" to "${player.child("color").value}",
                    "choice" to "null",
                    "score" to "0"
                ))
            }
            println("---$players")
            savePlayersToDatabase(players!!)
            setPlayers()
            gameLoop()
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

    var player1:String? = null
    var player2:String? = null

    fun setPlayers() {

        player1 = playerID
        player2 = opponentID

        println("player1_id: $player1 -vs- player2_id: $player2")
        setVsText("$player1 -vs- $player2")
    }

    // End of setPlayers

    fun gameLoop() {
        // add eventlisteners to both players, specifically on attribute 'choice'
        val player1connection = database.getReference("Sten Sax Pase").child(gameID).child("players").child(playerID!!)
        val player2connection = database.getReference("Sten Sax Pase").child(gameID).child("players").child(opponentID!!)
        player1connection.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                val choice = dataSnapshot.getValue()
                // handle player1 choice changes
                println("player1 choice: $choice")
                getChoiceFromDatabase()
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("db_error", "Failed to read value.", error.toException())
            }
        })
        player2connection.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                val choice = dataSnapshot.getValue()
                // handle player2 choice changes
                println("player2 choice: $choice")
                getChoiceFromDatabase()
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("db_error", "Failed to read value.", error.toException())
            }
        })
    }

    fun setChoice(choice:String, player:String) {
        //playerMap.forEach { entry -> if(entry.value.containsValue("choice\"") ) }
        println("$player chose: $choice")
        if(player == playerID) setChoiceInDatabase(choice, playerID!!)
        else if(player == opponentID) setChoiceInDatabase(choice, opponentID!!)
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
                checkOutcome(player1choice, player2choice)
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
            setActionText("$playerID won with $player1choice beating $player2choice")
            // Fördröjning
            handler.postDelayed({
                setScoreInDatabase(playerID!!)
            }, 4000)
        }
        else if(outcome == opponentID) {
            setActionText("$opponentID won with $player2choice beating $player1choice")
            // Fördröjning
            handler.postDelayed({
                setScoreInDatabase(opponentID!!)
            }, 4000)
        } else if(outcome == "even"){
            setActionText("outcome of round: $outcome")
            // Fördröjning
            handler.postDelayed({
                resetGame()
            }, 4000)
        }
    }

    fun setScoreInDatabase(player:String) {
        var scoreRef = stenSaxPaseRef.child(gameID).child("players").child(player).child("score")
        scoreRef.get().addOnSuccessListener {
            var score = it.value.toString().toInt()
            println("-score: $score , for player: $player")
            score++
            println("-updated score: $score , for player: $player")
            stenSaxPaseRef.child(gameID).child("players").child(player).child("score").setValue(score)
            resetGame()
            checkForWin(player, score)
        }

    }

    fun resetGame() {
        stenSaxPaseRef.child(gameID).child("players").child(playerID!!).child("choice").setValue("null")
        stenSaxPaseRef.child(gameID).child("players").child(opponentID!!).child("choice").setValue("null")
        resetUI()
    }

    fun resetUI() {
        binding.sten.isClickable = true
        binding.sax.isClickable = true
        binding.pase.isClickable = true

        binding.sten.visibility = View.VISIBLE
        binding.sax.visibility = View.VISIBLE
        binding.pase.visibility = View.VISIBLE
    }

    fun checkForWin(player:String,score:Int) {
        if(score == 2) {
            println("player: $player wins")
            // apply logic for what should happen once a player wins
            stenSaxPaseRef.child(gameID).child("status").setValue(false)
            try {
                database.getReference().child("Board Data").child(currentGameID).child("randomVal").setValue(-1)
                view?.findNavController()?.navigate(R.id.action_stensaxpaseFragment_to_testBoardFragment)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        else println("no win yet for player: $player")
    }
}
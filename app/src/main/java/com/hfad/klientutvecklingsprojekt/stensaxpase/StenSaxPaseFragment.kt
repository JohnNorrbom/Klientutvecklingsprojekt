package com.hfad.klientutvecklingsprojekt.stensaxpase

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.databinding.FragmentStenSaxPaseBinding
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import kotlin.random.Random

class StenSaxPaseFragment : Fragment() {

    private var _binding: FragmentStenSaxPaseBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: StenSaxPaseViewModel
    private var meModel : MeModel? = null

    var currentGameID : String = ""
    var currentPlayerID : String = ""


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
            } ?: run {
                // Handle the case when meModel is null
                Log.e("LobbyFragment", "meModel is null")
            }
        }

        // initialize game
        //viewModel.initGame()
        initGame()

        // For testing when mini-game is launched from start screen button
        if(currentGameID == "") {
            currentGameID = "9182"
            currentPlayerID = "8144"
        }

        // add view code here
        val sten = binding.sten
        val sax = binding.sax
        val pase = binding.pase

        sten.setOnClickListener {
            sax.visibility = View.INVISIBLE
            pase.visibility = View.INVISIBLE
            sten.isClickable = false
            println("sten klick")
            //viewModel.setChoice("sten", currentPlayerID)
            setChoice("sten", currentPlayerID)
            setActionText("$currentPlayerID valde: Sten")
        }
        sax.setOnClickListener {
            sten.visibility = View.INVISIBLE
            pase.visibility = View.INVISIBLE
            sax.isClickable = false
            println("sax klick")
            //viewModel.setChoice("sax", currentPlayerID)
            setChoice("sax", currentPlayerID)
            setActionText("$currentPlayerID valde: Sax")
        }
        pase.setOnClickListener {
            sten.visibility = View.INVISIBLE
            sax.visibility = View.INVISIBLE
            pase.isClickable = false
            println("pase klick")
            //viewModel.setChoice("pase", currentPlayerID)
            setChoice("pase", currentPlayerID)
            setActionText("$currentPlayerID valde: Påse")
        }

        return view
    }

    fun setText() {
        //den här
        currentGameID = meModel?.gameID ?: ""
        currentPlayerID = meModel?.playerID ?: ""
        Log.d("StenSaxPaseFragment", "playerID: ${currentPlayerID} GameID: ${currentGameID}")
    }

    private fun setActionText(text: String) {
        binding.actionText.text = text
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Code starting from here was originally in view model
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val stenSaxPaseRef = database.getReference("Sten Sax Pase")
    private val playerDataRef = database.getReference("Player Data")

    private var stenSaxPaseModel : StenSaxPaseModel? = null

    var playerID: String = ""
    var gameID: String = ""

    fun setID(currentGameID:String, currentPlayerID:String) {
        gameID = currentGameID
        playerID = currentPlayerID
    }

    fun initGame() {
        // write code here in sequential order of which they should execute :-P
        if(gameID.isEmpty()) {
            gameID = "9182"
            playerID = "8144"
        }

        println("Currently in game: $gameID , using playerID: $playerID")

        loadPlayersFromGameID()

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
            setPlayers(players!!)
            gameLoop()
        }
    }

    fun savePlayersToDatabase(players : MutableMap<String,MutableMap<String,String>>) {
        stenSaxPaseModel = StenSaxPaseModel(gameID, false, players)
        stenSaxPaseRef.child(gameID).setValue(stenSaxPaseModel)
    }

    // Maybe remove this whole setPlayers thing and instead use solution where a player chooses who to compete with

    var player1:String? = null
    var player2:String? = null

    fun setPlayers(playerMap: MutableMap<String,MutableMap<String,String>>) {

        player1 = playerID

        if(playerMap!=null) {
            var randomNmbr: Int
            do {
                randomNmbr = Random.nextInt(playerMap!!.size)

                var i = 0
                for (player in playerMap!!) {
                    if(randomNmbr == i) player2 = "${player.key}"
                    i++
                }
            } while (player1 == player2)
            println("player1_id: $player1 -vs- player2_id: $player2")
        }
    }

    // End of setPlayers

    val gameStatus = database.getReference("Sten Sax Pase").child(gameID).child("status")

    fun gameLoop() {
        gameStatus.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                val status = dataSnapshot.getValue()
                println("--gameStatus: $status")
                if(status == false) gameStatus.setValue(true)
                // handle gameStatus changes
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("db_error", "Failed to read value.", error.toException())
            }
        })
        // add eventlisteners to both players, specifically on attribute 'choice'
        val player1connection = database.getReference("Sten Sax Pase").child(gameID).child("players").child(player1!!)
        val player2connection = database.getReference("Sten Sax Pase").child(gameID).child("players").child(player2!!)
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
        if(player == playerID) setChoiceInDatabase(choice)
    }

    fun setChoiceInDatabase(choice:String) {
        stenSaxPaseRef.child(gameID).child("players").child(playerID).child("choice").setValue(choice)
    }

    fun getChoiceFromDatabase() {
        val player1choiceRef = stenSaxPaseRef.child(gameID).child("players").child(player1!!).child("choice")
        val player2choiceRef = stenSaxPaseRef.child(gameID).child("players").child(player2!!).child("choice")
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
        var outcome = "none"
        println("$player1choice mot $player2choice")
        if(player1choice != "null" && player2choice != "null") {
            if(player1choice == player2choice) outcome = "even"
            else if(player1choice == "sten" && player2choice == "sax") outcome = player1!!
            else if(player1choice == "sax" && player2choice == "pase") outcome = player1!!
            else if(player1choice == "pase" && player2choice == "sten") outcome = player1!!
            else outcome = player2!!
        }
        println("-outcome: $outcome")
        if(outcome == player1) {
            setScoreInDatabase(player1!!)
            setActionText("$player1 won with $player1choice beating $player2choice")
        }
        else if(outcome == player2) {
            setScoreInDatabase(player2!!)
            setActionText("$player2 won with $player2choice beating $player1choice")
        }
        setActionText("outcome of round: $outcome")
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
        stenSaxPaseRef.child(gameID).child("players").child(player1!!).child("choice").setValue("null")
        stenSaxPaseRef.child(gameID).child("players").child(player2!!).child("choice").setValue("null")
        resetUI()
    }

    fun resetUI() {
        binding.sten.isClickable = true
        binding.sax.isClickable = true
        binding.pase.isClickable = true

        binding.sten.visibility = View.VISIBLE
        binding.sax.visibility = View.VISIBLE
        binding.pase.visibility = View.VISIBLE

        setActionText("Gör ett val")
    }

    fun checkForWin(player:String,score:Int) {
        if(score == 2) {
            println("player: $player wins")
            // apply logic for what should happen once a player wins
            stenSaxPaseRef.child(gameID).setValue(null)
        }
        else println("no win yet for player: $player")
    }
}
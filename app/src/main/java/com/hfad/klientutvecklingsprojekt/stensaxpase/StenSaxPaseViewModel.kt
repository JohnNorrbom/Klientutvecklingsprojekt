package com.hfad.klientutvecklingsprojekt.stensaxpase

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlin.random.Random

/**
 * @author: 21siha02 : simon.hamner@gmail.com
 *
 * denna klass används inte
 */

class StenSaxPaseViewModel() : ViewModel() {

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
           gameID = "8718"
           playerID = "1199"
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
        if(outcome == player1) setScoreInDatabase(player1!!)
        else if(outcome == player2) setScoreInDatabase(player2!!)
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
    }

    fun checkForWin(player:String,score:Int) {
        if(score == 2) {
            println("player: $player wins")
            // apply logic for what should happen once a player wins
            stenSaxPaseRef.child(gameID).setValue(null)
        }
        else println("no win yet for player: $player")
    }

    /*
    var playerMap : MutableMap<String, MutableMap<String,String>>? = null

    fun loadPlayersFromLobby() {
        // Load all players from existing lobby
        var playerData = database.getReference("Player Data").child(gameID)

        playerData.child(gameID).get().addOnSuccessListener {
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
            // save to 'Sten Sax Pase' portion of database
            initialSaveToDatabase(players)

            // save local Map of players
            playerMap = players

            // set players which will be participating in the mini-game
            setPlayers()

            // execute gameLoop
            gameLoop()

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    var player1:String? = null
    var player2:String? = null

    var gID = ""
    var pID = ""
    fun setIdTest(gId:String,pId:String) {
        gID = gId
        pID = pId

        player1 = pID
    }

    fun setPlayers() {
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

    fun initialSaveToDatabase(playersArr:MutableMap<String,MutableMap<String,String>>) {

        var players:MutableMap<String,MutableMap<String,String>> = mutableMapOf()
        //if(!playersArr.isEmpty()) players = playersArr
        println("playerArr: $playersArr")

        // just for testing
        if(players.isEmpty() || players == null) {
            players.put(
                "10", mutableMapOf(
                    "nickname" to "Bengt",
                    "color" to "black",
                    "choice" to "null",
                    "score" to "0"
                )
            )
            /*
            players.put(
                "2222", mutableMapOf(
                    "nickname" to "Sven",
                    "color" to "white",
                    "choice" to "null",
                    "score" to "0"
                )
            )

            players.put(
                "3333", mutableMapOf(
                    "nickname" to "Måns",
                    "color" to "green",
                    "choice" to "null",
                    "score" to "0"
                )
            )
             */
        }

        // Save to Database
        stenSaxPaseModel = StenSaxPaseModel(gameID, false, players)
        println("----$gameID----$stenSaxPaseModel")
        myRef.child(gameID).setValue(stenSaxPaseModel)
    }

    //add bussiness logic for sten sax pase mini-game
    var gameID = "10"

    private fun setGameID() {
        if(!gID.isEmpty()) gameID = gID
        else gameID = "10"
        println("-----:$gameID")
    }

    fun initGame() {
        // set game ID
        setGameID()

        // load players from lobby, should always return something
        loadPlayersFromLobby()
    }

    fun setChoiceInDatabase(choice:String) {
        myRef.child(gameID).child("players").child(pID).child("choice").setValue(choice)
    }

    fun setChoice(choice:String, player:String) {
        //playerMap.forEach { entry -> if(entry.value.containsValue("choice\"") ) }
        if(player == pID) setChoiceInDatabase(choice)
    }

    fun getChoiceFromDatabase():String {
        val player1choice = myRef.child(gameID).child("players").child(player1!!).child("choice").get().toString()
        val player2choice = myRef.child(gameID).child("players").child(player2!!).child("choice").get().toString()
        var outcome:String = "none"
        if(player1choice != null && player2choice != null) {
            if(player1choice == player2choice) outcome = "even"
            else if(player1choice == "sten" && player2choice == "sax") outcome = player1!!
            else if(player1choice == "sax" && player2choice == "pase") outcome = player1!!
            else if(player1choice == "pase" && player2choice == "sten") outcome = player1!!
            else outcome = player2!!
        }
        return outcome
    }

    fun checkOutcome(outcome:String) {
        if(outcome != "none") {
            if(outcome == player1) setScoreInDatabase("player1")
            else if(outcome == player2) setScoreInDatabase("player2")
        }
        resetGame()
    }

    fun resetGame() {
        myRef.child(gameID).child("players").child(player1!!).child("choice").setValue(null)
        myRef.child(gameID).child("players").child(player2!!).child("choice").setValue(null)
    }

    fun setScoreInDatabase(player:String) {
        var score = myRef.child(gameID).child("players").child(player).child("score").get().toString().toInt()
        myRef.child(gameID).child("players").child(player).child("score").setValue(score)
    }

    val gameStatus = database.getReference("Sten Sax Pase").child(gameID).child("status")

    fun gameLoop() {
        gameStatus.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                val status = dataSnapshot.getValue()
                println("-------gameStatus: $status")
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
                checkOutcome(getChoiceFromDatabase())
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
                checkOutcome(getChoiceFromDatabase())
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("db_error", "Failed to read value.", error.toException())
            }
        })
    }
     */
}
package com.hfad.klientutvecklingsprojekt.stensaxpase

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.hfad.klientutvecklingsprojekt.gamestart.GameModel
import kotlin.random.Random

class StenSaxPaseViewModel() : ViewModel() {

    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Sten Sax Pase")

    private var stenSaxPaseModel : StenSaxPaseModel? = null

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

        val players = playersArr
        println("playerArr: $playersArr")

        // just for testing
        if(players.isEmpty() || players == null) {
            players.put(
                "$pID", mutableMapOf(
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

        myRef.child(gameID).setValue(stenSaxPaseModel)
    }

    //add bussiness logic for sten sax pase mini-game
    var gameID = "-1"

    private fun setGameID() {
        //if(gameModel?.gameID != null) gameID = gameModel.gameID
        //else
            gameID = gID
        //println("------:${gameModel?.gameID}-----:$gameID")
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
}
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

    val gameModel : GameModel?=null


    /*
    stenSaxPaseModel?.apply {
        // lägg till de variabler som ska in i firebase
    }
     */

    var playerMap : MutableMap<String, MutableMap<String,String>>? = null

    fun loadFromDatabase() {

        var spaceParty = database.getReference("Sten Sax Pase")

        spaceParty.child(gameID).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            val players : MutableMap<String,MutableMap<String,String>> = mutableMapOf()
            for(player in it.child("players").children) {
                //println(player.child("nickname").value)
                players.put("${player.key}", mutableMapOf(
                    "nickname" to "${player.child("nickname").value}",
                    "color" to "${player.child("color").value}",
                    "choice" to "null",
                    "score" to "0"
                ))
            }
            println(players)

            // put game logic here
            // save to database
            initialSaveToDatabase(players)

            playerMap = players

            // set players
            setPlayers()
            // execute gameLoop
            gameLoop()

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    var player1:String? = null
    var player2:String? = null

    fun setPlayers() {
        if(playerMap!=null) {
            var randomNmbr = Random.nextInt(playerMap!!.size)
            var randomNmbr2 = Random.nextInt(playerMap!!.size)
            while(randomNmbr == randomNmbr2) {
                randomNmbr = Random.nextInt(playerMap!!.size)
            }
            var i = 0
            for (player in playerMap!!) {
                //println("_____________${player.key}")
                if(randomNmbr == i) player1 = "${player.key}"
                if(randomNmbr2 == i) player2 = "${player.key}"
                i++
            }

            println("player1: $player1 ---- player2: $player2")
        }
    }

    fun initialSaveToDatabase(playersArr:MutableMap<String,MutableMap<String,String>>) {

        val players = playersArr

        // just for testing
        if(players.isEmpty() || players == null) {
            players.put(
                "1111", mutableMapOf(
                    "nickname" to "Bengt",
                    "color" to "black",
                    "choice" to "null",
                    "score" to "0"
                )
            )

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
        }

        // Save to Database
        stenSaxPaseModel = StenSaxPaseModel(gameID, false, players)

        myRef.child(gameID).setValue(stenSaxPaseModel)
    }

    //add bussiness logic for sten sax pase mini-game
    var gameID = "-1"

    private fun setGameID() {
        if(gameModel?.gameID != null) gameID = gameModel.gameID
        println("---------:${gameModel?.gameID}-----:$gameID")
    }

    fun initGame() {
        // set game ID
        setGameID()

        // load players from lobby, should always return something
        loadFromDatabase()
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
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("db_error", "Failed to read value.", error.toException())
            }
        })
    }
}
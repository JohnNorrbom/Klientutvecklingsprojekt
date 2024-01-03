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

    fun loadFromDatabase() {

        var spaceParty = database.getReference("Sten Sax Pase").child(gameID).child("players")
        var str:String? = ""

        spaceParty.get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

        println("-------:$str")
    }

    fun saveToDatabase() {

        //loadFromDatabase()

        val players : MutableMap<String,MutableMap<String,String>> = mutableMapOf()

        /*
        // for each player in lobby, get this from loadFromDatabase(), or possibly safeargs

        players.put("${player.playerID}", mutableMapOf(
            "nickname" to "${player.nickname}",
            "color" to "${player.color}",
            "choice" to "null",
            "score" to "0"
        ))
         */

        players.put("1111", mutableMapOf(
            "nickname" to "Bengt",
            "color" to "black",
            "choice" to "null",
            "score" to "0"
        ))

        players.put("2222", mutableMapOf(
            "nickname" to "Sven",
            "color" to "white",
            "choice" to "null",
            "score" to "0"
        ))

        players.put("3333", mutableMapOf(
            "nickname" to "Måns",
            "color" to "green",
            "choice" to "null",
            "score" to "0"
        ))

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

        // load two players from lobby
        loadFromDatabase()

        // save to database
        saveToDatabase()

        // execute gameLoop
        gameLoop()
    }

    val gameStatus = database.getReference("Sten Sax Pase").child(gameID).child("status")

        fun gameLoop() {
            gameStatus.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot){
                    val status = dataSnapshot.getValue()
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("db_error", "Failed to read value.", error.toException())
                }
            })
    }

}
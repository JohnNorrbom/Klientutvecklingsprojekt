package com.hfad.klientutvecklingsprojekt.gamestart

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

/**
 *
 * GameData:
 *
 * Ett object som används för att:
 * Spara till modelen och databasen
 * Hämta från databasen och spara till modelen
 *
 * @author William
 *
 */

object GameData {
    private var _gameModel : MutableLiveData<GameModel?> = MutableLiveData()
    var gameModel : MutableLiveData<GameModel?> = _gameModel
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Game Data")
    //används för att spara till modellen och databasen
    fun saveGameModel(model: GameModel){
        _gameModel.postValue(model)
        myRef.child(model.gameID?:"").setValue(model)
    }

    //används för att hämta från databasen och spara till modelen
    fun fetchGameModel(){
        gameModel.value?.apply {
            myRef.addValueEventListener(gameListener)
        }
    }

    private val gameListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val id = gameModel.value?.gameID
            val model = snapshot.child(id ?: "").getValue(GameModel::class.java)
            _gameModel.postValue(model)

        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }
}
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


object GameData {
    private var _gameModel : MutableLiveData<GameModel?> = MutableLiveData()
    var gameModel : MutableLiveData<GameModel?> = _gameModel
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Game Data")

    fun saveGameModel(model: GameModel){
        Log.d("GameModel","${model}")
        _gameModel.postValue(model)
        myRef.child(model.gameID?:"").setValue(model)

    }

    fun fetchGameModel(){
        gameModel.value?.apply {
            myRef.addValueEventListener(gameListener)
        }
    }

    val gameListener = object : ValueEventListener {
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
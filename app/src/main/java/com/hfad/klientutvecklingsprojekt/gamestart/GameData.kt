package com.hfad.klientutvecklingsprojekt.gamestart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.database


object GameData {
    private var _gameModel : MutableLiveData<GameModel> = MutableLiveData()
    var gameModel : LiveData<GameModel> = _gameModel
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Space Party")

    fun saveGameModel(model: GameModel){
        _gameModel.postValue(model)
        if(model.gameID!= "-1"){
            model.gameID?.let { myRef.child(it).setValue(model) }
        }
    }
}
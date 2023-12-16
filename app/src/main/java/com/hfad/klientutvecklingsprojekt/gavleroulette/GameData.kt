package com.hfad.wifeposijo_boardgame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.database

object GameData {
    private var _gameModel : MutableLiveData<GameModel> = MutableLiveData()
    var gameModel : LiveData<GameModel> = _gameModel
    var myId = ""

    fun saveGameModel(model: GameModel){
        _gameModel.postValue(model)

        val database = Firebase.database


    }
}
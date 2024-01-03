package com.hfad.klientutvecklingsprojekt.playerinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.gamestart.GameModel

object PlayerData {
        private var _playerModel : MutableLiveData<PlayerModel> = MutableLiveData()
        var playerModel : LiveData<PlayerModel> = _playerModel
        private val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
        private val myRef = database.getReference("Game Lobby").child("Players")
        fun savePlayerModel(model: PlayerModel){
            _playerModel.postValue(model)
            myRef.child(model.playerID ?: "").setValue(model)
        }
}
package com.hfad.klientutvecklingsprojekt.playerinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.gamestart.GameModel
import com.hfad.klientutvecklingsprojekt.lobby.LobbyData
import com.hfad.klientutvecklingsprojekt.lobby.LobbyModel

object PlayerData {
        private var _playerModel : MutableLiveData<PlayerModel> = MutableLiveData()
        var playerModel : LiveData<PlayerModel> = _playerModel
        var gameModel : GameModel? = null
        private val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
        private val myRef = database.getReference("Player Data")
        fun savePlayerModel(model: PlayerModel){
            _playerModel.postValue(model)
                myRef.child(gameModel?.gameID.toString()).child("players").setValue(model)
        }
}
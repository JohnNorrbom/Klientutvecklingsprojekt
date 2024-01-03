package com.hfad.klientutvecklingsprojekt.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData

object LobbyData {
    private var _lobbyModel : MutableLiveData<LobbyModel> = MutableLiveData()
    var lobbyModel : LiveData<LobbyModel> = _lobbyModel
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Space Party").child("Players")

    fun saveLobbyModel(model: LobbyModel){
        _lobbyModel.postValue(model)
        if(model.gameID!= "-1"){
            PlayerData.myRef.child(model.playerID).setValue(model)
        }
    }
}
package com.hfad.klientutvecklingsprojekt.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object LobbyData {
    private var _lobbyModel : MutableLiveData<LobbyModel> = MutableLiveData()
    var lobbyModel : LiveData<LobbyModel> = _lobbyModel

    fun saveLobbyModel(model: LobbyModel){
        _lobbyModel.postValue(model)
    }
}
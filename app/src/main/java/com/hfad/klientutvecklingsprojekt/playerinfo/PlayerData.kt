package com.hfad.klientutvecklingsprojekt.playerinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object PlayerData {
        private var _playerModel : MutableLiveData<PlayerModel> = MutableLiveData()
        var playerModel : LiveData<PlayerModel> = _playerModel

        fun savePlayerModel(model: PlayerModel){
            _playerModel.postValue(model)
        }
}
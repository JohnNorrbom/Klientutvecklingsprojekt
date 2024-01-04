package com.hfad.klientutvecklingsprojekt.player

import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.lobby.LobbyModel

object MeData {
    private var _meModel : MutableLiveData<MeModel?> = MutableLiveData()
    var meModel : MutableLiveData<MeModel?> = _meModel
    //  setMeModel
    fun saveMeModel(model: MeModel){
        _meModel.postValue(model)
    }
    //  getMemodel
    fun fetchMeModel() : MutableLiveData<MeModel?>{
        return _meModel
    }

}
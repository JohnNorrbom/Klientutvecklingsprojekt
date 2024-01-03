package com.hfad.klientutvecklingsprojekt.gavleroulette

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object RouletteData {
    private var _rouletteModel : MutableLiveData<RouletteModel> = MutableLiveData()
    var rouletteModel : LiveData<RouletteModel> = _rouletteModel
    var myId = ""

    fun saveGameModel(model: RouletteModel){
        _rouletteModel.postValue(model)
    }
}
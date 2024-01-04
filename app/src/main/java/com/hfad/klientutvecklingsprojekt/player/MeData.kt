package com.hfad.klientutvecklingsprojekt.player

import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.lobby.LobbyModel

object MeData {
    private var _meModel : MutableLiveData<MeModel?> = MutableLiveData()
    var meModel : MutableLiveData<MeModel?> = _meModel
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Lobby Data")
    val id = meModel?.value?.gameID

    fun saveMeModel(model: MeModel){
        _meModel.postValue(model)
    }

}
package com.hfad.klientutvecklingsprojekt.playerinfo

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.firebase.FirebaseFragment

object PlayerData {
        private var _playerModel : MutableLiveData<PlayerModel> = MutableLiveData()
        var playerModel : LiveData<PlayerModel> = _playerModel
        val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("Space Party")
        fun savePlayerModel(model: PlayerModel){
            _playerModel.postValue(model)
            if(model.gameID!= "-1"){
                model.gameID?.let { myRef.child(it).setValue(model) }
            }
        }

    fun fetchPlayerModel(){
        playerModel.value?.apply {
            if (gameID!= "-1"){

            }
        }
    }
}
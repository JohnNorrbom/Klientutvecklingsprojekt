package com.hfad.klientutvecklingsprojekt.lobby

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.gamestart.GameData
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerData
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel

object LobbyData {
    private var _lobbyModel : MutableLiveData<LobbyModel?> = MutableLiveData()
    var lobbyModel : MutableLiveData<LobbyModel?> = _lobbyModel
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Lobby Data")
    val id = lobbyModel?.value?.gameID

    fun saveLobbyModel(model: LobbyModel,id : String){
        _lobbyModel.postValue(model)
        myRef.child(id).setValue(model)
    }

    fun fetchLobbyModel(){
        lobbyModel.value?.apply {
            myRef.addValueEventListener(lobbyListener)
        }
    }

    val lobbyListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val id =  lobbyModel.value?.gameID
            val model = snapshot.child(id ?: "").getValue(LobbyModel::class.java)
            _lobbyModel.postValue(model)
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }
}
package com.hfad.klientutvecklingsprojekt.playerinfo

import android.util.Log
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
    var gameID = gameModel?.gameID ?:""
        private val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
        private val myRef = database.getReference("Game Lobby")
        fun savePlayerModel(model: PlayerModel, gameID : String){
            _playerModel.postValue(model)
            // För att spara under ett specifikt ID
            val playerID = model.playerID ?: ""
            Log.d("p ID","${playerID}")
            Log.d("g ID","${gameID}")


            // Skapa en referens till den specifika spelarens nod
            val playerRef = myRef.child(gameID).child("players").child(playerID)

            // Uppdatera data i den specifika spelarens nod
            playerRef.setValue(model)
        }
}
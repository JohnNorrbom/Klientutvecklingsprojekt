package com.hfad.klientutvecklingsprojekt.playerinfo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.gamestart.GameData
import com.hfad.klientutvecklingsprojekt.gamestart.GameModel
import com.hfad.klientutvecklingsprojekt.lobby.LobbyData
import com.hfad.klientutvecklingsprojekt.lobby.LobbyModel
/**
 *
 * PlayerData:
 *
 * Ett object som används för att:
 * Spara till modelen och databasen
 * Hämta från databasen och spara till modelen
 *
 * @author William
 *
 */
object PlayerData {
    private var _playerModel: MutableLiveData<PlayerModel?> = MutableLiveData()
    var playerModel: MutableLiveData<PlayerModel?> = _playerModel
    var gameModel: GameModel? = null
    var gameID = gameModel?.gameID ?: ""
    private val database =
        Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Player Data")
    fun savePlayerModel(model: PlayerModel, gameID: String) {
        _playerModel.postValue(model)
        // För att spara under ett specifikt ID
        val playerID = model.playerID ?: ""
        Log.d("p ID", "${playerID}")
        Log.d("g ID", "${gameID}")
        // Skapa en referens till den specifika spelarens nod
        val playerRef = myRef.child(gameID).child("players").child(playerID)
        playerRef.setValue(model)
    }

    fun fetchPlayerModel() {
        GameData.gameModel.value?.apply {
            GameData.myRef.addValueEventListener(playerListener)
        }
    }

    val playerListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val id = GameData.gameModel.value?.gameID
            val model = snapshot.child(id ?: "").getValue(PlayerModel::class.java)
            _playerModel.postValue(model)

        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }
}
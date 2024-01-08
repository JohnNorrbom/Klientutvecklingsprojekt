package com.hfad.klientutvecklingsprojekt.soccer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.gamestart.GameData
import com.hfad.klientutvecklingsprojekt.gamestart.GameModel
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel


object SoccerData {

    private var _soccerModel : MutableLiveData<SoccerModel?> = MutableLiveData()
    var soccerModel : MutableLiveData<SoccerModel?> = _soccerModel
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Soccer")
    /**
     * Saves the SoccerModel to the Firebase Realtime Database.
     * @param model: The SoccerModel to be saved
     */
    fun saveSoccerModel(model: SoccerModel){
        _soccerModel.postValue(model)
        myRef.child(model.gameID?:"").setValue(model)
    }
    /**
     * Fetches the SoccerModel from the Firebase Realtime Database.
     * Listens for changes and updates the LiveData soccerModel accordingly.
     */
    fun fetchSoccerModel(){
        soccerModel.value?.apply {
            myRef.addValueEventListener(soccerListener)
        }
    }

    private val soccerListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val id = soccerModel.value?.gameID
            val model = snapshot.child(id ?: "").getValue(SoccerModel::class.java)
            _soccerModel.postValue(model)

        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    }
}
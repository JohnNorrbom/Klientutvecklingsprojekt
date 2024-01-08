package com.hfad.klientutvecklingsprojekt.gavleroulette

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.inject.Deferred
import com.hfad.klientutvecklingsprojekt.gamestart.GameData
import com.hfad.klientutvecklingsprojekt.lobby.LobbyModel
/**
 *
 * RouletteData:
 *
 * Ett object som används för att:
 * Spara till modelen och databasen
 * Hämta från databasen och spara till modelen
 * Tabort lyssnare
 *
 * @author William
 *
 */
object RouletteData {
    private var _rouletteModel : MutableLiveData<RouletteModel?> = MutableLiveData()
    var rouletteModel : MutableLiveData<RouletteModel?> = _rouletteModel
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Roulette")
    var gameID : String =""


    //används för att spara till modellen och databasen
    fun saveGameModel(model: RouletteModel,id : String){
        _rouletteModel.postValue(model)
        myRef.child(id).setValue(model)
    }

    //används för att hämta från databasen och spara till modelen
    fun fetchGameModel(){
        rouletteModel.value?.apply {
            myRef.child(gameId?:"").addValueEventListener(rouletteListener)
        }
    }

    val rouletteListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                val model = snapshot.getValue(RouletteModel::class.java)
                _rouletteModel.postValue(model)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }
    //Tarbort lyssnare
    fun removeListner(){
        rouletteModel.value?.apply {
             myRef.child(gameId?:"").removeEventListener(rouletteListener)
        }
    }
}
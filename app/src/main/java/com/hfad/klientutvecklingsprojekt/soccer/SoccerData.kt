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
    fun saveSoccerModel(model: SoccerModel){
        _soccerModel.postValue(model)
        myRef.child(model.gameID?:"").setValue(model)
    }

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
    /*
    fun setP1Score(score: Int, gameId: String){
        myRef.child(gameId).child("p1Score").setValue(score)
    }
    fun setP2Score(score: Int, gameId: String){
        myRef.child(gameId).child("p2Score").setValue(score)
    }
    fun getP1Score(gameId: String) : Int {
        return myRef.child(gameId).child("p1Score").get() as Int
    }
    fun getP2Score(gameId: String) : Int {
        return myRef.child(gameId).child("p2Score").get() as Int
    }
    fun setP1Color(color: String, gameId: String){
        myRef.child(gameId).child("p1Color").setValue(color)
    }
    fun setP2Color(color: String, gameId: String){
        myRef.child(gameId).child("p2Color").setValue(color)
    }
    fun getP1Color(gameId: String) : String{
        return myRef.child(gameId).child("p1Color").get() as String
    }
    fun getP2Color(gameId: String) : String{
        return myRef.child(gameId).child("p2Color").get() as String
    }

     */

}
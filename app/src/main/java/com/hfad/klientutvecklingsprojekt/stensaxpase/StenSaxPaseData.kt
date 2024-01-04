package com.hfad.klientutvecklingsprojekt.stensaxpase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.gamestart.GameModel

object StenSaxPaseData {
    private var _sspModel : MutableLiveData<StenSaxPaseModel?> = MutableLiveData()
    var sspModel : MutableLiveData<StenSaxPaseModel?> = _sspModel
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Game Data")

    fun saveGameModel(model: StenSaxPaseModel){
        Log.d("GameModel","${model}")
        _sspModel.postValue(model)
        myRef.child(model.gameID?:"").setValue(model)

    }

    fun fetchGameModel(){
        sspModel.value?.apply {
            myRef.addValueEventListener(gameListener)
        }
    }

    val gameListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val id = sspModel.value?.gameID
            val model = snapshot.child(id ?: "").getValue(StenSaxPaseModel::class.java)
            _sspModel.postValue(model)

        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }
}
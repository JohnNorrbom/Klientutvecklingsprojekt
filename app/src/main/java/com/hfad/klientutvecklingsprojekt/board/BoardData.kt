package com.hfad.klientutvecklingsprojekt.board

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

/**
 *
 * @author John
 *
 * Objekt som hämtar data från databasen och sparar daata
 *
 */
object BoardData {
    private var _boardModel : MutableLiveData<BoardModel?> = MutableLiveData()
    var boardModel : MutableLiveData<BoardModel?> = _boardModel
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Board Data")
    //  Saves BoardModel locally and online
    fun saveBoardModel(model: BoardModel){
        Log.d("BoardModel","${model}")
        _boardModel.postValue(model)                            //  local
        myRef.child(model.gameID?:"").setValue(model)  //  database
    }
    //  Fetch BoardModel from database
    fun fetchBoardModel(){
        boardModel.value?.apply {
            myRef.addValueEventListener(boardListener)
        }
    }
    //
    val boardListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            //  ID for right boardmodel
            val id = boardModel.value?.gameID
            //  Saves boardmodel from database
            val model = snapshot.child(id ?: "").getValue(BoardModel::class.java)
            //  Writes over local board model
            _boardModel.postValue(model)    //  Saves locally
        }
        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    }
}
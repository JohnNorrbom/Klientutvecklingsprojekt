package com.hfad.klientutvecklingsprojekt.soccer

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class soccerModel(
    var gameID : String = "",
    var status : Progress? = null,
    var p1Score: Int,
    var p2Score: Int,
    var p1Choice : String,
    var p2Choice : String
)
enum class Progress{
    INPROGRESS,
    FINISHED
}

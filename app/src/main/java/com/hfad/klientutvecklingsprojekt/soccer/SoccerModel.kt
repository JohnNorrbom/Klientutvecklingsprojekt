package com.hfad.klientutvecklingsprojekt.soccer

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SoccerModel(
    var gameID : String = "0",
    var p1Score: Int,
    var p2Score: Int,
    var p1Choice : String,
    var p2Choice : String,
    var p1Color : String,
    var p2Color : String,
    var bothPlayerReady : Boolean
)
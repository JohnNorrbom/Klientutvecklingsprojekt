package com.hfad.klientutvecklingsprojekt.soccer

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SoccerModel(
    var gameID : String? = null,
    var p1Score: Int? = null,
    var p2Score: Int? = null,
    var p1Choice : String? = null,
    var p2Choice : String? = null,
    var p1Color : String? = null,
    var p2Color : String? = null,
    var bothPlayerReady : Boolean? = null,
    var p1Id: String? = null,
    var p2Id: String? = null
)
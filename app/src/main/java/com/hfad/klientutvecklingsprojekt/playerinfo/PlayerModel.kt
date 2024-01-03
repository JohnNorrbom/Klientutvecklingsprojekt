package com.hfad.klientutvecklingsprojekt.playerinfo

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class PlayerModel(
    var gameID : String? = null,
    var playerID : String? = null,
    var nickname : String? = null,
    var color : String? = null,
)
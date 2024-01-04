package com.hfad.klientutvecklingsprojekt.playerinfo

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class PlayerModel(
    var playerID : String? = null,
    var nickname : String? = null,
    var color : String? = null,
    var score : String? = null,
)
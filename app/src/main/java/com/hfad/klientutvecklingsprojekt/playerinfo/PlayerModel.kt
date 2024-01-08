package com.hfad.klientutvecklingsprojekt.playerinfo

import com.google.firebase.database.IgnoreExtraProperties

/**
 *
 * PlayerModel:
 *
 * Lokal model för spelare modelen
 * Alltså all information som behövs för att kunnna skapa spelare
 *
 * @author William
 *
 */

@IgnoreExtraProperties
data class PlayerModel(
    var playerID : String? = null,
    var nickname : String? = null,
    var color : String? = null,
    var score : Int? = null,
    var position : Int? = null,
)
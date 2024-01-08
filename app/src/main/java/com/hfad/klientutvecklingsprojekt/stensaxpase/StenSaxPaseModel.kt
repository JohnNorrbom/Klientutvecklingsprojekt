package com.hfad.klientutvecklingsprojekt.stensaxpase

import com.google.firebase.database.IgnoreExtraProperties

/**
 * @author: 21siha02 : simon.hamner@gmail.com
 *
 * denna klass används som mall för att spara i databsen
 */

@IgnoreExtraProperties
data class StenSaxPaseModel
    (
    val gameID : String? = null,
    var status : Boolean? = null,
    var players: MutableMap<String, MutableMap<String,String>>? = null,
    var playerID : String? = null,
    var opponentID: String? = null
    )
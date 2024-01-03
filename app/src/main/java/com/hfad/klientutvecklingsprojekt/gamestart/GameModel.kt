package com.hfad.klientutvecklingsprojekt.gamestart

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class GameModel(val gameID : String? = null, var status : Progress? = null, var takenPosition: MutableMap<String, CharacterStatus>? = null)
enum class Progress{
    INPROGRESS,
    FINISHED
}
enum class CharacterStatus{
    TAKEN,
    FREE
}

package com.hfad.klientutvecklingsprojekt.playerinfo

import com.google.firebase.database.IgnoreExtraProperties
import com.hfad.klientutvecklingsprojekt.lobby.LobbyModel

//  Det här är inte playermodel, borde vara gamemodel/lobbymodel
@IgnoreExtraProperties
data class PlayerModel(
    val gameID: String? = null,
    var status: Progress? = null,
    var takenPosition: MutableMap<String, CharacterStatus>? = null,
    var players: ArrayList<LobbyModel>? = null
)

enum class Progress {
    INPROGRESS,
    FINISHED
}

enum class CharacterStatus {
    TAKEN,
    FREE
}
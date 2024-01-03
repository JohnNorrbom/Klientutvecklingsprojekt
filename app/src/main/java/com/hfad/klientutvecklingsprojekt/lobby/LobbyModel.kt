package com.hfad.klientutvecklingsprojekt.lobby

import com.google.firebase.database.IgnoreExtraProperties
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel

@IgnoreExtraProperties
data class LobbyModel(val gameID : String?= null, var players : PlayerModel?=null)
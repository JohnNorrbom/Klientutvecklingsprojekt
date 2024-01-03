package com.hfad.klientutvecklingsprojekt.lobby

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class LobbyModel(val gameID : String?= null, var lobby : MutableList<String>? = null)
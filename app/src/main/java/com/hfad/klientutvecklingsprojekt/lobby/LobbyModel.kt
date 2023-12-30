package com.hfad.klientutvecklingsprojekt.lobby

data class LobbyModel(
    var gameID : String? = null,
    var nickname : String = "",
    var color : String = "",
    var participants : MutableList<Pair<String,String>> = mutableListOf()
)

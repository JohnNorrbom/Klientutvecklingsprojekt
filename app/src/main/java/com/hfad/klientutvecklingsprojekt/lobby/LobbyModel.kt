package com.hfad.klientutvecklingsprojekt.lobby

data class LobbyModel(
    var gameID : String = "",
    var playerID : String = "",
    var nickname : String = "",
    var color : String = "",
    var participants : MutableList<Pair<String,String>> = mutableListOf()
)

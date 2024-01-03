package com.hfad.klientutvecklingsprojekt.lobby

data class LobbyModel(
    var gameID : String = "",
    var playerID : String = "",
    var nickname : String = "",
    var color : String = "",
    //  Ska nog ta bort denna kodrad, vet inte vad den uppfyller för funktion
    var participants : MutableList<Pair<String,String>> = mutableListOf()
)

package com.hfad.klientutvecklingsprojekt.gavleroulette

import kotlin.random.Random

data class RouletteModel (
    val temp: MutableList<Pair<String, PlayerStatus>> = mutableListOf(
        Pair("1", PlayerStatus.ALIVE),
        Pair("2", PlayerStatus.ALIVE)
    ),
    var gameId : String = "-1",
    var playerStatus : PlayerStatus = PlayerStatus.ALIVE,
    var participants :  MutableList<Pair<String,String>> = mutableListOf(Pair("Test","test")),
    var offlineParticipants :  MutableList<Pair<String,PlayerStatus>> = temp,
    var winner : String ="",
    var gameStatus : GameStatus = GameStatus.CREATED,
    var currentPlayer : String = "",
    var attempts: Int = 0,
    var laps : Int = 0,
    val nbrOfPlayers : Int = 0,
    var aliveCount : Int = 0,
    val luckyNumber : MutableList<String> = mutableListOf((Random.nextInt(6)+1).toString(),"","","",""),
    var currentBullet : Int = 0
    )

enum class GameStatus{
    CREATED,
    JOINED,
    INPROGRESS,
    FINISHED
}

enum class PlayerStatus{
    ALIVE,
    DEAD
}
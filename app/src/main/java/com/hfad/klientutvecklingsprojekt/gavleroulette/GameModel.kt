package com.hfad.klientutvecklingsprojekt.gavleroulette

import kotlin.random.Random

data class GameModel (
    var gameId : String = "-1",
    var playerStatus : PlayerStatus = PlayerStatus.ALIVE,
    var playerOne: Pair<String, PlayerStatus> = Pair("Player One",PlayerStatus.ALIVE),
    var playerTwo: Pair<String, PlayerStatus> = Pair("Player Two",PlayerStatus.ALIVE),
    var onlineParticipants :  MutableList<Pair<String,PlayerStatus>>? = null,
    var offlineParticipants :  MutableList<Pair<String,PlayerStatus>> = mutableListOf(playerOne,playerTwo),
    var winner : String ="",
    var gameStatus : GameStatus = GameStatus.CREATED,
    var currentPlayer : String = "",
    var attempts: Int = 0,
    var laps : Int = 0,
    var aliveCount : Int = offlineParticipants.size,
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
package com.hfad.klientutvecklingsprojekt.gavleroulette

import com.google.firebase.database.IgnoreExtraProperties
import kotlin.random.Random
@IgnoreExtraProperties
data class RouletteModel (
    var gameId : String? = null,
    var players :  MutableMap<String,PlayerStatus> ? = null,
    var winner : String ? = null,
    var gameStatus : GameStatus ? = null,
    var currentPlayer : String ? = null,
    var attempts: Int ? = null,
    var laps : Int ? = null,
    val nbrOfPlayers : Int ? = null,
    var aliveCount : Int ? = null,
    val luckyNumber : MutableList<String>? = null,
        //mutableListOf((Random.nextInt(6)+1).toString(),"","","",""),
    var currentBullet : Int ? = null
    )

enum class GameStatus{
    INPROGRESS,
    FINISHED
}

enum class PlayerStatus{
    ALIVE,
    DEAD
}
package com.hfad.klientutvecklingsprojekt.gavleroulette

import com.google.firebase.database.IgnoreExtraProperties
/**
 *
 * RouletteModel:
 *
 * Lokal model för roulette spelet
 * Alltså all information som behövs för att kunnna spela
 * @author William
 *
 */
@IgnoreExtraProperties
data class RouletteModel (
    var gameId : String? = null,
    var players :  MutableMap<String,PlayerStatus> ? = null,
    var winner : String ? = null,
    var gameStatus : GameStatus ? = null,
    var currentPlayer : String ? = null,
    var attempts: Int ? = null,
    var laps : Int ? = null,
    var score : MutableMap<String,Int> ?= null,
    val nbrOfPlayers : Int ? = null,
    var aliveCount : Int ? = null,
    val luckyNumber : Int? = null,
    var currentBullet : Int ? = null,
    )

enum class GameStatus{
    INPROGRESS,
    FINISHED
}

enum class PlayerStatus{
    ALIVE,
    DEAD
}
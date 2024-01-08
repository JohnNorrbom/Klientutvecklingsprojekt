package com.hfad.klientutvecklingsprojekt.board

/**
 * @author John
 *
 * Tabell med:
 * gameID som håller koll så alla spelare är i samma lobby/game
 * currentPlayerID som håller koll på vems tur det är
 * playerCount som håller koll på hur många spelare det är med i lobbyn/gamet
 * randomVal som håller koll på vilket minigame som ska startas
 *
 */
data class BoardModel (val gameID: String?, val currentPlayerID: String?, val playerCount: Int?, val randomVal: Int?)

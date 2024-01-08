package com.hfad.klientutvecklingsprojekt.soccer

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Data class representing the SoccerModel entity to manage game-related data in Firebase.
 * @property gameID Unique identifier for the game.
 * @property p1Score Score of player 1.
 * @property p2Score Score of player 2.
 * @property p1Choice Choice made by player 1 ('left', 'right', 'mid').
 * @property p2Choice Choice made by player 2 ('left', 'right', 'mid').
 * @property p1Color Color assigned to player 1.
 * @property p2Color Color assigned to player 2.
 * @property bothPlayerReady Indicates if both players are ready to start the game.
 * @property p1Id Unique identifier for player 1.
 * @property p2Id Unique identifier for player 2.
 */
@IgnoreExtraProperties
data class SoccerModel(
    var gameID : String? = null,
    var p1Score: Int? = null,
    var p2Score: Int? = null,
    var p1Choice : String? = null,
    var p2Choice : String? = null,
    var p1Color : String? = null,
    var p2Color : String? = null,
    var bothPlayerReady : Boolean? = null,
    var p1Id: String? = null,
    var p2Id: String? = null
)
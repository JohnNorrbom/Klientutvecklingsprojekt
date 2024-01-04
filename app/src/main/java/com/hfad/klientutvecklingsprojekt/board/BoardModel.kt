package com.hfad.klientutvecklingsprojekt.board

import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel

data class BoardModel (val gameID: String?, val currentPlayer: PlayerModel?=null)

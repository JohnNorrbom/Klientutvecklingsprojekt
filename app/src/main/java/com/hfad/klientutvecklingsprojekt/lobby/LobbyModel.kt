package com.hfad.klientutvecklingsprojekt.lobby

import com.google.firebase.database.IgnoreExtraProperties

/**
 *
 * GameModel:
 *
 * Lokal model för skapandet av lobbyn
 * Alltså all information som behövs för att kunnna skapa lobbyn
 *
 * @author William
 *
 */

@IgnoreExtraProperties
data class LobbyModel(val gameID : String?= null, var btnPressed : Boolean?= null)
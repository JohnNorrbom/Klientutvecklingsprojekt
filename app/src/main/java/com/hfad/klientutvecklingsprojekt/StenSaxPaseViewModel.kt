package com.hfad.klientutvecklingsprojekt

import androidx.lifecycle.ViewModel
import kotlin.random.Random

class StenSaxPaseViewModel : ViewModel() {

    //add bussiness logic for sten sax pase mini-game
    var players = ArrayList<Player>()
    var lobbies = ArrayList<Lobby>()
    init {
        setPlayerCount()
    }
    fun setPlayerCount() {
        val randomNmbr = Random.nextInt(2, 5)
        var i = 2
        do {
            players.add(Player().getThis())
            i++
        } while(i < randomNmbr)
    }

    private var playerCount: Int = players.size

    //gameStatus should remain true if any player is still "ingame"
    var gameStatus: Boolean = true

    //do game loop
    private fun gameLoop() {

        do {
            //check if player count is even or odd, based on that, apply suitable game logic
            if(playerCount%2 == 0) {
                //match players against each other based on score
                //create lobbies and assign players
                for((i, player) in players.withIndex()) {
                    lobbies.add(Lobby(players[i], players[i+1]).getThis())
                }
                //do match
                for(lobby in lobbies) lobby.doMatch()
                //wait for lobbies to finish
            }

            //check if more games are needed
            var nmbrOfAlive = 0
            for(player in players) if(player.alive) nmbrOfAlive++
            if(nmbrOfAlive == 1) gameStatus = false

        } while (gameStatus)
    }

    fun setChoice(choice : String) {
        val choice = choice
    }

    class Player {
        val id = Random.nextInt(0, 999)
        val color = "black"
        var alive = true
        val score = Random.nextInt(0,5)

        fun getThis() = this

        fun unAlive() {
            alive = false
        }

        fun makeChoice() {

        }
    }
    class Lobby(playerOne: Player, playerTwo: Player) {
        val id = Random.nextInt(0, 999)
        val playersInLobby = arrayOf(playerOne, playerTwo)
        var active = false
        fun getThis() = this

        fun doMatch() {
            playersInLobby
        }
    }

}
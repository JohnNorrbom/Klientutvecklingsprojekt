package com.hfad.klientutvecklingsprojekt

import androidx.lifecycle.ViewModel
import kotlin.random.Random

class StenSaxPaseViewModel : ViewModel() {

    //add bussiness logic for sten sax pase mini-game
    var players = ArrayList<Player>()
    var lobbies = ArrayList<Lobby>()
    init {
        setPlayerCount()
        gameLoop()
    }
    fun setPlayerCount() {

        //val randomNmbr = Random.nextInt(2, 5)
        val randomNmbr = 4
        var i = 0
        while(i < randomNmbr) {
            players.add(Player().getThis())
            i++
        }
    }

    private var playerCount: Int = players.size

    //gameStatus should remain true if any player is still "ingame"
    var gameStatus: Boolean = true

    //do game loop
    private fun gameLoop() {
        println("playerCount: ${playerCount}")
        do {
            //check if player count is even or odd, based on that, apply suitable game logic
            if(playerCount%2 == 0) {
                //match players against each other based on score
                //create lobbies and assign players
                for((i, player) in players.withIndex()) {
                    if(i%2 == 0) lobbies.add(Lobby(players[i], players[i+1]).getThis())
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

    fun setChoice(choice: String) {

    }

    class Player {
        val id = Random.nextInt(0, 999)
        val color = "black"
        var alive = true
        val score = Random.nextInt(0,5)
        var choice: String? = null

        //change this to false
        var cpu = true

        val choices = arrayOf("sten", "sax", "pase")

        fun getThis() = this

        fun unAlive() {
            alive = false
        }

        fun setCPU() {
            cpu = true
        }

        fun makeChoice(choice: String) {
            this.choice = choice
        }

        fun randomChoice() {
            this.choice = choices[Random.nextInt(0,3)]
        }
    }
    class Lobby(playerOne: Player, playerTwo: Player) {
        val id = Random.nextInt(0, 999)
        val playerOne = playerOne
        val playerTwo = playerTwo
        var active = false
        fun getThis() = this

        fun doMatch() {
            // mark lobby as active
            active = true
            var result: String

            do {
                if (playerOne.cpu) playerOne.randomChoice()
                //else playerOne.makeChoice()
                if (playerTwo.cpu) playerTwo.randomChoice()
                //else playerTwo.makeChoice()

                result = checkResult()
            } while(result.equals("even"))

            if(result.equals("playerOne")) playerTwo.unAlive()
            else playerOne.unAlive()
        }

        private fun checkResult(): String {
            var result: String

            if(playerOne.choice.equals(playerTwo.choice)) result = "even"
            else if(playerOne.choice.equals("sten") && playerTwo.choice.equals("sax")) result = "playerOne"
            else if(playerOne.choice.equals("sax") && playerTwo.choice.equals("pase")) result = "playerOne"
            else if(playerOne.choice.equals("pase") && playerTwo.choice.equals("sten")) result = "playerOne"
            else result = "playerTwo"

            println("won: $result , playerOne: ${playerOne.choice} , playerTwo: ${playerTwo.choice}" )

            return result
        }
    }

}
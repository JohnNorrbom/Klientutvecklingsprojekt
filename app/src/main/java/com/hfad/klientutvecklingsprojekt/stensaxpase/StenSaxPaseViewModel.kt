package com.hfad.klientutvecklingsprojekt.stensaxpase

import androidx.lifecycle.ViewModel
import kotlin.random.Random

class StenSaxPaseViewModel : ViewModel() {

    //add bussiness logic for sten sax pase mini-game
    var players = ArrayList<Player>()
    var lobbies = ArrayList<Lobby>()
    var id: Int = -1
    /*
    init {
        setPlayerCount()
        gameLoop()
    }
     */

    fun setID(id: Int) {
        this.id = id
        println(this.id)
    }

    fun initPlayers() {

        for(i in 0..1) {
            if(i == 0) players.add(Player(id).getThis())
            else players.add(Player(-1).getThis())
        }
    }

    //gameStatus should remain true if any player is still "ingame"
    var gameStatus: Boolean = true

    //do game loop
    private fun gameLoop() {
        //println("playerCount: $playerCount , $players.size")
        /*
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
         */
        do {

            //match players against each other based on score
            //create lobbies and assign players
            for((i, player) in players.withIndex()) {
                if(i%2 == 0) lobbies.add(Lobby(players[i], players[i+1]).getThis())
            }
            //do match
            for(lobby in lobbies) lobby.doMatch()
            //wait for lobbies to finish

            //check if more games are needed
            var nmbrOfAlive = 0
            for(player in players) if(player.alive) nmbrOfAlive++
            if(nmbrOfAlive == 1) gameStatus = false

        } while (gameStatus)
    }

    fun setChoice(choice: String) {
        initPlayers()
        for(player in players) {
            if(player.id == id) player.choice = choice
        }
        //gameLoop()
    }

    class Player(id:Int) {
        val id = id
        var alive = true
        val score = Random.nextInt(0,5)
        var choice: String? = null

        val cpu: Boolean = if( id == -1) true else false

        val choices = arrayOf("sten", "sax", "pase")

        fun getThis() = this

        fun unAlive() {
            alive = false
        }

        fun makeChoice(): String? {
            while(this.choice == null)
            {
                println("making choice...")
                println("${this.id} chose ${this.choice}")
                Thread.sleep(1000)
            }
            return this.choice
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
                println("playerOne is cpu: ${playerOne.cpu}")
                println("playerTwo is cpu: ${playerTwo.cpu}")
                if (playerOne.cpu) playerOne.randomChoice()
                else playerOne.makeChoice()
                if (playerTwo.cpu) playerTwo.randomChoice()
                else playerTwo.makeChoice()

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

            println("outcome: $result , playerOne chose: ${playerOne.choice} , playerTwo chose: ${playerTwo.choice}" )

            return result
        }
    }
}
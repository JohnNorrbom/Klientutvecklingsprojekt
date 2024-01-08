package com.hfad.klientutvecklingsprojekt.soccer

import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

/**
 * ViewModel for handling game logic and data related to the Soccer game.
 */
class SoccerViewModel() : ViewModel(){

    private var buttonCount = 0
    private var p1Color = "not assigned"
    private var p2Color = "not assigned"
    private var type = "not assigned"
    private var enemyType = "not assigned"
    var p1Points = 0
    var p2Points = 0
    var shooterChoice = "not assigned"
    var goalieChoice = "not assigned"
    private var round = 1
    var animationReady = false
    var shooterHit = false
    var shooterColor = p1Color
    var goalieColor = p2Color
    var player1 = "shooter"
    var player2 = "goalie"
    /**
     * Retrieves the color of player 1.
     */
    fun getColor(): String {
        return p1Color
    }
    /**
     * Retrieves the color of player 2 (enemy).
     */
    fun getEnemyColor(): String {
        return p2Color
    }
    /**
     * Sets the colors of player 1 and player 2 based on provided colors and who shoots first.
     */
    fun setColors(p1Color: String, p2Color: String, whoShootsFirst: Int){
        this.p1Color = p1Color
        this.p2Color = p2Color

        if(whoShootsFirst == 1){
            shooterColor = p1Color
            goalieColor = p2Color
        }else{
            shooterColor = p2Color
            goalieColor = p1Color
        }

    }
    /**
     * Sets the chosen direction ('left') for the shooter or goalie based on the player number and role.
     * @param playerNbr The player number indicating the player making the move.
     */
    fun leftButtonClick(playerNbr: Int){
        if (playerNbr == 1 && player1 == "shooter"){
            shooterChoice = "left"
        }
        if (playerNbr == 1 && player1 == "goalie"){
            goalieChoice = "left"
        }
        if (playerNbr == 2 && player2 == "shooter"){
            shooterChoice = "left"
        }
        if (playerNbr == 2 && player2 == "goalie"){
            goalieChoice = "left"
        }

    }
    /**
     * Sets the chosen direction ('right') for the shooter or goalie based on the player number and role.
     * @param playerNbr The player number indicating the player making the move.
     */
    fun rightButtonClick(playerNbr: Int){
        if (playerNbr == 1 && player1 == "shooter"){
            shooterChoice = "right"
        }
        if (playerNbr == 1 && player1 == "goalie"){
            goalieChoice = "right"
        }
        if (playerNbr == 2 && player2 == "shooter"){
            shooterChoice = "right"
        }
        if (playerNbr == 2 && player2 == "goalie"){
            goalieChoice = "right"
        }
    }
    /**
     * Sets the chosen direction ('mid') for the shooter or goalie based on the player number and role.
     * @param playerNbr The player number indicating the player making the move.
     */
    fun midButtonClick(playerNbr: Int){
        if (playerNbr == 1 && player1 == "shooter"){
            shooterChoice = "mid"
        }
        if (playerNbr == 1 && player1 == "goalie"){
            goalieChoice = "mid"
        }
        if (playerNbr == 2 && player2 == "shooter"){
            shooterChoice = "mid"
        }
        if (playerNbr == 2 && player2 == "goalie"){
            goalieChoice = "mid"
        }
    }

    /**
     * Switches the roles/types of player 1 and player 2.
     */
    fun switchType(){
        //change color of goalie and shooter
        var temp = goalieColor
        goalieColor = shooterColor
        shooterColor = temp

        //change player of goalie and shooter
        if(player1 == "shooter"){
            player1 = "goalie"
            player2 = "shooter"
        }else{
            player1 = "shooter"
            player2 = "goalie"
        }
    }

    /**
     * Starts a new round of the game, calculates points, and updates game state.
     */
    fun startRound(){
        shooterHit = false
        println("shooterChoice: " + shooterChoice)
        println("goalieChoice: " + goalieChoice)
        println("p1 is: " + player1)
        println("p2 is: " + player2)
        if(goalieChoice == "right" && shooterChoice == "left"){
            if(player1 == "shooter"){
                p1Points++

            }else{
                p2Points++
            }
            shooterHit = true
        }

        if(goalieChoice == "mid" && shooterChoice == "left"){
            if(player1 == "shooter"){
                p1Points++

            }else{
                p2Points++
            }
            shooterHit = true
        }
        if(goalieChoice == "left" && shooterChoice == "mid"){
            if(player1 == "shooter"){
                p1Points++

            }else{
                p2Points++
            }
            shooterHit = true
        }
        if(goalieChoice == "right" && shooterChoice == "mid"){
            if(player1 == "shooter"){
                p1Points++
            }else{
                p2Points++
            }
            shooterHit = true
        }
        if(goalieChoice == "left" && shooterChoice == "right"){
            if(player1 == "shooter"){
                p1Points++
            }else{
                p2Points++
            }
            shooterHit = true
        }
        if(goalieChoice == "mid" && shooterChoice == "right"){
            if(player1 == "shooter"){
                p1Points++
            }else{
                p2Points++
            }
            shooterHit = true
        }
        round++
    }
}
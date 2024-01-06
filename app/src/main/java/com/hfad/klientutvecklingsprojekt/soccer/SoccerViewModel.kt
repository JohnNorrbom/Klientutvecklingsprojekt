package com.hfad.klientutvecklingsprojekt.soccer

import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class SoccerViewModel() : ViewModel(){

    val databaseReference = FirebaseDatabase.getInstance().getReference("Soccer")


    private var buttonCount = 0
    private var color = "not assigned"
    private var enemyColor = "not assigned"
    private var type = "not assigned"
    private var enemyType = "not assigned"
    var p1Points = 0
    var p2Points = 0
    var shooterChoice = "not assigned"
    var goalieChoice = "not assigned"
    private var round = 1
    var animationReady = false
    var shooterHit = false
    var shooterColor = color
    var goalieColor = enemyColor
    var player1 = "shooter"
    var player2 = "goalie"

    fun getColor(): String {
        return color
    }
    fun getEnemyColor(): String {
        return enemyColor
    }
    fun setColors(color: String, enemyColor: String, whoShootsFirst: Int){
        this.color = color
        this.enemyColor = enemyColor

        if(whoShootsFirst == 1){
            shooterColor = color
            goalieColor = enemyColor
        }else{
            shooterColor = enemyColor
            goalieColor = color
        }

    }

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


    fun switchType(){
        //change color of goalie and shooter
        var temp = goalieColor
        goalieColor = shooterColor
        shooterColor = temp

        //change type of goalie and shooter

        if(player1 == "shooter"){
            player1 = "goalie"
            player2 = "shooter"
        }else{
            player2 = "shooter"
            player1 = "goalie"
        }
    }


    fun startRound(){
        shooterHit = false
        if(round == 1){
            player1 = "shooter"
            player2 = "goalie"
        }

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
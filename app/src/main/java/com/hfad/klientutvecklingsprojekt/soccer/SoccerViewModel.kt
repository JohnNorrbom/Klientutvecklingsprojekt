package com.hfad.klientutvecklingsprojekt.soccer

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.gavleroulette.GameStatus
import com.hfad.klientutvecklingsprojekt.gavleroulette.PlayerStatus
import kotlin.random.Random

class SoccerViewModel() : ViewModel(){

    val databaseReference = FirebaseDatabase.getInstance().getReference("Soccer")


    private var buttonCount = 0
    private var color = "not assigned"
    private var enemyColor = "not assigned"
    private var type = "not assigned"
    private var enemyType = "not assigned"
    var points = 0
    var enemyPoints = 0
    var shooterChoice = "not assigned"
    var goalieChoice = "not assigned"
    private var round = 1
    var animationReady = false
    var shooterHit = false
    var shooterColor = color
    var goalieColor = enemyColor

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

    fun setTypes(type: String, enemyType: String){
        this.type = type
        this.enemyType = enemyType
    }

    fun getType(): String{
        return type
    }

    fun getEnemyType(): String{
        return enemyType
    }

    fun leftButtonClick(isPlayer1: Boolean){
        Log.d("Count", "" + buttonCount)

        if(buttonCount%2 == 0){
            shooterChoice = "left"
            animationReady = false

        }
        if(buttonCount%2 == 1){
            goalieChoice = "left"
            startRound()
            animationReady = true
        }
        buttonCount++
    }
    fun rightButtonClick(isPlayer1: Boolean){
        Log.d("Count", "" + buttonCount)

        if(buttonCount%2 == 0){
            shooterChoice = "right"
            animationReady = false
        }
        if(buttonCount%2 == 1){
            goalieChoice = "right"
            startRound()
            animationReady = true
        }
        buttonCount++
    }
    fun midButtonClick(isPlayer1: Boolean){
        Log.d("Count", "" + buttonCount)

        if(buttonCount%2 == 0){
            shooterChoice = "mid"
            animationReady = false
        }
        if(buttonCount%2 == 1){
            goalieChoice = "mid"
            startRound()
            animationReady = true
        }
        buttonCount++
    }


    fun switchType(){
        //change color of goalie and shooter
        var temp = goalieColor
        goalieColor = shooterColor
        shooterColor = temp

        //change type of goalie and shooter

        if(type == "shooter"){
            type = "goalie"
            enemyType = "shooter"
        }else{
            type = "shooter"
            enemyType = "goalie"
        }
    }


    fun startRound(){
        shooterHit = false
        if(round == 1){
            type = "shooter"
            enemyType = "goalie"
        }

        if(goalieChoice == "right" && shooterChoice == "left"){
            if(type == "shooter"){
                points++

            }else{
                enemyPoints++
            }
            shooterHit = true
        }

        if(goalieChoice == "mid" && shooterChoice == "left"){
            if(type == "shooter"){
                points++

            }else{
                enemyPoints++
            }
            shooterHit = true
        }
        if(goalieChoice == "left" && shooterChoice == "mid"){
            if(type == "shooter"){
                points++

            }else{
                enemyPoints++
            }
            shooterHit = true
        }
        if(goalieChoice == "right" && shooterChoice == "mid"){
            if(type == "shooter"){
                points++
            }else{
                enemyPoints++
            }
            shooterHit = true
        }
        if(goalieChoice == "left" && shooterChoice == "right"){
            if(type == "shooter"){
                points++
            }else{
                enemyPoints++
            }
            shooterHit = true
        }
        if(goalieChoice == "mid" && shooterChoice == "right"){
            if(type == "shooter"){
                points++
            }else{
                enemyPoints++
            }
            shooterHit = true
        }
        round++
    }
}
package com.hfad.klientutvecklingsprojekt.gavleroulette
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.ActivityMainsBinding
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playOfflineBtn.setOnClickListener{
            createOfflinGame()
        }

        binding.createOnlineGameBtn.setOnClickListener{
            createOnlineGame()
        }

        binding.joinOnlineGameBtn.setOnClickListener(){
            joinOnlineGame()
        }
    }

    fun createOfflinGame(){
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.JOINED
            )
        )
        startGame()
    }
    fun createOnlineGame(){
        GameData.myId= "1"
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.CREATED,
                gameId = Random.nextInt(1000..9999).toString()
            )
        )
        startGame()
    }

    fun joinOnlineGame(){
        var gameId = binding.gameIdInput.text.toString()
        if(gameId.isEmpty()){
            binding.gameIdInput.error=(getText(R.string.please_enter_game_id))
            return
        }

    }

    fun startGame(){
        {/** startActivity(Intent(this,GameActivity::class.java))*/}
    }
}
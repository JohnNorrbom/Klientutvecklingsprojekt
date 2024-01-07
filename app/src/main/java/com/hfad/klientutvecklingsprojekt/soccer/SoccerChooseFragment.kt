package com.hfad.klientutvecklingsprojekt.soccer

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentSoccerBinding
import com.hfad.klientutvecklingsprojekt.databinding.FragmentSoccerChooseBinding
import com.hfad.klientutvecklingsprojekt.gamestart.CharacterStatus
import com.hfad.klientutvecklingsprojekt.gamestart.GameData
import com.hfad.klientutvecklingsprojekt.gamestart.GameModel
import com.hfad.klientutvecklingsprojekt.gamestart.Progress
import com.hfad.klientutvecklingsprojekt.gavleroulette.RouletteData.myRef
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import kotlin.random.Random
import kotlin.random.nextInt


class SoccerChooseFragment : Fragment() {

    private var mediaPlayer: MediaPlayer? = null

    private var _binding: FragmentSoccerChooseBinding? = null
    private val binding get() = _binding!!

    //meModel
    private var meModel : MeModel?= null//den här

    private val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Player Data")

    //Here you should get your color from meDataModel or boardModel
    private var yourColor: String  = ""
    private var yourId: String = "-1"

    //Here you should get the other colors from boardModel
    private var otherColors = arrayListOf("")

    //id for other players
    private var otherIds = arrayListOf("-1")

    //othermap is hashmap of otherids and othercolors
    private var otherMap = hashMapOf<String,String>()

    //here you should get the id from boardModel
    private var gameId: String = Random.nextInt(1000..9999).toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoccerChooseBinding.inflate(inflater,container,false)

        binding.astroBlue.visibility = View.GONE
        binding.astroWhite.visibility = View.GONE
        binding.astroGreen.visibility = View.GONE
        binding.astroRed.visibility = View.GONE
        binding.astroYellow.visibility = View.GONE
//den här
        MeData.meModel.observe(this) { meModel ->
            meModel?.let {
                this@SoccerChooseFragment.meModel = it
                setValues()
                otherColors()

            } ?: run {
                Log.e("LobbyFragment", "meModel is null")
            }
        }



        binding.astroBlue.setOnClickListener {
            if(yourColor != "blue"){
                createSoccerGame("blue", gameId)
                view?.findNavController()?.navigate(R.id.action_soccerChooseFragment_to_soccerFragment)
            }

        }
        binding.astroRed.setOnClickListener {
            if(yourColor != "red"){
            createSoccerGame("red" , gameId)
            view?.findNavController()?.navigate(R.id.action_soccerChooseFragment_to_soccerFragment)
            }
        }
        binding.astroYellow.setOnClickListener {
            if(yourColor != "yellow"){
            createSoccerGame("yellow" , gameId)
            view?.findNavController()?.navigate(R.id.action_soccerChooseFragment_to_soccerFragment)
            }
        }
        binding.astroGreen.setOnClickListener {
            if(yourColor != "green"){
            createSoccerGame("green" , gameId)
            view?.findNavController()?.navigate(R.id.action_soccerChooseFragment_to_soccerFragment)
            }
        }
        binding.astroWhite.setOnClickListener {
            if (yourColor != "white"){
            createSoccerGame("white" , gameId)
            view?.findNavController()?.navigate(R.id.action_soccerChooseFragment_to_soccerFragment)
        }
    }

        val view = binding.root


        return view
    }

//TODO MÅSTE ÄNDRAS
    fun createSoccerGame(p2Color: String, gameId: String){
        SoccerData.saveSoccerModel(
            SoccerModel(gameId,0,0,"","", yourColor,p2Color,false, yourId, otherMap.get(p2Color))
        )
    }

    fun setColorButtonVisible(){
        for (color in otherColors){
            if(color == "blue" && yourColor != "blue"){
                binding.astroBlue.visibility = View.VISIBLE
            }
            if(color == "white"&& yourColor != "white"){
                binding.astroWhite.visibility = View.VISIBLE
            }
            if(color == "green" && yourColor != "green"){
                binding.astroGreen.visibility = View.VISIBLE
            }
            if(color == "red"&& yourColor != "red"){
                binding.astroRed.visibility = View.VISIBLE
            }
            if(color == "yellow"&& yourColor != "yellow"){
                binding.astroYellow.visibility = View.VISIBLE
            }
        }
    }

    fun setValues(){
        meModel?.apply {
            gameId = gameID?:""
            yourId = playerID?:""
        }
    }
    fun getYourData(){
    }

    fun otherColors(){
        otherColors = arrayListOf()
        otherIds = arrayListOf()
        var colorArr = arrayListOf<String>()
        myRef.child(gameId).child("players").get()
            .addOnSuccessListener { dataSnapshot ->
                dataSnapshot.children.forEach { playerSnapshot ->
                    val playerId = playerSnapshot.child("playerID").value.toString()
                    val color = playerSnapshot.child("color").value.toString()
                    if(playerId == yourId){
                        yourColor = color
                    }
                    otherColors.add(color)
                    otherIds.add(playerId)
                    otherMap.set(color,playerId)
                }
                setColorButtonVisible()
            }
    }
}
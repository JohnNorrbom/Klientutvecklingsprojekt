package com.hfad.klientutvecklingsprojekt.stensaxpase

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentStenSaxPaseChooseBinding
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import kotlin.random.Random
import kotlin.random.nextInt

class StenSaxPaseChooseFragment : Fragment() {

    private var _binding: FragmentStenSaxPaseChooseBinding? = null
    private val binding get() = _binding!!
    private var meModel : MeModel? = null

    var currentGameID : String = ""
    var currentPlayerID : String = ""

    var gameID:String = ""
    var playerID:String = ""
    var opponentID:String = ""

    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val playerDataRef = database.getReference("Player Data")

    //Here you should get the other colors from boardModel
    private var otherColors = arrayListOf("blue", "white", "red", "yellow", "green")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // establish binding
        _binding = FragmentStenSaxPaseChooseBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.astroBlue.visibility = View.GONE
        binding.astroWhite.visibility = View.GONE
        binding.astroGreen.visibility = View.GONE
        binding.astroRed.visibility = View.GONE
        binding.astroYellow.visibility = View.GONE
        println("hejsan")
        MeData.meModel.observe(context as LifecycleOwner) { meModel ->
            meModel?.let {
                this@StenSaxPaseChooseFragment.meModel = it
                setText()
                setID(currentGameID, currentPlayerID)
                loadPlayersFromGameID()
            } ?: run {
                // Handle the case when meModel is null
                Log.e("StenSaxPaseFragment", "meModel is null")
            }
        }

        if(gameID == "") {
            gameID = "4367"
            playerID = "2314"
        }
        println("$gameID--$playerID")
        loadPlayersFromGameID()

        binding.astroBlue.setOnClickListener {
            createStenSaxPaseGame(playerID!!,opponentID!!)
            view?.findNavController()?.navigate(R.id.action_stenSaxPaseChooseFragment_to_stensaxpaseFragment)

        }
        binding.astroRed.setOnClickListener {
            createStenSaxPaseGame(playerID!!,opponentID!!)
            view?.findNavController()?.navigate(R.id.action_stenSaxPaseChooseFragment_to_stensaxpaseFragment)
        }
        binding.astroYellow.setOnClickListener {
            createStenSaxPaseGame(playerID!!,opponentID!!)
            view?.findNavController()?.navigate(R.id.action_stenSaxPaseChooseFragment_to_stensaxpaseFragment)
        }
        binding.astroGreen.setOnClickListener {
            createStenSaxPaseGame(playerID!!,opponentID!!)
            view?.findNavController()?.navigate(R.id.action_stenSaxPaseChooseFragment_to_stensaxpaseFragment)
        }
        binding.astroWhite.setOnClickListener {
            createStenSaxPaseGame(playerID!!,opponentID!!)
            view?.findNavController()?.navigate(R.id.action_stenSaxPaseChooseFragment_to_stensaxpaseFragment)
        }

        return view
    }

    fun loadPlayersFromGameID() {
        println("hello")
        playerDataRef.child(gameID).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            // Prepare Map which players will go into
            val players : MutableMap<String,MutableMap<String,String>> = mutableMapOf()
            // Loop through all players and add to local 'players' Map
            for(player in it.child("players").children) {
                players.put("${player.child("playerID").value}", mutableMapOf(
                    "nickname" to "${player.child("nickname").value}",
                    "color" to "${player.child("color").value}",
                    "choice" to "null",
                    "score" to "0"
                ))
            }
            println("---$players")
            setUI(players!!)
        }
    }

    fun setUI(players:MutableMap<String,MutableMap<String,String>>) {
        for (player: Any? in players){
            println(player)
            /*
            if(player.getValue("color") == "blue"){
                binding.astroBlue.visibility = View.VISIBLE
            }
            if(color == "white"){
                binding.astroWhite.visibility = View.VISIBLE
            }
            if(color == "green"){
                binding.astroGreen.visibility = View.VISIBLE
            }
            if(color == "red"){
                binding.astroRed.visibility = View.VISIBLE
            }
            if(color == "yellow"){
                binding.astroYellow.visibility = View.VISIBLE
            }

             */
        }
    }

    fun setText() {
        currentGameID = meModel?.gameID ?: ""
        currentPlayerID = meModel?.playerID ?: ""
        Log.d("StenSaxPaseFragment", "playerID: ${currentPlayerID} GameID: ${currentGameID}")
    }

    fun setID(gameID:String,playerID:String) {
        this.gameID = gameID
        this.playerID = playerID
    }

    fun createStenSaxPaseGame(playerID:String,opponentID:String) {

    }

}
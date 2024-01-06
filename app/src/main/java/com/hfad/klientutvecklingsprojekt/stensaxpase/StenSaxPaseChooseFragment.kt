package com.hfad.klientutvecklingsprojekt.stensaxpase

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.databinding.FragmentStenSaxPaseChooseBinding
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel

class StenSaxPaseChooseFragment : Fragment() {

    private var _binding: FragmentStenSaxPaseChooseBinding? = null
    private val binding get() = _binding!!
    private var meModel : MeModel? = null

    var currentGameID : String = ""
    var currentPlayerID : String = ""

    var gameID:String = ""
    var playerID:String = ""
    private var opponentID:String = ""

    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val playerDataRef = database.getReference("Player Data")

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
        /*
        // Remove this once testing done
        if(gameID == "") {
            gameID = "5369"
            playerID = "1986"
            println("$gameID--$playerID")
            loadPlayersFromGameID()
        }
        // End of test section
         */
        binding.astroBlue.setOnClickListener {
            setOpponentID("blue")
            createStenSaxPaseGame(playerID!!,opponentID!!,view)
            //view?.findNavController()?.navigate(R.id.action_stenSaxPaseChooseFragment_to_stensaxpaseFragment)

        }
        binding.astroRed.setOnClickListener {
            setOpponentID("red")
            createStenSaxPaseGame(playerID!!,opponentID!!,view)
            //view?.findNavController()?.navigate(R.id.action_stenSaxPaseChooseFragment_to_stensaxpaseFragment)
        }
        binding.astroYellow.setOnClickListener {
            setOpponentID("yellow")
            createStenSaxPaseGame(playerID!!,opponentID!!,view)
            //view?.findNavController()?.navigate(R.id.action_stenSaxPaseChooseFragment_to_stensaxpaseFragment)
        }
        binding.astroGreen.setOnClickListener {
            setOpponentID("green")
            createStenSaxPaseGame(playerID!!,opponentID!!,view)
            //view?.findNavController()?.navigate(R.id.action_stenSaxPaseChooseFragment_to_stensaxpaseFragment)
        }
        binding.astroWhite.setOnClickListener {
            setOpponentID("white")
            createStenSaxPaseGame(playerID!!,opponentID!!,view)
            //view?.findNavController()?.navigate(R.id.action_stenSaxPaseChooseFragment_to_stensaxpaseFragment)
        }

        return view
    }
    // Prepare Map which players will go into
    val players : MutableMap<String,MutableMap<String,String>> = mutableMapOf()
    fun loadPlayersFromGameID() {
        playerDataRef.child(gameID).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            // Loop through all players and add to local 'players' Map
            for(player in it.child("players").children) {
                players.put("${player.child("playerID").value}", mutableMapOf(
                    "nickname" to "${player.child("nickname").value}",
                    "color" to "${player.child("color").value}"
                ))
            }
            println("---$players")
            setUI(players!!)
        }
    }

    private fun setOpponentID(color:String) {
        players.forEach { entry ->
            if(entry.key != playerID) {
                if (entry.value.get("color") == color) {
                    opponentID = entry.key
                }
            }
        }
    }

    fun setUI(players:MutableMap<String,MutableMap<String,String>>) {
        players.forEach { entry ->
            if(entry.key != playerID) {
                var color = entry.value.get("color")
                if (color == "blue") {
                    binding.astroBlue.visibility = View.VISIBLE
                }
                if (color == "white") {
                    binding.astroWhite.visibility = View.VISIBLE
                }
                if (color == "green") {
                    binding.astroGreen.visibility = View.VISIBLE
                }
                if (color == "red") {
                    binding.astroRed.visibility = View.VISIBLE
                }
                if (color == "yellow") {
                    binding.astroYellow.visibility = View.VISIBLE
                }
            }
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

    fun createStenSaxPaseGame(playerID:String,opponentID:String,view:ConstraintLayout) {
        println("__$playerID and __$opponentID")
        val action = StenSaxPaseChooseFragmentDirections.actionStenSaxPaseChooseFragmentToStensaxpaseFragment(playerID,opponentID)
        view?.findNavController()?.navigate(action)
    }
    /*
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
     */
}
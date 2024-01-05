package com.hfad.klientutvecklingsprojekt.gavleroulette

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.databinding.FragmentGavleRouletteBinding
import com.hfad.klientutvecklingsprojekt.gamestart.CharacterStatus
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import kotlin.random.Random

class GavleRouletteFragment : Fragment(){
    private var _binding: FragmentGavleRouletteBinding? = null
    private val binding get() = _binding!!
    private var meModel : MeModel?=null
    private var rouletteModel: RouletteModel? = null
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Roulette")
    val lobbyRef = database.getReference("Player Data")
    var localPlayerID =""
    var localGameID = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGavleRouletteBinding.inflate(inflater, container, false)
        val view = binding.root
        RouletteData.fetchGameModel()


        RouletteData.rouletteModel.observe(viewLifecycleOwner) {rouletteModel ->
            Log.d("GavleRouletteFragment", "Observing rouletteModel: $rouletteModel")
            rouletteModel?.let {
                if (it.gameId == null){
                    Log.e("GavleRouletteFragment", "rouletteModel is null")
                }else{
                    this@GavleRouletteFragment.rouletteModel = it
                    setUi()
                    addBullet()

                }
            }
        }

        MeData.meModel.observe(this) { meModel ->
            meModel?.let {
                this@GavleRouletteFragment.meModel = it
                setText()
            } ?: run {
                // Handle the case when meModel is null
                Log.e("LobbyFragment", "meModel is null")
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rouletAction.setOnClickListener{
            onTriggerPulled()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    fun setText(){
        //den här
        localGameID = meModel?.gameID?:""
        localPlayerID = meModel?.playerID?:""
        Log.d("meModel","player ${localGameID} Game ${localPlayerID}")
    }

    fun setUi() {
        rouletteModel?.apply {
            lobbyRef.child(localGameID).child("players").get().addOnSuccessListener {
                val snapshot = it
                var text = ""
            binding.gameStatusText.text =
                when (gameStatus) {
                    GameStatus.INPROGRESS -> {
                        if (laps == 0 && attempts == 0) {
                            setPlayerInfo()
                            val resId = resources.getIdentifier(
                                "chamber",
                                "drawable",
                                requireContext().packageName
                            )
                            binding.magasinSlot.setImageResource(resId)
                            binding.test.text = luckyNumber?.get(0)
                        }
                        when (localPlayerID) {
                            currentPlayer -> text = "Your turn"
                            else -> text = snapshot.child(currentPlayer ?: "").child("nickname").value.toString() + " turn"
                        }
                        text
                    }

                    GameStatus.FINISHED -> {
                        when (localPlayerID) {
                            winner -> text = "You won"
                            else -> text = snapshot.child(winner ?: "").child("nickname").value.toString() + " won"
                        }
                        text
                    }

                    else -> {return@addOnSuccessListener}
                }
            }
        }
    }
    fun onTriggerPulled(){
        myRef.child(localGameID).child("currentPlayer").get().addOnSuccessListener {
            Log.d("localPlayerID","${localPlayerID}")
            Log.d("localPlayerID","${it.value}")
            if(localPlayerID != it.value){
                Toast.makeText(context?.applicationContext ?: context,"Not your turn",Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }else{
                addBullet()
                changePlayer()
                pullTheTrigger()
                checksForKill()
                checkForWinner()
            }
        }
    }

    //sets the correct info for each player
    fun setPlayerInfo(){
        rouletteModel?.apply {
            lobbyRef.child(localGameID).child("players").get().addOnSuccessListener {
                val snapshot = it
                   players?.onEachIndexed { index, entry ->
                       Log.d("player i setPlayerInfo","${snapshot.child(players?.keys?.elementAt(index) ?:"").child("color").value}")
                       Log.d("player index setPlayerInfo","${index+1}")
                       val player = snapshot.child(players?.keys?.elementAt(index) ?:"").toString()
                       if (players?.get(player) == PlayerStatus.ALIVE){
                           val resId = resources.getIdentifier(
                               "astro_${snapshot.child(players?.get(player).toString()).child("color").value.toString()}",
                               "drawable",
                               requireContext().packageName
                           )
                           //get id for Imageview
                           val astroId = resources.getIdentifier(
                               "player_${index+1}",
                               "id",
                               requireContext().packageName
                           )

                           // change imageView
                           val characterImageView =
                               binding.root.findViewById<ImageView>(astroId)
                           Log.d("resId", "${resId}")
                           Log.d("astroId", "${astroId}")
                           Log.d("characterImageView", "${characterImageView}")
                           characterImageView.setImageResource(resId)
                           characterImageView.visibility = View.VISIBLE

                           // get id for radio button and makes it visible
                           val textId = resources.getIdentifier(
                               "player_${index+1}_text",
                               "id",
                               requireContext().packageName
                           )
                           val text = binding.root.findViewById<TextView>(textId)
                           text.text = snapshot.child(players?.keys?.elementAt(index) ?:"").child("nickname").value.toString()
                           text.visibility = View.VISIBLE
                       }else{
                           val resId = resources.getIdentifier("sceleton", "drawable", requireContext().packageName)

                           //get id for Imageview
                           val playerId = resources.getIdentifier(
                               "player_${index+1}",
                               "id",
                               requireContext().packageName
                           )

                           // change imageView
                           val characterImageView =
                               binding.root.findViewById<ImageView>(playerId)
                           Log.d("resId", "${resId}")
                           Log.d("astroId", "${playerId}")
                           Log.d("characterImageView", "${characterImageView}")
                           characterImageView.setImageResource(resId)
                       }
                   }
            }
        }
    }

    //adds a value to currentBullet and pluses on how many attempts and laps thier has been
    fun pullTheTrigger() {
        rouletteModel?.apply {
            currentBullet = Random.nextInt(6) + 1
            Log.d("currentBullet","${currentBullet}")
            Log.d("attempts","${attempts}")
            attempts = attempts?.plus(1)
            Log.d("attempts","${attempts}")
            Log.d("nbrOfPlayers","${this.nbrOfPlayers}")
            if (attempts == nbrOfPlayers) {
                attempts = 0
                laps = laps?.plus(1)
            }
            binding.test1.text = "current attempts" + attempts.toString()
            binding.test2.text = "total laps " + laps.toString()
            updateGameData(this,localGameID)
        }
    }

    fun addBullet() {
        rouletteModel?.apply {
            if (laps == 2 && luckyNumber?.get(1)?.isEmpty() ?: false) {
                var temp: String
                do {
                    temp = (Random.nextInt(6) + 1).toString()
                } while (temp == luckyNumber?.get(0))
                luckyNumber?.add(temp)
                binding.test.text = (luckyNumber?.get(0) + " " + luckyNumber?.get(1) )
                val resId = resources.getIdentifier("chamber_2_bullet", "drawable", requireContext().packageName)
                binding.magasinSlot.setImageResource(resId)
            }
            if (laps == 3 && luckyNumber?.get(2)?.isEmpty() ?: false) {
                var temp: String
                do {
                    temp = (Random.nextInt(6) + 1).toString()
                } while (luckyNumber?.contains(temp) ?: true || temp == luckyNumber?.get(2))
                luckyNumber?.add(temp)
                binding.test.text = luckyNumber?.get(0) + " " + luckyNumber?.get(1) + " " + luckyNumber?.get(2)
                val resId = resources.getIdentifier("chamber_3_bullet", "drawable", requireContext().packageName)
                binding.magasinSlot.setImageResource(resId)
            }
            if (laps == 4 && luckyNumber?.get(3)?.isEmpty() ?: false) {
                var temp: String
                do {
                    temp = (Random.nextInt(6) + 1).toString()
                } while (luckyNumber?.contains(temp) ?: true || temp == luckyNumber?.get(3))
                luckyNumber?.add(temp)
                binding.test.text =
                    luckyNumber?.get(0) + " " + luckyNumber?.get(1) + " " + luckyNumber?.get(2) + " " + luckyNumber?.get(3)
                val resId = resources.getIdentifier("chamber_4_bullet", "drawable", requireContext().packageName)
                binding.magasinSlot.setImageResource(resId)
            }
            if (laps == 5 && luckyNumber?.get(4)?.isEmpty()?: false) {
                var temp: String
                do {
                    temp = (Random.nextInt(6) + 1).toString()
                } while (luckyNumber?.contains(temp) ?: true || temp == luckyNumber?.get(4))
                luckyNumber?.add(temp)
                binding.test.text =
                    luckyNumber?.get(0) + " " + luckyNumber?.get(1) + " " + luckyNumber?.get(2) + " " + luckyNumber?.get(3) + " " + luckyNumber?.get(4)
                val resId = resources.getIdentifier("chamber_5_bullet", "drawable", requireContext().packageName)
                binding.magasinSlot.setImageResource(resId)
            }
            updateGameData(this,localGameID)
        }
    }

    fun startGame() {
        rouletteModel?.apply {
            updateGameData(
                RouletteModel(
                    gameStatus = GameStatus.INPROGRESS,
                ),localGameID

            )
        }
    }

//saves the model
    fun updateGameData(model: RouletteModel,id : String) {
        RouletteData.saveGameModel(model,id)
    }
    //Changes to the next player
    fun changePlayer() {
        rouletteModel?.apply {
            myRef.child(localGameID).child("currentPlayer").get().addOnSuccessListener { snapshot ->
                Log.d("snapshot","${snapshot.value}")
                val currentPlayerIndex = players?.keys?.indexOf(snapshot.value) ?: -1
                Log.d("player keys","${players?.keys}")
                Log.d("currentPlayerIndex","${players?.keys?.indexOf(snapshot.value)}")
                Log.d("currentPlayerIndex","${currentPlayerIndex}")

                if (currentPlayerIndex != -1) {
                    var newIndex = (currentPlayerIndex + 1) % players?.size!!
                    Log.d("newIndex","${newIndex}")

                    // Find the next alive player
                    while (players?.get(players?.keys?.elementAt(newIndex) ?: "") == PlayerStatus.DEAD) {
                        newIndex = (newIndex + 1) % players?.size!!
                    }

                    Log.d("newIndex","${newIndex}")
                    Log.d("currentPlayer","${currentPlayer}")
                    currentPlayer = players?.keys?.elementAt(newIndex) ?: ""
                    Log.d("newcurrentPlayer","${currentPlayer}")
                    Log.d("newcurrentPlayer","${this}")
                    updateGameData(this, localGameID)
                }
            }
        }
    }
    //Looks if anny of the bullets is equal to the current bullet if it is equal it changes the status for currentPlayer
    fun checksForKill() {
        rouletteModel?.apply {
            if (luckyNumber?.contains(currentBullet.toString()) == true) {
                players?.put(currentPlayer?:"",PlayerStatus.DEAD)
                aliveCount = aliveCount?.minus(1)
                updateGameData(this,localGameID)
            }
        }
    }
    //if only one player is alive then it means that we have a winner
    fun checkForWinner(){
        rouletteModel?.apply {
            if (aliveCount == 1) {
                for (i in 0 until nbrOfPlayers!!){
                    if (players?.get(players?.keys?.elementAt(i)) == PlayerStatus.ALIVE){
                        winner = players?.keys?.elementAt(i)
                        break
                    }
                }
                gameStatus = GameStatus.FINISHED
                updateGameData(this,localGameID)
            }
        }
    }

}
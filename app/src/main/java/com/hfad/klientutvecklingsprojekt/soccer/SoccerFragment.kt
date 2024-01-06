package com.hfad.klientutvecklingsprojekt.soccer

import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentSoccerBinding
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import kotlinx.coroutines.delay
import kotlin.concurrent.thread

class SoccerFragment : Fragment() {

    private lateinit var soccerViewModel: SoccerViewModel
    private var mediaPlayer: MediaPlayer? = null
    private var soccerModel : SoccerModel? = null
    private var _binding: FragmentSoccerBinding? = null

    private val binding get() = _binding!!

    private var goalieColor = "yellow"
    private var shooterColor = "red"
    var text: String = ""
    private var yourId = ""


    //player choices
    private var p1Choice: String = ""
    private var p2Choice: String = ""

    //meModel
    private var meModel : MeModel? = null

    //online variables
    private var localP1Id = ""
    private var localP2Id = ""
    private var localGameId = ""

    //  DATABASE
    private val database =
        Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Soccer")

    //variable that see if you are p1 or p2
    private var youArePlayerOne: Boolean = false
    private var youArePlayerTwo: Boolean = false

    //variables to see if both player are ready
    private var bothIsReady: Boolean = false

    //variables to see if both player are ready
    private var p1IsReady: Boolean = false
    private var p2IsReady: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoccerBinding.inflate(inflater, container, false)
        val view = binding.root

        MeData.meModel.observe(this){
            meModel = it
            retrieveYourId()
            SoccerData.fetchSoccerModel()
            SoccerData.soccerModel.observe(this) {
                soccerModel = it

            }
            setValues()
        }


        return view
    }

    fun retrieveYourId(){
        meModel?.apply {
            yourId = playerID.toString()
            localGameId = gameID.toString()
            println("retrieve your id: yourid: " + yourId)
            myRef.child(localGameId).child("p1Choice").addValueEventListener(p1Listener)
            myRef.child(localGameId).child("p2Choice").addValueEventListener(p2Listener)
            myRef.child(localGameId).child("bothPlayerReady").addValueEventListener(bothReadyListener)
        }
    }

    private val p1Listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            myRef.child(localGameId).child("p1Choice").get().addOnSuccessListener { dataSnapshot ->
                println("what p1Choice is: "+dataSnapshot.value.toString())
                if(dataSnapshot.value.toString() != "" && !p1IsReady){
                    println("p1Listener: p1 is ready")
                    p1Choice = dataSnapshot.value.toString()
                    p1IsReady = true
                    println("player1 is ready: "+p1IsReady)
                    if(p2IsReady){
                        myRef.child(localGameId).child("bothPlayerReady").setValue(true)
                    }
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
        }
    }

    private val p2Listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            myRef.child(localGameId).child("p2Choice").get().addOnSuccessListener { dataSnapshot ->
                println("what p2Choice is: "+dataSnapshot.value.toString())
                println("player2 is ready: "+p2IsReady)
                if(dataSnapshot.value.toString() != "" && !p2IsReady){
                    println("p2listener: p2 is ready")
                    p2Choice = dataSnapshot.value.toString()
                    p2IsReady = true
                    if(p1IsReady){
                        myRef.child(localGameId).child("bothPlayerReady").setValue(true)
                    }
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
        }
    }
    private val bothReadyListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            myRef.child(localGameId).child("bothPlayerReady").get().addOnSuccessListener { dataSnapshot ->
                if(dataSnapshot.value.toString().toBoolean()){
                    println("Both player is ready")
                    bothIsReady = true
                    doMove()

                }else{
                    //RESETS THE "READINESS"
                    bothIsReady = false
                    p1IsReady = false
                    p1IsReady = false
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
        }
    }



    //this retrieves the game data from last fragment
    fun setValues(){ //THIS DONT WORK
        myRef.child(localGameId).get()
            .addOnSuccessListener { dataSnapshot ->
                localGameId = dataSnapshot.child("gameID").value.toString()
                shooterColor = dataSnapshot.child("p1Color").value.toString()
                goalieColor = dataSnapshot.child("p2Color").value.toString()
                localP1Id = dataSnapshot.child("p1Id").value.toString()
                localP2Id = dataSnapshot.child("p2Id").value.toString()
                setPlayerValues()
                var resourceId = resources.getIdentifier("z" + goalieColor+"goalleft", "drawable", "com.hfad.klientutvecklingsprojekt")
                Log.d("goalieAnimation", "z" + goalieColor+"goalleft")
                binding.goalie.setImageResource(resourceId)
                var resourceId2 = resources.getIdentifier("z" + shooterColor +"leftmiss", "drawable", "com.hfad.klientutvecklingsprojekt")
                Log.d("shooterAnimation", "z" + shooterColor +"leftmiss")
                binding.shooterMiss.setImageResource(resourceId2)
            }

        println("setvalues: shootercolor: " + shooterColor + " goalieColor: " + goalieColor)
        println("setvalues: p1Id: " + localP1Id + "p2Id: " + localP2Id)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.finalScorePoint.visibility = View.INVISIBLE
        binding.finishedGameButton.visibility = View.INVISIBLE
        binding.finishedGameScreen.visibility = View.INVISIBLE
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.android_song3_140bpm)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        soccerViewModel = ViewModelProvider(this).get(SoccerViewModel::class.java)


        binding.leftButton.setOnClickListener {
            sendChoiceOnline("left")
            binding.bottomButtons.visibility = View.INVISIBLE
        }
        binding.rightButton.setOnClickListener {
            sendChoiceOnline("right")
            binding.bottomButtons.visibility = View.INVISIBLE
        }
        binding.midButton.setOnClickListener {
            sendChoiceOnline("mid")
            binding.bottomButtons.visibility = View.INVISIBLE
        }



    }

    fun doMove(){
        if (p1Choice == "left"){
            soccerViewModel.leftButtonClick(1)
        }
        if (p1Choice == "mid"){
            soccerViewModel.midButtonClick(1)
        }
        if (p1Choice == "right"){
            soccerViewModel.rightButtonClick(1)
        }
        if (p2Choice == "left"){
            soccerViewModel.leftButtonClick(2)
        }
        if (p2Choice == "mid"){
            soccerViewModel.midButtonClick(2)
        }
        if (p2Choice == "right"){
            soccerViewModel.rightButtonClick(2)
        }
        soccerViewModel.startRound()

        doAnimation(soccerViewModel)

        if(youArePlayerOne || youArePlayerTwo){
            binding.bottomButtons.visibility = View.VISIBLE
            myRef.child(localGameId).child("bothPlayerReady").setValue(false)
            myRef.child(localGameId).child("p1Choice").setValue("")
            myRef.child(localGameId).child("p2Choice").setValue("")
            p1IsReady = false
            p2IsReady = false
        }
        updateScoreBoard()
    }

    fun setPlayerValues(){
        println("shooter: "+ shooterColor)
        println("UNDER SHOULD BE YOUR COLOR")
        println("your id: " + yourId)
        println("goalie: "+ goalieColor)

        //sets player1 and 2 locally  (p1 starts as shooter)
        youArePlayerOne = yourId == localP1Id
        youArePlayerTwo = yourId == localP2Id

        println("you are player one: " + youArePlayerOne)
        println("you are player two: " + youArePlayerTwo)
        if(youArePlayerOne || youArePlayerTwo){
            binding.bottomButtons.visibility = View.VISIBLE
        }else{
            binding.bottomButtons.visibility = View.INVISIBLE
        }

        soccerViewModel.setColors(shooterColor,goalieColor, 1)
    }

    fun updateScoreBoard(){
        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
            binding.scoreBoard.text = "" + soccerViewModel.p1Points + "-" + soccerViewModel.p2Points + " "
        }, 2000)
        handler.postDelayed({
            if (soccerViewModel.p2Points == 3 || soccerViewModel.p1Points == 3){
                binding.finalScorePoint.visibility = View.VISIBLE
                binding.finishedGameButton.visibility = View.VISIBLE
                binding.finishedGameScreen.visibility = View.VISIBLE
                if(soccerViewModel.p2Points == 3){
                    binding.finalScorePoint.text = "" + soccerViewModel.getEnemyColor() + " won!"
                }
                if(soccerViewModel.p1Points == 3){
                    binding.finalScorePoint.text = "" + soccerViewModel.getColor() + " won!"
                }
                binding.finishedGameButton.setOnClickListener {
                    findNavController().popBackStack()
                }
            }

        }, 2000)
    }


    fun checkOtherPlayerReady() {
        soccerModel?.apply {
            bothIsReady = bothPlayerReady.toString().toBoolean()
        }
        //TODO KAN HOPPA ÖVER OM DET ÄR THREADS
    }


    fun sendChoiceOnline(choice: String){
        println("Send choice online")
        if (youArePlayerOne){
                println("Send choice online as p1")
                myRef.child(localGameId).child("p1Choice").setValue(choice)
        }
        if(youArePlayerTwo){
                println("Send choice online as p2")
                myRef.child(localGameId).child("p2Choice").setValue(choice)
        }
    }


    fun doAnimation(soccerViewModel: SoccerViewModel){
        if (bothIsReady){
            var currentImageView: ImageView
            var hitStatus: String
            if (soccerViewModel.shooterHit){
                binding.shooterHit.visibility= View.VISIBLE
                binding.shooterMiss.visibility = View.INVISIBLE
                currentImageView = binding.shooterHit
                hitStatus = "hit"
            }else{
                binding.shooterHit.visibility = View.INVISIBLE
                binding.shooterMiss.visibility = View.VISIBLE
                currentImageView = binding.shooterMiss
                hitStatus = "miss"
            }
            Log.d("destination", "z" + soccerViewModel.shooterColor + soccerViewModel.shooterChoice + hitStatus)

            //set right animationresource here...

            val destination = "z" + soccerViewModel.shooterColor + soccerViewModel.shooterChoice + hitStatus
            val resourceId = resources.getIdentifier(destination, "drawable", "com.hfad.klientutvecklingsprojekt")
            currentImageView.setImageResource(resourceId)
            val shooterAnimation = currentImageView.drawable as AnimationDrawable
            shooterAnimation.start()

            val goalDestination: String

            if (soccerViewModel.goalieChoice == "mid"){
               goalDestination  = "z" + soccerViewModel.goalieColor + "goalleft"
                val resourceId = resources.getIdentifier(goalDestination,"drawable","com.hfad.klientutvecklingsprojekt")
                binding.goalie.setImageResource(resourceId)
                Log.d("Goal if statement", "did click mid")
            }else{
                Log.d("Goal if statement", "did not click mid")
                goalDestination = "z" + soccerViewModel.goalieColor + "goal" + soccerViewModel.goalieChoice
                val resourceId = resources.getIdentifier(goalDestination,"drawable","com.hfad.klientutvecklingsprojekt")
                binding.goalie.setImageResource(resourceId)
                var goalieAnimation  = binding.goalie.drawable as AnimationDrawable
                goalieAnimation.start()
            }


            soccerViewModel.switchType()

            val handler = Handler(Looper.getMainLooper())

            if(youArePlayerOne || youArePlayerTwo){
                binding.bottomButtons.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
package com.hfad.klientutvecklingsprojekt.soccer

import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.net.wifi.p2p.WifiP2pManager.ActionListener
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentSoccerBinding
import kotlinx.coroutines.delay
import java.net.URI
import java.time.Duration
import kotlin.concurrent.thread

class SoccerFragment : Fragment() {

    private lateinit var soccerViewModel: SoccerViewModel
    private var mediaPlayer: MediaPlayer? = null

    private var _binding: FragmentSoccerBinding? = null
    private val binding get() = _binding!!

    //database

    //getting firebase database from the url
    var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")

    var playerColor = "not assigned"
    var opponentFound = false
    var opponentsColor = "not assigned"
    var opponentsUniqueId = "0"
    // values must bne matching or waiting. When a user create a new connection/room
    // and he is waiting for other to join the value will be waiting.
    var status = "matching"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoccerBinding.inflate(inflater,container,false)
        val view = binding.root

        //making uniqueID for player
        var playerUniqueId = System.currentTimeMillis().toString()

        val valueEventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //look for opponent
                if(opponentFound){
                    //look for any opponent in the node
                    if (snapshot.hasChildren()){
                        //checking all connections if other users are also waiting for a user to play the match
                        for (connection: DataSnapshot in snapshot.children){

                            //getting connection unique id
                            val conID = connection.key?.toLong()

                            //2 players are required to play the game.
                            //If playercount is 1 it means other player is waiting for a opponent to play the game.
                            //else if playerCount is 2 it means this connection has completed with 2 players.
                            val playerCount = connection.childrenCount as Int
                        }
                        //if there is no connection available in the firebase then create a new connection
                        // it is like creating a room and waiting for other players to join the room.
                    }else{
                        //generating unique id for the connection
                        var connectionUniqueId = System.currentTimeMillis().toString()
                        // adding first player to the connection and waiting for other to complete the connection
                        val connectionRef = databaseReference.child(connectionUniqueId).child(playerUniqueId).child("player_color")
                        status = "waiting"
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        databaseReference.child("connection").addValueEventListener(valueEventListener)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.finalScorePoint.visibility = View.INVISIBLE
        binding.finishedGameButton.visibility = View.INVISIBLE
        binding.finishedGameScreen.visibility = View.INVISIBLE
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.android_song3_140bpm)
        mediaPlayer?.isLooping = true // Disable built-in looping
        mediaPlayer?.start()

        soccerViewModel = ViewModelProvider(this).get(SoccerViewModel::class.java)
        var goalieColor = "yellow"
        var shootercolor = "green"
        soccerViewModel.setColors(shootercolor,goalieColor, 1)

        var resourceId = resources.getIdentifier("z" + goalieColor+"goalleft", "drawable", "com.hfad.klientutvecklingsprojekt")
        binding.goalie.setImageResource(resourceId)
        var resourceId2 = resources.getIdentifier("z" + shootercolor +"leftmiss", "drawable", "com.hfad.klientutvecklingsprojekt")
        binding.shooterMiss.setImageResource(resourceId2)

        binding.leftButton.setOnClickListener {
            soccerViewModel.leftButtonClick()
            doAnimation(soccerViewModel)
        }
        binding.rightButton.setOnClickListener {
            soccerViewModel.rightButtonClick()
            doAnimation(soccerViewModel)
        }
        binding.midButton.setOnClickListener {
            soccerViewModel.midButtonClick()
            doAnimation(soccerViewModel)
        }

    }


    fun doAnimation(soccerViewModel: SoccerViewModel){
        if (soccerViewModel.animationReady){
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
            Log.d("Goal Destination", goalDestination)
            Log.d("all choices", soccerViewModel.shooterColor+"shooter: " + soccerViewModel.shooterChoice + " " + soccerViewModel.goalieColor+"goalie: " + soccerViewModel.goalieChoice)

            soccerViewModel.switchType()

            if (soccerViewModel.enemyPoints == 3 || soccerViewModel.points == 3){
                binding.finalScorePoint.visibility = View.VISIBLE
                binding.finishedGameButton.visibility = View.VISIBLE
                binding.finishedGameScreen.visibility = View.VISIBLE
                if(soccerViewModel.enemyPoints == 3){
                    binding.finalScorePoint.text = "" + soccerViewModel.getEnemyColor() + " won!"
                }
                if(soccerViewModel.points == 3){
                    binding.finalScorePoint.text = "" + soccerViewModel.getColor() + " won!"
                }
                binding.finishedGameButton.setOnClickListener {
                    findNavController().popBackStack()
                }
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
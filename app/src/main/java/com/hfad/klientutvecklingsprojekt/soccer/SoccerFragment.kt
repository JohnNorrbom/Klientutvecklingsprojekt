package com.hfad.klientutvecklingsprojekt.soccer

import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentSoccerBinding

class SoccerFragment : Fragment() {

    private lateinit var soccerViewModel: SoccerViewModel
    private var mediaPlayer: MediaPlayer? = null
    private var soccerModel : SoccerModel? = null
    private var _binding: FragmentSoccerBinding? = null
    private val binding get() = _binding!!

    private var goalieColor = "yellow"
    private var shooterColor = "red"
    var text: String = ""

    //variable that see if you are p1 or p2
    private var youArePlayerOne: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoccerBinding.inflate(inflater, container, false)
        val view = binding.root
        SoccerData.fetchSoccerModel()
        SoccerData.soccerModel.observe(this) {
            soccerModel = it
            Log.d("observe model", soccerModel.toString())
            setValues()
        }
        return view
    }

    //this retrieves the game data from last fragment
    fun setValues(){
        soccerModel?.apply {
            shooterColor = p1Color.toString()
            goalieColor = p2Color.toString()
            binding.finalScorePoint.text = p1Color
        }
        Log.d("shooter","shooter: "+ shooterColor)
        Log.d("goalie","goalie: "+ goalieColor)


        soccerViewModel.setColors(shooterColor,goalieColor, 1)

        var resourceId = resources.getIdentifier("z" + goalieColor+"goalleft", "drawable", "com.hfad.klientutvecklingsprojekt")
        Log.d("goalieAnimation", "z" + goalieColor+"goalleft")
        binding.goalie.setImageResource(resourceId)
        var resourceId2 = resources.getIdentifier("z" + shooterColor +"leftmiss", "drawable", "com.hfad.klientutvecklingsprojekt")
        Log.d("shooterAnimation", "z" + shooterColor +"leftmiss")
        binding.shooterMiss.setImageResource(resourceId2)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Log.d("after observe model", soccerModel.toString())
        //red yellow
        Log.d("onViewCreated after observe model", shooterColor)
        Log.d("onViewCreated after observe model", goalieColor)


        binding.finalScorePoint.visibility = View.INVISIBLE
        binding.finishedGameButton.visibility = View.INVISIBLE
        binding.finishedGameScreen.visibility = View.INVISIBLE
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.android_song3_140bpm)
        mediaPlayer?.isLooping = true // Disable built-in looping
        mediaPlayer?.start()

        soccerViewModel = ViewModelProvider(this).get(SoccerViewModel::class.java)


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


    fun checkOtherPlayerReady(): Boolean {
        soccerModel?.apply {
            shooterColor = p1Color.toString()
            goalieColor = p2Color.toString()
            binding.finalScorePoint.text = p1Color
        }
        return false
    }


    fun sendChoiceOnline(choice: String){

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
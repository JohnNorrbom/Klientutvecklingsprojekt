package com.hfad.klientutvecklingsprojekt.soccer

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.hfad.klientutvecklingsprojekt.databinding.FragmentSoccerBinding
import kotlinx.coroutines.delay
import java.time.Duration

class SoccerFragment : Fragment() {

    private lateinit var soccerViewModel: SoccerViewModel
    private val nbrOfPresses = 0
    private val playerID = 0

    private var _binding: FragmentSoccerBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoccerBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                val goalieAnimation  = binding.goalie.drawable as AnimationDrawable
                goalieAnimation.start()
            }
            Log.d("Goal Destination", goalDestination)
            Log.d("all choices", soccerViewModel.shooterColor+"shooter: " + soccerViewModel.shooterChoice + " " + soccerViewModel.goalieColor+"goalie: " + soccerViewModel.goalieChoice)



            val shooterAnimation = currentImageView.drawable as AnimationDrawable

            shooterAnimation.start()

            binding.scoreBoard.text = "" + soccerViewModel.points + "-" + soccerViewModel.enemyPoints


            soccerViewModel.switchType()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
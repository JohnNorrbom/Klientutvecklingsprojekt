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

        soccerViewModel.setColors("white","blue", 1)

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
            }else{
                goalDestination = "z" + soccerViewModel.goalieColor + "goal" + soccerViewModel.goalieChoice
                val goalieAnimation  = binding.goalie.drawable as AnimationDrawable
                goalieAnimation.start()
            }
            Log.d("all choices", "")



            val shooterAnimation = currentImageView.drawable as AnimationDrawable

            shooterAnimation.start()

            soccerViewModel.switchType()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
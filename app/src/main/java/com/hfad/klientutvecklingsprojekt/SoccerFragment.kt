package com.hfad.klientutvecklingsprojekt

import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hfad.klientutvecklingsprojekt.databinding.FragmentSoccerBinding

class SoccerFragment : Fragment() {

    private var _binding: FragmentSoccerBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoccerBinding.inflate(inflater,container,false)
        val view = binding.root
        //set all shooters invisible
        binding.whiteMidMiss.visibility = View.INVISIBLE

        //set all goalies invisible
        binding.blueGoalLeft.visibility = View.INVISIBLE
        binding.blueGoalRight.visibility = View.INVISIBLE
        binding.redGoalLeft.visibility = View.INVISIBLE
        binding.redGoalRight.visibility = View.INVISIBLE
        binding.yellowGoalLeft.visibility = View.INVISIBLE
        binding.yellowGoalRight.visibility = View.INVISIBLE
        binding.greenGoalLeft.visibility = View.INVISIBLE
        binding.greenGoalRight.visibility = View.INVISIBLE
        binding.whiteGoalLeft.visibility = View.INVISIBLE
        binding.whiteGoalRight.visibility = View.INVISIBLE



        //create animation for shooter
        //var animationDrawable = binding.whiteShooter.background as AnimationDrawable
        //var ballAnimation = binding.ballRightIn.background as AnimationDrawable
        //var goalieAnimation = binding.blueGoalieLeft.background as AnimationDrawable
        /*
        animationDrawable.apply {
            start()
        }
        ballAnimation.apply {
            start()
        }

        goalieAnimation.apply{
            start()
        }
    */
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
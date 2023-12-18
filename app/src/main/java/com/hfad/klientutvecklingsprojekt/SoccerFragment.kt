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
        //set all shooters invisible except for white
        binding.yellowShooter.visibility = View.INVISIBLE
        binding.redShooter.visibility = View.INVISIBLE
        binding.greenShooter.visibility = View.INVISIBLE
        binding.blueShooter.visibility = View.INVISIBLE

        //set all goalies invisible except for white
        binding.blueGoalieRight.visibility = View.INVISIBLE

        //set all ball animations invisible except right in animation

        binding.ballLeft.visibility = View.INVISIBLE
        binding.ballRight.visibility = View.INVISIBLE
        binding.ballMid.visibility = View.INVISIBLE
        binding.ballLeftIn.visibility = View.INVISIBLE
        binding.ballMidIn.visibility = View.INVISIBLE

        //create animation for shooter
        var animationDrawable = binding.whiteShooter.background as AnimationDrawable
        var ballAnimation = binding.ballRightIn.background as AnimationDrawable
        var goalieAnimation = binding.blueGoalieLeft.background as AnimationDrawable
        animationDrawable.apply {
            start()
        }
        ballAnimation.apply {
            start()
        }

        goalieAnimation.apply{
            start()
        }

        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
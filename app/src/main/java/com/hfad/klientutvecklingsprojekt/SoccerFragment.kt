package com.hfad.klientutvecklingsprojekt

import android.graphics.drawable.AnimationDrawable
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
        //binding shooters
        binding.yellowShooter.visibility = View.INVISIBLE
        binding.redShooter.visibility = View.INVISIBLE
        binding.greenShooter.visibility = View.INVISIBLE
        binding.blueShooter.visibility = View.INVISIBLE
        //create animation for shooter
        var animationDrawable = binding.whiteShooter.background as AnimationDrawable
        animationDrawable.start()
        animationDrawable = binding.ballRight.background as AnimationDrawable
        animationDrawable.start()
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.hfad.klientutvecklingsprojekt

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hfad.klientutvecklingsprojekt.databinding.FragmentSoccerBinding
import com.hfad.klientutvecklingsprojekt.databinding.FragmentWaitingSoccerBinding


class WaitingSoccerFragment : Fragment() {

    private var _binding: FragmentWaitingSoccerBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWaitingSoccerBinding.inflate(inflater, container, false)
        val view = binding.root

        var loadingAnimation = binding.loadingSymbol.drawable as AnimationDrawable
        loadingAnimation.start()


        return view
    }

    fun joinSoccerGame(){

    }

}
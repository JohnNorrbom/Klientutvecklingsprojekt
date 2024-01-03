package com.hfad.klientutvecklingsprojekt.soccer

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentSoccerBinding
import com.hfad.klientutvecklingsprojekt.databinding.FragmentSoccerChooseBinding


class SoccerChooseFragment : Fragment() {

    private var mediaPlayer: MediaPlayer? = null

    private var _binding: FragmentSoccerChooseBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoccerChooseBinding.inflate(inflater,container,false)
        val view = binding.root

        return view
    }


}
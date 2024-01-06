package com.hfad.klientutvecklingsprojekt.leaderboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentLeaderBoardBinding
import com.hfad.klientutvecklingsprojekt.databinding.FragmentLobbyBinding

class leaderBoardFragment : Fragment() {
    private var _binding: FragmentLeaderBoardBinding? = null
    private val binding get()  = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeaderBoardBinding.inflate(inflater,container,false)
        val view = binding.root
        binding.root.rootView.setBackgroundResource(R.drawable.leaderboard)
        return view
    }
}
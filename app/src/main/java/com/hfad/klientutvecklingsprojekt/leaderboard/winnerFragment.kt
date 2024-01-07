package com.hfad.klientutvecklingsprojekt.leaderboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentWinnerBinding

class winnerFragment : Fragment() {
    private var _binding: FragmentWinnerBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWinnerBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.root.rootView.setBackgroundResource(R.drawable.leaderboard)
        // referens till databasen
        val winner = winnerFragmentArgs.fromBundle(requireArguments()).winnerName
        val score = winnerFragmentArgs.fromBundle(requireArguments()).winnerScore
        binding.textView4.text = "The winner is: " + winner + " with a score of: " + score + " points!!!"
        return view
    }
}
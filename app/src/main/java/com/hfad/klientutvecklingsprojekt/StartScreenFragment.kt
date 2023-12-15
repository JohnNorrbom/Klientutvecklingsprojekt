package com.hfad.klientutvecklingsprojekt

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController

class StartScreenFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_start_screen, container, false)
        val startButton = view.findViewById<Button>(R.id.start)

        // Changes view when button is clicked
        startButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_startScreenFragment_to_boardFragment)
        }
        println("startscreen print")
        return view
    }

}
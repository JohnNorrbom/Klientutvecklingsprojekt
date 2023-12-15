package com.hfad.klientutvecklingsprojekt

import android.os.Bundle
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

        // Changes view when button is clicked ****BOARD*****
        val startButton = view.findViewById<Button>(R.id.start_button)
        startButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_startScreenFragment_to_boardFragment)
        }
        //soccer button
        val soccerButton = view.findViewById<Button>(R.id.start_button)
        soccerButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_startScreenFragment_to_soccerFragment)
        }
        return view
    }

}
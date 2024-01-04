package com.hfad.klientutvecklingsprojekt.stensaxpase

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.databinding.FragmentStenSaxPaseBinding
import com.hfad.klientutvecklingsprojekt.gamestart.GameModel
import com.hfad.klientutvecklingsprojekt.gamestart.GameStartFragment
import kotlin.random.Random

class StenSaxPaseFragment : Fragment() {

    private var _binding: FragmentStenSaxPaseBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: StenSaxPaseViewModel
    private var gameModel : GameModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val gID = StenSaxPaseFragmentArgs.fromBundle(requireArguments()).gameID
        //val pID = StenSaxPaseFragmentArgs.fromBundle(requireArguments()).playerID
        val pID = Random.nextInt(1000,9999).toString()

        // establish binding
        _binding = FragmentStenSaxPaseBinding.inflate(inflater, container, false)
        val view = binding.root

        // establish viewModel
        viewModel = ViewModelProvider(this).get(StenSaxPaseViewModel::class.java)

        viewModel.setIdTest(gID, pID)

        // initialize game
        viewModel.initGame()

        // add view code here
        val sten = binding.sten
        val sax = binding.sax
        val pase = binding.pase

        sten.setOnClickListener {
            sax.visibility = View.INVISIBLE
            pase.visibility = View.INVISIBLE
            viewModel.setChoice("sten", pID)
            setActionText("$pID valde: Sten")
        }
        sax.setOnClickListener {
            sten.visibility = View.INVISIBLE
            pase.visibility = View.INVISIBLE
            viewModel.setChoice("sax", pID)
            setActionText("$pID valde: Sax")
        }
        pase.setOnClickListener {
            sten.visibility = View.INVISIBLE
            sax.visibility = View.INVISIBLE
            viewModel.setChoice("pase", pID)
            setActionText("$pID valde: Påse")
        }

        return view
    }

    private fun setActionText(text: String) {
        binding.actionText.text = text
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
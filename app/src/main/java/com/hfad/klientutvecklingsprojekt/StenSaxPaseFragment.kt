package com.hfad.klientutvecklingsprojekt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.hfad.klientutvecklingsprojekt.databinding.FragmentStenSaxPaseBinding

class StenSaxPaseFragment : Fragment() {
    private var _binding: FragmentStenSaxPaseBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: StenSaxPaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // establish binding
        _binding = FragmentStenSaxPaseBinding.inflate(inflater, container, false)
        val view = binding.root

        // establish viewModel
        viewModel = ViewModelProvider(this).get(StenSaxPaseViewModel::class.java)

        // add view code here
        val sten = binding.sten
        val sax = binding.sax
        val pase = binding.pase

        sten.setOnClickListener {
            sax.visibility = View.INVISIBLE
            pase.visibility = View.INVISIBLE
            viewModel.setChoice("sten")
            setActionText("Du valde: Sten")
        }
        sax.setOnClickListener {
            sten.visibility = View.INVISIBLE
            pase.visibility = View.INVISIBLE
            viewModel.setChoice("sax")
            setActionText("Du valde: Sax")
        }
        pase.setOnClickListener {
            sten.visibility = View.INVISIBLE
            sax.visibility = View.INVISIBLE
            viewModel.setChoice("pase")
            setActionText("Du valde: Påse")
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
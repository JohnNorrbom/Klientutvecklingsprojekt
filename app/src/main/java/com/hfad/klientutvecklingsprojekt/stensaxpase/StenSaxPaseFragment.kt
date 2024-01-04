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
import com.hfad.klientutvecklingsprojekt.gamestart.GameStartFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StenSaxPaseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StenSaxPaseFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentStenSaxPaseBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: StenSaxPaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // establish binding
        _binding = FragmentStenSaxPaseBinding.inflate(inflater, container, false)
        val view = binding.root

        // establish viewModel
        viewModel = ViewModelProvider(this).get(StenSaxPaseViewModel::class.java)

        // initialize game
        viewModel.initGame()

        // add view code here
        val sten = binding.sten
        val sax = binding.sax
        val pase = binding.pase

        sten.setOnClickListener {
            sax.visibility = View.INVISIBLE
            pase.visibility = View.INVISIBLE
            //viewModel.setChoice("sten")
            setActionText("Du valde: Sten")
        }
        sax.setOnClickListener {
            sten.visibility = View.INVISIBLE
            pase.visibility = View.INVISIBLE
            //viewModel.setChoice("sax")
            setActionText("Du valde: Sax")
        }
        pase.setOnClickListener {
            sten.visibility = View.INVISIBLE
            sax.visibility = View.INVISIBLE
            //viewModel.setChoice("pase")
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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StenSaxPaseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StenSaxPaseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
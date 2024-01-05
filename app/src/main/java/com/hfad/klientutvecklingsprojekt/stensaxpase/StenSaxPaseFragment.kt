package com.hfad.klientutvecklingsprojekt.stensaxpase

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.hfad.klientutvecklingsprojekt.databinding.FragmentStenSaxPaseBinding
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel

class StenSaxPaseFragment : Fragment() {

    private var _binding: FragmentStenSaxPaseBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: StenSaxPaseViewModel
    private var meModel : MeModel? = null

    var currentGameID : String = ""
    var currentPlayerID : String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // establish binding
        _binding = FragmentStenSaxPaseBinding.inflate(inflater, container, false)
        val view = binding.root

        // establish viewModel
        viewModel = ViewModelProvider(this).get(StenSaxPaseViewModel::class.java)

        MeData.meModel.observe(context as LifecycleOwner) { meModel ->
            meModel?.let {
                this@StenSaxPaseFragment.meModel = it
                setText()
                viewModel.setID(currentGameID, currentPlayerID)
            } ?: run {
                // Handle the case when meModel is null
                Log.e("LobbyFragment", "meModel is null")
            }
        }

        // initialize game
        viewModel.initGame()
        /*
        // For testing when mini-game is launched from start screen button
        if(currentGameID == "") {
            currentGameID = "8718"
            currentPlayerID = "1199"
        }
         */

        // add view code here
        val sten = binding.sten
        val sax = binding.sax
        val pase = binding.pase

        sten.setOnClickListener {
            //sax.visibility = View.INVISIBLE
            //pase.visibility = View.INVISIBLE
            viewModel.setChoice("sten", currentPlayerID)
            setActionText("$currentPlayerID valde: Sten")
        }
        sax.setOnClickListener {
            //sten.visibility = View.INVISIBLE
            //pase.visibility = View.INVISIBLE
            viewModel.setChoice("sax", currentPlayerID)
            setActionText("$currentPlayerID valde: Sax")
        }
        pase.setOnClickListener {
            //sten.visibility = View.INVISIBLE
            //sax.visibility = View.INVISIBLE
            viewModel.setChoice("pase", currentPlayerID)
            setActionText("$currentPlayerID valde: Påse")
        }

        return view
    }

    fun setText() {
        //den här
        currentGameID = meModel?.gameID ?: ""
        currentPlayerID = meModel?.playerID ?: ""
        Log.d("StenSaxPaseFragment", "playerID: ${currentPlayerID} GameID: ${currentGameID}")
    }

    private fun setActionText(text: String) {
        binding.actionText.text = text
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.hfad.klientutvecklingsprojekt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.hfad.klientutvecklingsprojekt.databinding.FragmentDiceBinding
import com.hfad.klientutvecklingsprojekt.databinding.FragmentPlayerInfoBinding
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DiceFragment : Fragment() {
    private var _binding: FragmentDiceBinding? = null
    private val binding get()  = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiceBinding.inflate(inflater,container,false)
        val view = binding.root
        // Inflate the layout for this fragment
        return view
    }
    fun rollDice(imageView: ImageView) {
        val randomNumber = Random.nextInt(6) + 1

        val drawableResource = resources.getIdentifier("dice$randomNumber", "drawable", packageName)
        imageView.setImageResource(drawableResource)
    }

}
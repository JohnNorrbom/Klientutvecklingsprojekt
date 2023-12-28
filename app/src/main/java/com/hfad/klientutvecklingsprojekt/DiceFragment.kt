package com.hfad.klientutvecklingsprojekt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import com.hfad.klientutvecklingsprojekt.databinding.FragmentDiceBinding
import kotlin.random.Random

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DiceFragment : Fragment() {
    private var _binding: FragmentDiceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUI() {
        val rollButton: ImageButton = binding.diceImageButton

        rollButton.setOnClickListener {
            rollDice(rollButton)
        }
    }

    private fun rollDice(imageView: ImageView) {
        val randomNumber = Random.nextInt(6) + 1
        val packageName = requireContext().packageName
        val drawableResource = resources.getIdentifier("dice$randomNumber", "drawable", packageName)

        imageView.setImageResource(drawableResource)
    }
}

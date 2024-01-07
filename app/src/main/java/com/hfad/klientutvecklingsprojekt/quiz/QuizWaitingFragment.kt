package com.hfad.klientutvecklingsprojekt.gavleroulette

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentGavleRouletteWaitBinding
import com.hfad.klientutvecklingsprojekt.databinding.FragmentQuizWaitingBinding
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel

class QuizWaitingFragment : Fragment() {
    private var _binding: FragmentQuizWaitingBinding? = null
    private val binding get() = _binding!!
    private var meModel: MeModel? = null
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Quiz")
    var localPlayerID = ""
    var localGameID = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizWaitingBinding.inflate(inflater, container, false)
        val view = binding.root

        MeData.meModel.observe(this) { meModel ->
            meModel?.let {
                this@QuizWaitingFragment.meModel = it
                setText()

            } ?: run {
                // Handle the case when meModel is null
                Log.e("LobbyFragment", "meModel is null")
            }
        }

        binding.startGame.setOnClickListener{
            startGame()
        }

        return view
    }

    fun startGame(){
        myRef.child(localGameID).get().addOnSuccessListener {

                view?.findNavController()?.navigate(R.id.action_quizWaitingFragment_to_quizFragment)

        }
    }

    fun setText() {
        localGameID = meModel?.gameID ?: ""
        localPlayerID = meModel?.playerID ?: ""
        Log.d("meModel", "player $localGameID Game $localPlayerID")
    }
}

package com.hfad.klientutvecklingsprojekt.quiz

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
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


/** @author Pontus Lindholm : lindholmpontus@outlook.com
 * Denna klass är väldigt liten och är som en kort "waiting screen" innan man slussas in till quizet.
 * Tanken är att låta användaren förbereda sig, säkerställa att ett seed genererats (som används för att slumpa frågor)
 * samt att det ska se coolt ut.
 *
 */

class QuizWaitingFragment : Fragment() {
    private var _binding: FragmentQuizWaitingBinding? = null
    private val binding get() = _binding!!
    private var meModel: MeModel? = null
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Quiz")
    var localPlayerID = ""
    var localGameID = ""
    private val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizWaitingBinding.inflate(inflater, container, false)
        val view = binding.root
        //Laddar cool animation.
        var loadingAnimation = binding.loadingSymbol.drawable as AnimationDrawable
        loadingAnimation.start()

        MeData.meModel.observe(this) { meModel ->
            meModel?.let {
                this@QuizWaitingFragment.meModel = it
                setText()

            } ?: run {
                // Handle the case when meModel is null
                Log.e("LobbyFragment", "meModel is null")
            }
        }
        //Väntar fem sekunder innan quizet startar
        handler.postDelayed({
            startGame()
        }, 5000)
        return view
    }

    //Startar quizet
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

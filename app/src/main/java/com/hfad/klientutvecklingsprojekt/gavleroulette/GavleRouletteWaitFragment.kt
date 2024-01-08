package com.hfad.klientutvecklingsprojekt.gavleroulette

import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
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
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel

class GavleRouletteWaitFragment : Fragment() {
    private var _binding: FragmentGavleRouletteWaitBinding? = null
    private val binding get() = _binding!!
    private var meModel: MeModel? = null
    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Roulette")
    var localPlayerID = ""
    var localGameID = ""
    private val handler = Handler()
    // BG MUSIC
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGavleRouletteWaitBinding.inflate(inflater, container, false)
        val view = binding.root
        mediaPlayer = MediaPlayer.create(
            requireContext(), R.raw.android_song7_130bpm
        )
        mediaPlayer?.isLooping = true // Disable built-in looping
        mediaPlayer?.start()

        var loadingAnimation = binding.loadingSymbol.drawable as AnimationDrawable
        loadingAnimation.start()

        MeData.meModel.observe(this) { meModel ->
            meModel?.let {
                this@GavleRouletteWaitFragment.meModel = it
                setText()

            } ?: run {
                // Handle the case when meModel is null
                Log.e("LobbyFragment", "meModel is null")
            }
        }

        handler.postDelayed({
            startGame()
        }, 5000)
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun startGame(){
        myRef.child(localGameID).get().addOnSuccessListener {
            val model = it.getValue(RouletteModel::class.java)
            if (model != null){
                try {
                    RouletteData.saveGameModel(model,localGameID)
                    view?.findNavController()?.navigate(R.id.action_gavleRouletteWaitFragment_to_gavleRouletteFragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun setText() {
        localGameID = meModel?.gameID ?: ""
        localPlayerID = meModel?.playerID ?: ""
        Log.d("meModel", "player $localGameID Game $localPlayerID")
    }
}

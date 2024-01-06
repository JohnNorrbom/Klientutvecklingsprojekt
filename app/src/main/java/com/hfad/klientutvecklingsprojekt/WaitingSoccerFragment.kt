package com.hfad.klientutvecklingsprojekt

import android.graphics.drawable.AnimationDrawable
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
import com.hfad.klientutvecklingsprojekt.databinding.FragmentSoccerBinding
import com.hfad.klientutvecklingsprojekt.databinding.FragmentWaitingSoccerBinding
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel


class WaitingSoccerFragment : Fragment() {

    //TODO fixa listeners som lyssnar på SOCCERGAMES OCH VEM SOM SKA KÖRA

    private var _binding: FragmentWaitingSoccerBinding? = null
    private val binding get() = _binding!!

    //  DATABASE
    private val database =
        Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Soccer")

    //meModel
    private var meModel : MeModel?= null

    //gameId
    private var localGameId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWaitingSoccerBinding.inflate(inflater, container, false)
        val view = binding.root

        var loadingAnimation = binding.loadingSymbol.drawable as AnimationDrawable
        loadingAnimation.start()

        MeData.meModel.observe(this) { meModel ->
            meModel?.let {
                this@WaitingSoccerFragment.meModel = it
                getGameId()
                joinSoccerGame()

            } ?: run {
                Log.e("LobbyFragment", "meModel is null")
            }
        }

        myRef.addValueEventListener(soccerListener)



        return view
    }

    fun getGameId(){
        meModel?.apply {
            localGameId = gameID.toString()
        }
    }

    private val soccerListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            myRef.child(localGameId).get().addOnSuccessListener { dataSnapshot ->
                if(dataSnapshot.exists()){
                    println("dataSnapshot exists...")
                    view?.findNavController()?.navigate(R.id.action_waitingSoccerFragment_to_soccerFragment)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    fun joinSoccerGame(){



    }

}
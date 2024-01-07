package com.hfad.klientutvecklingsprojekt.stensaxpase

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
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentStenSaxPaseWaitBinding
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel

class StenSaxPaseWaitFragment : Fragment() {

    private var _binding: FragmentStenSaxPaseWaitBinding? = null
    private val binding get() = _binding!!
    private var meModel : MeModel? = null

    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val gameIDRef = database.getReference("Sten Sax Pase")

    private var localGameId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // establish binding
        _binding = FragmentStenSaxPaseWaitBinding.inflate(inflater, container, false)
        val view = binding.root

        var loadingAnimation = binding.loadingSymbol.drawable as AnimationDrawable
        loadingAnimation.start()

        MeData.meModel.observe(this) { meModel ->
            meModel?.let {
                this@StenSaxPaseWaitFragment.meModel = it
                getGameId()

            } ?: run {
                Log.e("StenSaxPaseWait", "meModel is null")
            }
        }

        gameIDRef.addValueEventListener(gameStartListener)

        return view
    }

    fun getGameId(){
        meModel?.apply {
            localGameId = gameID.toString()
            println("wait fragment loads gameID: $localGameId")
        }
    }

    private val gameStartListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            gameIDRef.child(localGameId).get().addOnSuccessListener { dataSnapshot ->
                if(dataSnapshot.exists()){
                    println("datasnapshot returned: $dataSnapshot")
                    println("entering game with id: $localGameId")
                    // här behöver vi skicka med 'playerID' och 'opponentID' som safearg argument
                    // gör en call till databasen, och hämta dessa värden, när hämtning är klar..
                    gameIDRef.child(localGameId).get().addOnSuccessListener {
                        // skicka dem och låt användarna navigera vidare till sten sax pase mini-game
                        var playerID = ""
                        var opponentID = ""

                        for(elem in it.children) {
                            println(elem)
                            playerID = elem.child("playerID").value.toString()
                            opponentID = elem.child("opponentID").value.toString()
                        }

                        println("wait fragment got playerID: $playerID and opponentID: $opponentID")
                        val action = StenSaxPaseWaitFragmentDirections.actionStenSaxPaseWaitFragmentToStensaxpaseFragment(playerID,opponentID)
                        view?.findNavController()?.navigate(action)
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }
    /*
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

     */
    override fun onStop() {
        super.onStop()
        gameIDRef.removeEventListener(gameStartListener)
        println("WAITFRAG: JAG HAR PAUSAT")
    }
}

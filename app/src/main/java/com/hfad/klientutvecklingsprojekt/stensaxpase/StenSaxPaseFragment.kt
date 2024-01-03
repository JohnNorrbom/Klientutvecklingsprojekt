package com.hfad.klientutvecklingsprojekt.stensaxpase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.databinding.FragmentStenSaxPaseBinding

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
    lateinit var viewModel: StenSaxPaseViewModel

    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("Sten Sax Pase")

    var stenSaxPaseModel : StenSaxPaseModel? = null

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


        stenSaxPaseModel?.apply {
            // lägg till de variabler som ska in i firebase
        }

        fun saveToDatabase(gameID:String) {

            val players : MutableMap<String,MutableMap<String,String>> = mutableMapOf()

            players.put("playerIDplaceholder1", mutableMapOf(
                "nickname" to "nicknamePlaceholder",
                "color" to "colorPlaceholder",
                "choice" to "choicePlacerholder"
            ))

            players.put("playerIDplaceholder2", mutableMapOf(
                "nickname" to "nicknamePlaceholder",
                "color" to "colorPlaceholder",
                "choice" to "choicePlacerholder"
            ))

            stenSaxPaseModel = StenSaxPaseModel(gameID, false, players)
            var lobbyID:String = gameID
            myRef.child(lobbyID).setValue(stenSaxPaseModel)
        }

        // change gameID to the gameID which the overall game uses, possibly by safeARGS
        saveToDatabase("0")

        fun loadFromDatabase() {
            var spaceParty = database.getReference("Space Party")
        }

        // establish binding
        _binding = FragmentStenSaxPaseBinding.inflate(inflater, container, false)
        val view = binding.root

        // establish viewModel
        viewModel = ViewModelProvider(this).get(StenSaxPaseViewModel::class.java)

        // load in players
        viewModel.initPlayers()

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
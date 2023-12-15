package com.hfad.klientutvecklingsprojekt.firebase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentFirebaseBinding

/**
 * A simple [Fragment] subclass.
 * Use the [FirebaseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirebaseFragment : Fragment() {
    private var _binding : FragmentFirebaseBinding? = null
    private val binding get() = _binding!!

    val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    //
    //  !!!!    BEHÖVER BYTA UT MESSAGES VVVVVVVVVV !!!!!!!! tror det är koppling till klassen
    //
    val myRef = database.getReference("messages")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFirebaseBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
}
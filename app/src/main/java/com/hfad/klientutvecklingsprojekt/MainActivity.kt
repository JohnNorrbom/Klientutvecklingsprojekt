package com.hfad.klientutvecklingsprojekt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hfad.klientutvecklingsprojekt.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // Ensure your binding variable is declared

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create an instance of your SoccerFragment
        val soccerFragment = SoccerFragment()

        // Get FragmentManager and start a transaction
        supportFragmentManager.beginTransaction().apply {
            // Replace the container view with your SoccerFragment instance
            replace(R.id.containerFragment, soccerFragment)
            addToBackStack(null) // If you want to add the transaction to the back stack
            commit() // Commit the transaction
        }
    }
}
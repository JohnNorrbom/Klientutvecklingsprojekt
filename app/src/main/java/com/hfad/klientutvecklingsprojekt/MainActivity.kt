package com.hfad.klientutvecklingsprojekt

import android.R
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
//import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.hfad.klientutvecklingsprojekt.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // Ensure your binding variable is declared
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  Makes the status and navigation bar of the phone transparent
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create an instance of your SoccerFragment
        //val soccerFragment = SoccerFragment()

        // Get FragmentManager and start a transaction
        //supportFragmentManager.beginTransaction().apply {
            // Replace the container view with your SoccerFragment instance
            //replace(R.id.nav_host_fragment, soccerFragment)
            //addToBackStack(null) // If you want to add the transaction to the back stack
            //commit() // Commit the transaction
        //}
    }
    //Hejsan ferdinand is here
}
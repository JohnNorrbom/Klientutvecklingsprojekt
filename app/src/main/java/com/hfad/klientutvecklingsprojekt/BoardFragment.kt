package com.hfad.klientutvecklingsprojekt

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class BoardFragment : Fragment() {
    private var pairArray = arrayOf(1 to "null")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("board fragment create print")
        pairArray = arrayOf(
                1 to "tile_go.png", 2 to "tile_plus_1.png", 3 to "tile_plus_2.png", 4 to "tile_plus_3.png", 5 to "tile_minus_5.png",
                6 to "tile_plus_1.png", 7 to "tile_plus_2.png", 8 to "tile_plus_3.png", 9 to "tile_minus_5.png", 10 to "tile_plus_1.png",
                11 to "tile_plus_2.png", 12 to "tile_plus_1.png", 13 to "tile_plus_2.png", 14 to "tile_plus_3.png", 15 to "tile_minus_5.png",
                16 to "tile_plus_2.png", 17 to "tile_plus_1.png", 18 to "tile_plus_2.png", 19 to "tile_plus_3.png", 20 to "tile_minus_5.png")
        println("pararray to map" + pairArray.toMap().toString())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        println("boardfragment view print")
        return inflater.inflate(R.layout.fragment_board, container, false)
    }
}
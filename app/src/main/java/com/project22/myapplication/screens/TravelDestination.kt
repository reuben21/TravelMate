package com.project22.myapplication.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.project22.myapplication.R

class TravelDestination : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_destination)

        val userWallet = intent?.getStringExtra("placeName").toString()
        Log.d("TravelDestination",userWallet)
    }


}
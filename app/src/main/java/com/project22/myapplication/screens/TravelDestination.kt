package com.project22.myapplication.screens

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.project22.myapplication.R
import kotlinx.android.synthetic.main.activity_travel_destination.*

class TravelDestination : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_destination)

        try {
            // TODO :- To Hide the Toolbar which Comes by Default
            this.supportActionBar!!.hide()
        }
        catch (e: NullPointerException) {
        }

        // TODO :- To Make Fragment Visible in Full Screen
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val placeName = intent?.getStringExtra("placeName").toString()
        val travellersHolder = intent?.getStringExtra("travellersHolder").toString()
        val placeImageUrl = intent?.getStringExtra("placeImageUrl").toString()

        toolbar_back_to_travel_destination_single_screen.setNavigationOnClickListener { onBackPressed() }
0
        toolbar_back_to_travel_destination_single_screen.title = placeName
        card_travellers.text = "Travelling: " + travellersHolder
        this.applicationContext?.let {
            Glide.with(it)
                .load(placeImageUrl)
                .placeholder(R.drawable.travel)
                .into( singleScreenImage)
        }

    }


}
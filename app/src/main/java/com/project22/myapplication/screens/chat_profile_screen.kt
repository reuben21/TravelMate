package com.project22.myapplication.screens

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.project22.myapplication.R
import kotlinx.android.synthetic.main.activity_chat_profile_screen.*
import kotlinx.android.synthetic.main.activity_travel_destination.*

class chat_profile_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_profile_screen)
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
        val chatId = intent?.getStringExtra("chatId").toString()
        val chatName = intent?.getStringExtra("chatName").toString()
        val chatImageHolder = intent?.getStringExtra("chatImageHolder").toString()
        toolbar_back_chat_screen.setNavigationOnClickListener { onBackPressed() }
        toolbar_back_chat_screen.title = chatName

        this.applicationContext?.let {
            Glide.with(it)
                .load(chatImageHolder)
                .placeholder(R.drawable.travel)
                .into(chatProfileScreenImage)
        }
    }
}
package com.project22.myapplication.screens

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project22.myapplication.MainActivity
import com.project22.myapplication.R
import kotlinx.android.synthetic.main.activity_travel_destination.*

class TravelDestination : AppCompatActivity() {


    val db = Firebase.firestore
    var auth: FirebaseAuth = Firebase.auth

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
        val placeImageUrl = intent?.getStringExtra("destinationImageUrl").toString()
        val destinationName = intent?.getStringExtra("destinationName").toString()
        val originName = intent?.getStringExtra("originName").toString()
        val ticketImageUrl = intent?.getStringExtra("ticketImageUrl").toString()
        val creatorName = intent?.getStringExtra("creatorName").toString()
        val creatorId = intent?.getStringExtra("creatorId").toString()
        val chatId = intent?.getStringExtra("chatId").toString()
        val chatName = intent?.getStringExtra("chatName").toString()

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
        fromOriginTextViewFill.text = originName
        fromDestinationTextViewFill.text = destinationName
        singleScreenUserName.text = creatorName

        val chatDetails = hashMapOf(
            "chatName" to chatName,
            "chatId" to chatId,
            "chatImageHolder" to placeImageUrl
        )

        if(auth.currentUser?.uid == creatorId) {
            buttonJoinChatGroup.text = "Creator"
            buttonJoinChatGroup.setOnClickListener {
                Toast.makeText(
                    baseContext,
                    "You Created This Travel",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            // TODO :  ADD A DIALOG BOX WHERE IT WILL ASK THE USER TO JOIN THE GROUP WITH OPTION YES AND NO,
            // TODO :  IF THE USER SELECTS YES, THEN JOIN THE GROUP CHAT, IF USER SELECTS NO
            // TODO :  FOR PRIVATE CONTACT THEN SHOW ANOTHER DIALOG WITH USER DETAILS.

            buttonJoinChatGroup.setOnClickListener {


                //YES
                db.collection("users").document(auth.currentUser?.uid.toString())
                    .collection("chats")
                    .document(chatId).set(
                        chatDetails

                    ).addOnSuccessListener {


                    }.addOnFailureListener {

                    }
                //END YES
            }
        }



    }


}
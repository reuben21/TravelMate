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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project22.myapplication.MainActivity
import com.project22.myapplication.R
import com.project22.myapplication.database.DatabaseHelper
import kotlinx.android.synthetic.main.activity_travel_destination.*
import java.util.*

class TravelDestination : AppCompatActivity() {


    val db = Firebase.firestore
    var auth: FirebaseAuth = Firebase.auth

    var userIdDB: String? = null
    var emailDB: String? = null
    var firstNameDB: String? = null
    var lastNameDB: String? = null
    var profileImageUrlDB: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_destination)
        val dbHelper = DatabaseHelper(this)
        val res = dbHelper.allData
        while (res.moveToNext()) {
            userIdDB  = res.getString(0)
            emailDB = res.getString(1)
            firstNameDB = res.getString(2)
            lastNameDB = res.getString(3)
            profileImageUrlDB = res.getString(4)

        }
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

        val originLatitude = intent?.getStringExtra("originLatitude").toString().toDouble()
        val originLongitude = intent?.getStringExtra("originLongitude").toString().toDouble()
        val destinationLatitude = intent?.getStringExtra("destinationLatitude").toString().toDouble()
        val destinationLongitude = intent?.getStringExtra("destinationLongitude").toString().toDouble()

        toolbar_back_to_travel_destination_single_screen.setNavigationOnClickListener { onBackPressed() }
0
        toolbar_back_to_travel_destination_single_screen.title = placeName
        card_travellers.text = "Travelling: " + travellersHolder
        this.applicationContext?.let {
            Glide.with(it)
                .load(placeImageUrl)
                .placeholder(R.drawable.travel)
                .into(singleScreenImage)
        }
        this.applicationContext?.let {
            Glide.with(it)
                .load(placeImageUrl)
                .placeholder(R.drawable.travel)
                .into(displayProfileImage)
        }
        this.applicationContext?.let {
            Glide.with(it)
                .load(ticketImageUrl)
                .placeholder(R.drawable.travel)
                .into(travelImageProof)
        }
        fromOriginTextViewFill.text = originName
        fromDestinationTextViewFill.text = destinationName
        singleScreenUserName.text = creatorName

        val chatDetails = hashMapOf(
            "chatName" to chatName,
            "chatId" to chatId,
            "chatImageHolder" to placeImageUrl
        )
        val ref = db.collection("users").document(chatId).collection("location").document()
        val ref2 = db.collection("users").document(chatId).collection("chats").document()
        val chatDetailsCreator = hashMapOf(
            "chatName" to creatorName,
            "chatId" to ref2.id,
            "chatImageHolder" to placeImageUrl
        )
        val chatDetailsUser = hashMapOf(
            "chatName" to "${firstNameDB} ${lastNameDB}",
            "chatId" to ref2.id,
            "chatImageHolder" to placeImageUrl
        )
        val location = hashMapOf(
            "originLatitude" to originLatitude,
            "originLongitude" to originLongitude,
            "destinationLatitude" to destinationLatitude,
            "destinationLongitude" to destinationLongitude,
            "id" to ref.id,
            "chatId" to chatId,
            "chatImageHolder" to placeImageUrl
        )
        val textMessage = hashMapOf(
            "id" to ref2.id,
            "message" to " ${firstNameDB} ${lastNameDB} created this Personal Chat ",
            "createdAt" to Timestamp(Date()),
            "senderId" to auth.currentUser?.uid,
            "senderName" to "${firstNameDB} ${lastNameDB}",
            "messageType" to "3",

            )
        val textMessage2 = hashMapOf(
            "id" to ref2.id,
            "message" to " ${firstNameDB} ${lastNameDB} joined this Group Chat ",
            "createdAt" to Timestamp(Date()),
            "senderId" to auth.currentUser?.uid,
            "senderName" to "${firstNameDB} ${lastNameDB}",
            "messageType" to "3",

            )
        val travellerCount = hashMapOf(
            "travellers" to travellersHolder.toInt() + 1
        )
        val organiser = hashMapOf(
            "userId" to auth.currentUser?.uid.toString(),
            "user" to firstNameDB +" "+lastNameDB,
            "profileImageUrl" to "${profileImageUrlDB}",
        )
        val organiser2 = hashMapOf(
            "userId" to creatorId,
            "user" to creatorName,
            "profileImageUrl" to "${placeImageUrl}",
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

                MaterialAlertDialogBuilder(this,R.style.MaterialAlertDialog_App)
                    .setTitle("Group Chat")
                    .setMessage("Select Yes to Join the Group Chat OR Select No for personal Chat")
                    .setNeutralButton("Cancel") { dialog, which ->
                        // Respond to neutral button press
                    }
                    .setNegativeButton("No") { dialog, which ->

                        db.collection("destination").document(chatId).update(travellerCount as Map<String, Any>)
                            .addOnSuccessListener {
                                db.collection("chats").document(ref2.id).collection("users").document()
                                    .set(organiser)
                                    .addOnSuccessListener {
                                        db.collection("chats").document(ref2.id).collection("users").document()
                                            .set(organiser2)
                                            .addOnSuccessListener {
                                                db.collection("chats").document(ref2.id)
                                                    .collection("messages")
                                                    .document(ref2.id).set(textMessage)
                                                    .addOnSuccessListener {
                                                        db.collection("users").document(auth.currentUser?.uid.toString())
                                                            .collection("chats")
                                                            .document(ref2.id).set(
                                                                chatDetailsCreator

                                                            ).addOnSuccessListener {
                                                                db.collection("users").document(creatorId)
                                                                    .collection("chats")
                                                                    .document(ref2.id).set(
                                                                        chatDetailsUser

                                                                    ).addOnSuccessListener {

                                                                        db.collection("users").document(auth.currentUser?.uid.toString())
                                                                            .collection("location")
                                                                            .document(ref.id).set(location).addOnSuccessListener {

                                                                                Toast.makeText(
                                                                                    baseContext,
                                                                                    "You have been added to the Personal Chat",
                                                                                    Toast.LENGTH_LONG
                                                                                ).show()

                                                                            }.addOnFailureListener {

                                                                            }

                                                                    }
                                                            }
                                                    }
                                            }

                                    }
                            }


                    }
                    .setPositiveButton("Yes") { dialog, which ->
                        // Respond to positive button press
                        //YES
                        db.collection("destination").document(chatId).update(travellerCount as Map<String, Any>)
                            .addOnSuccessListener {
                                db.collection("chats").document(chatId).collection("users").document(ref.id)
                                    .set(organiser).addOnSuccessListener {
                                        db.collection("chats").document(chatId)
                                            .collection("messages")
                                            .document().set(textMessage2)
                                            .addOnSuccessListener {
                                                db.collection("users").document(auth.currentUser?.uid.toString())
                                                    .collection("chats")
                                                    .document(chatId).set(
                                                        chatDetails

                                                    ).addOnSuccessListener {


                                                        db.collection("users").document(auth.currentUser?.uid.toString())
                                                            .collection("location")
                                                            .document(ref.id).set(location).addOnSuccessListener {

                                                                Toast.makeText(
                                                                    baseContext,
                                                                    "You have been added to the Group Chat",
                                                                    Toast.LENGTH_LONG
                                                                ).show()

                                                            }.addOnFailureListener {

                                                            }

                                                    }.addOnFailureListener {

                                                    }
                                            }
                                    }
                            }


                        //END YES
                    }
                    .show()


            }
        }



    }


}
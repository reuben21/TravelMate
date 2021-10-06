package com.project22.myapplication.screens


import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.WindowInsets
import android.view.WindowManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import com.project22.myapplication.R
import com.project22.myapplication.adapters.UserViewHolder
import com.project22.myapplication.model.User
import com.project22.myapplication.model.UserProfile
import kotlinx.android.synthetic.main.activity_chat_profile_screen.*
import kotlinx.android.synthetic.main.activity_travel_destination.*
import kotlinx.android.synthetic.main.fragment_chats.*

class chat_profile_screen : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<UserProfile>
    private lateinit var userAdapter: UserViewHolder
    private lateinit var db: FirebaseFirestore


    var chatIdG:String = ""



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
        chatIdG = chatId
        val chatName = intent?.getStringExtra("chatName").toString()
        val chatImageHolder = intent?.getStringExtra("chatImageHolder").toString()
        Log.d("CHAT ID ",chatId)
        toolbar_back_chat_screen.setNavigationOnClickListener { onBackPressed() }
        toolbar_back_chat_screen.title = chatName

        this.applicationContext?.let {
            Glide.with(it)
                .load(chatImageHolder)
                .placeholder(R.drawable.travel)
                .into(chatProfileScreenImage)
        }



        recyclerView = userInChatListProfile
        recyclerView.layoutManager = LinearLayoutManager(this.applicationContext)
        recyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf()

        userAdapter = UserViewHolder(userArrayList)

        recyclerView.adapter = userAdapter

        EventChangeListener()



    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("chats")
            .document(chatIdG)
            .collection("users").addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.d("Error ", error.message.toString())
                        return
                    }

                    for (document: DocumentChange in value?.documentChanges!!){
                        if(document.type == DocumentChange.Type.ADDED) {
                            userArrayList.add(document.document.toObject(UserProfile::class.java))
                        }

                    }
                    userAdapter.notifyDataSetChanged()

                }

            })
    }



}


package com.project22.myapplication.frangments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.project22.myapplication.MainActivity
import com.project22.myapplication.R
import com.project22.myapplication.adapters.ChatViewHolder
import com.project22.myapplication.model.Chat
import kotlinx.android.synthetic.main.activity_chat_screen.*

import kotlinx.android.synthetic.main.fragment_chats.*
import kotlinx.android.synthetic.main.fragment_home.*

class ChatFragment : Fragment() {


    var auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private val TAG = "CHAT FRAGMENT"
    private var adapter: FirestoreRecyclerAdapter<Chat, ChatViewHolder>? = null
    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    private var chatList = mutableListOf<Chat>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)






        firestoreDB = FirebaseFirestore.getInstance()


        val mLayoutManager = LinearLayoutManager(activity?.applicationContext)
        chatListRecyclerView.layoutManager = mLayoutManager
        chatListRecyclerView.itemAnimator = DefaultItemAnimator()

        loadDestinationList()

        firestoreListener = firestoreDB!!.collection("users/"+ auth.currentUser?.uid.toString()+"/chats")
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed!", e)
                    return@EventListener
                }

                chatList = mutableListOf<Chat>()

                if (documentSnapshots != null) {
                    for (doc in documentSnapshots) {
                        val chat = doc.toObject(Chat::class.java)
                        chat.id = doc.id
                        chatList.add(chat)
                    }
                }

                adapter!!.notifyDataSetChanged()
                chatListRecyclerView.adapter = adapter
            })

    }

    private fun loadDestinationList() {

        val query = firestoreDB!!.collection("users/"+ auth.currentUser?.uid.toString()+"/chats")

        val response = FirestoreRecyclerOptions.Builder<Chat>()
            .setQuery(query, Chat::class.java)
            .build()

        adapter = object : FirestoreRecyclerAdapter<Chat, ChatViewHolder>(response) {
            override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: Chat) {
                val dest = chatList[position]

                holder.singleChatNameHolder.text = dest.chatName.toString()

                activity?.applicationContext?.let {
                    Glide.with(it)
                        .load(dest.chatImageHolder)
                        .placeholder(R.drawable.travel)
                        .into(holder.singleChatImageHolder)
                }

                holder.cardOfChatView.setOnClickListener {
                    Log.d("TEXT",dest.chatName.toString())


                }
//
//                holder.delete.setOnClickListener { deleteNote(note.id!!) }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.single_chat_item, parent, false)

                return ChatViewHolder(view)
            }

            override fun onError(e: FirebaseFirestoreException) {
                e!!.message?.let { Log.e("error", it) }
            }
        }

        adapter!!.notifyDataSetChanged()
        chatListRecyclerView.adapter = adapter
    }
    public override fun onStart() {
        super.onStart()

        adapter!!.startListening()
    }

}
package com.project22.myapplication.screens


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project22.myapplication.R
import com.project22.myapplication.adapters.TextMessageReceiverHolder
import com.project22.myapplication.adapters.TextMessageSenderViewHolder
import com.project22.myapplication.adapters.TextMessageViewHolder
import com.project22.myapplication.model.TextMessage
import kotlinx.android.synthetic.main.activity_chat_screen.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.File
import java.util.*
import java.text.SimpleDateFormat
import javax.security.auth.callback.PasswordCallback
import com.google.firebase.firestore.DocumentReference
import com.project22.myapplication.MainActivity
import com.project22.myapplication.adapters.TextMessageCenterViewHolder
import kotlinx.android.synthetic.main.fragment_settings.*


class ChatScreen : AppCompatActivity() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
    }


    var auth: FirebaseAuth = Firebase.auth
    val db = Firebase.firestore
    private val TAG = "MainActivity"
    private var adapter: FirestoreRecyclerAdapter<TextMessage, RecyclerView.ViewHolder>? = null
    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    private var messageList = mutableListOf<TextMessage>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_screen)
        val chatId = intent?.getStringExtra("chatId").toString()
//        Log.d("CHAT ID", chatId)
//        val docRef = db.collection("chats").document(chatId)
//        docRef.get().addOnSuccessListener { document ->
//            if (document != null) {
//
//            } else {
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//            }
//        }
//            .addOnFailureListener { exception ->
//                Log.d("TAG", "get failed with ", exception)
//            }
//

        try {
            // TODO :- To Hide the Toolbar which Comes by Default
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) { }

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

        sendCameraMessageButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }



        sendMessageButton.setOnClickListener {
            Log.d("TEST", "ONLCLICK")
            val message = textInputMessage.text.toString()
            if (message.isNotEmpty()) {
                val ref = db.collection("chats").document(chatId).collection("messages").document()

//            val textMessage = TextMessage(1,message,Timestamp(Date()),"9bBm4sEB6XauE94eiS4gwTZ0LSa2","dwight")
                val textMessage = hashMapOf(
                    "id" to ref.id,
                    "message" to message,
                    "createdAt" to Timestamp(Date()),
                    "senderId" to auth.currentUser?.uid,
                    "senderName" to "dwight",
                    "messageType" to "1",

                    )



                db.collection("chats").document(chatId).collection("messages").document(ref.id)
                    .set(textMessage)
            }
        }

        firestoreDB = FirebaseFirestore.getInstance()

        val mLayoutManager = LinearLayoutManager(applicationContext)
        mLayoutManager.setReverseLayout(true)
        chatRecyclerView.layoutManager = mLayoutManager
        chatRecyclerView.itemAnimator = DefaultItemAnimator()

        loadChatList(chatId)

        firestoreListener =
            firestoreDB!!.collection("chats").document(chatId).collection("messages")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener(EventListener { documentSnapshots, e ->
                    if (e != null) {
                        Log.e(TAG, "Listen failed!", e)
                        return@EventListener
                    }

                    messageList = mutableListOf<TextMessage>()

                    if (documentSnapshots != null) {
                        for (doc in documentSnapshots) {
                            val message = doc.toObject(TextMessage::class.java)
                            Log.d("documentSnapshots", message.id.toString())
                            message.id = doc.id
                            messageList.add(message)
                        }
                    }

                    adapter!!.notifyDataSetChanged()
                    chatRecyclerView.adapter = adapter
                })


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                val thumBNail: Bitmap = data!!.extras!!.get("data") as Bitmap
            }
        }
    }

    private fun loadChatList(chatId: String) {

        val query = firestoreDB!!.collection("chats").document(chatId).collection("messages")
            .orderBy("createdAt", Query.Direction.DESCENDING)

        val response = FirestoreRecyclerOptions.Builder<TextMessage>()
            .setQuery(query, TextMessage::class.java)
            .build()



        adapter =
            object : FirestoreRecyclerAdapter<TextMessage, RecyclerView.ViewHolder>(response) {

                val MSG_TYPE_LEFT = 0
                val MSG_TYPE_RIGHT = 1
                val MSG_TYPE_CENTER = 2
                override fun onBindViewHolder(
                    holder: RecyclerView.ViewHolder,
                    position: Int,
                    model: TextMessage
                ) {
                    val mess = messageList[position]
                    val sfd = SimpleDateFormat("HH:mm aa")
                    if(mess.messageType == "3") {

                        Log.d("TextMessageCenterViewHolder", mess.message.toString())

                        when (holder) {
                            is TextMessageCenterViewHolder -> {
                                Log.d("TextMessageCenterViewHolder", mess.message.toString())
                                holder.text_view_message.text = mess.message

                            }


                        }
                    } else {
                        if (mess.senderId == auth.currentUser?.uid) {

                            when (holder) {
                                is TextMessageSenderViewHolder -> {
                                    Log.d("TextMessageSenderViewHolder", mess.message.toString())
                                    holder.text_view_message.text = mess.message

                                    holder.text_view_time.text = sfd.format(mess.createdAt?.toDate())


                                }


                            }

                        } else if (mess.senderId != auth.currentUser?.uid) {

                            when (holder) {

                                is TextMessageReceiverHolder -> {
                                    // Manually get the model item
                                    Log.d("TextMessageReceiverHolder", mess.message.toString())
                                    holder.text_view_message.text = mess.message
                                    holder.text_view_time.text =
                                        sfd.format(mess.createdAt?.toDate())
                                }
                            }


                        }

                    }


                }

                override fun getItemViewType(position: Int): Int {
                    val mess = messageList[position]
                    if(mess.messageType == "3") {
                        return MSG_TYPE_CENTER
                    } else {
                        if (mess.senderId == auth.currentUser?.uid) {
                            return MSG_TYPE_RIGHT
                        }
                        if (mess.senderId != auth.currentUser?.uid) {
                            return MSG_TYPE_LEFT
                        }
                    }


                    return 3

                }


                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): RecyclerView.ViewHolder {
                    Log.d("onCreateViewHolder", viewType.toString())
                    if (viewType == MSG_TYPE_CENTER) {
                        Log.d("MSG_TYPE_CENTER", MSG_TYPE_CENTER.toString())
                        val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.layout_center_message, parent, false)
                        return TextMessageCenterViewHolder(view)
                    } else {
                        if (viewType == MSG_TYPE_RIGHT) {
                            Log.d("MSG_TYPE_RIGHT", MSG_TYPE_RIGHT.toString())
                            val view = LayoutInflater.from(parent.context)
                                .inflate(R.layout.layout_message_sender, parent, false)
                            return TextMessageSenderViewHolder(view)
                        } else if (viewType == MSG_TYPE_LEFT) {
                            Log.d("MSG_TYPE_LEFT", MSG_TYPE_LEFT.toString())
                            val view = LayoutInflater.from(parent.context)
                                .inflate(R.layout.layout_message_receiver, parent, false)
                            return TextMessageReceiverHolder(view)
                        }
                    }

                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_message_receiver, parent, false)

                    return TextMessageViewHolder(view)

                }

                override fun onError(e: FirebaseFirestoreException) {
                    e!!.message?.let { Log.e("error", it) }
                }
            }

        adapter!!.notifyDataSetChanged()
        chatRecyclerView.adapter = adapter
    }

    public override fun onStart() {
        super.onStart()

        adapter!!.startListening()
    }
}
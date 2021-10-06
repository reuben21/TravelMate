package com.project22.myapplication.screens


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project22.myapplication.R
import com.project22.myapplication.model.TextMessage
import kotlinx.android.synthetic.main.activity_chat_screen.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import java.text.SimpleDateFormat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.project22.myapplication.adapters.*
import com.project22.myapplication.database.DatabaseHelper
import kotlinx.android.synthetic.main.activity_travel_destination.*
import kotlinx.android.synthetic.main.activity_travel_destination_form.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.io.ByteArrayOutputStream
import java.io.IOException


class ChatScreen : AppCompatActivity() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
    }

    private val PICK_IMAGE_REQUEST = 71
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    var chatIdGlobal: String? = null
    var auth: FirebaseAuth = Firebase.auth
    val db = Firebase.firestore
    private var filePath: Uri? = null
    private val TAG = "CHAT SCREEN"
    private var adapter: FirestoreRecyclerAdapter<TextMessage, RecyclerView.ViewHolder>? = null
    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    private var messageList = mutableListOf<TextMessage>()


    var userIdDB: String? = null
    var emailDB: String? = null
    var firstNameDB: String? = null
    var lastNameDB: String? = null
    var profileImageUrlDB: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_screen)
        val chatId = intent?.getStringExtra("chatId").toString()
        val chatName= intent?.getStringExtra("chatName").toString()
        val chatImageHolder= intent?.getStringExtra("chatImageHolder").toString()
        chatIdGlobal = chatId

        toolbar_for_chat_screen.title = chatName
        toolbar_for_chat_screen.setNavigationOnClickListener { onBackPressed() }
        toolbar_for_chat_screen.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.chatProfileMenuButton -> {
                    Log.d("CHAT SCREEN","PRINTING")
                    val intent = Intent(this,chat_profile_screen::class.java)
                    intent.putExtra("chatId",chatId)
                    intent.putExtra("chatName",chatName)
                    intent.putExtra("chatImageHolder",chatImageHolder)
                    startActivity(intent)
                    true
                }
                else -> false
            }

        }
        val dbHelper = DatabaseHelper(this)
        val res = dbHelper.allData
        while (res.moveToNext()) {
            userIdDB  = res.getString(0)
            emailDB = res.getString(1)
            firstNameDB = res.getString(2)
            lastNameDB = res.getString(3)
            profileImageUrlDB = res.getString(4)

        }
        Log.d("TAG DB HELPER",userIdDB.toString())
        Log.d("TAG DB HELPER",emailDB.toString())

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
                    "senderName" to firstNameDB + " " +lastNameDB,
                    "messageType" to "1",

                    )



                db.collection("chats").document(chatId).collection("messages").document(ref.id)
                    .set(textMessage)
                (textInputMessage as TextView).text = ""
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

//    private fun launchGallery() {
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
//    }





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
                val MSG_TYPE_IMAGE_LEFT = 4
                val MSG_TYPE_IMAGE_RIGHT = 5
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
                    } else if (mess.messageType == "4") {
                        if (mess.senderId == auth.currentUser?.uid) {

                            when (holder) {
                                is TextMessageSenderImageHolder -> {
                                    Log.d("TextMessageSenderViewHolder", mess.message.toString())
                                    applicationContext?.let {
                                        Glide.with(it)
                                            .load(mess.message)
                                            .placeholder(R.drawable.travel)
                                            .into(holder.text_view_message)
                                    }

                                    holder.text_view_time.text = sfd.format(mess.createdAt?.toDate())


                                }


                            }

                        } else if (mess.senderId != auth.currentUser?.uid) {

                            when (holder) {

                                is TextMessageReceiverImageHolder -> {
                                    // Manually get the model item
                                    Log.d("TextMessageReceiverHolder", mess.message.toString())
                                    applicationContext?.let {
                                        Glide.with(it)
                                            .load(mess.message)
                                            .placeholder(R.drawable.travel)
                                            .into(holder.text_view_message)
                                    }
                                    holder.text_view_time.text =
                                        sfd.format(mess.createdAt?.toDate())
                                }
                            }


                        }
                    }
                    else {
                        if (mess.senderId == auth.currentUser?.uid) {

                            when (holder) {
                                is TextMessageSenderViewHolder -> {
                                    Log.d("TextMessageSenderViewHolder", mess.message.toString())
                                    holder.text_view_message.text = mess.message
                                    holder.text_view_time.text = sfd.format(mess.createdAt?.toDate())
                                    holder.text_view_user_name.text = mess.senderName

                                }


                            }

                        } else if (mess.senderId != auth.currentUser?.uid) {

                            when (holder) {

                                is TextMessageReceiverHolder -> {
                                    // Manually get the model item
                                    Log.d("TextMessageReceiverHolder", mess.message.toString())
                                    holder.text_view_message.text = mess.message
                                    holder.text_view_time.text = sfd.format(mess.createdAt?.toDate())
                                    holder.text_view_user_name.text = mess.senderName
                                }
                            }


                        }

                    }


                }

                override fun getItemViewType(position: Int): Int {
                    val mess = messageList[position]
                    if(mess.messageType == "3") {
                        return MSG_TYPE_CENTER
                    } else if (mess.messageType == "4") {
                        if (mess.senderId == auth.currentUser?.uid) {
                            return MSG_TYPE_IMAGE_RIGHT
                        }
                        if (mess.senderId != auth.currentUser?.uid) {
                            return MSG_TYPE_IMAGE_LEFT
                        }

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
                    } else if (viewType == MSG_TYPE_IMAGE_LEFT) {
                        Log.d("MSG_TYPE_IMAGE_LEFT", MSG_TYPE_IMAGE_LEFT.toString())
                        val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.layout_image_message_receiver, parent, false)
                        return TextMessageReceiverImageHolder(view)

                    } else if (viewType == MSG_TYPE_IMAGE_RIGHT) {
                        Log.d("MSG_TYPE_IMAGE_RIGHT", MSG_TYPE_IMAGE_RIGHT.toString())
                        val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.layout_image_message_sender, parent, false)
                        return TextMessageSenderImageHolder(view)

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


//    private fun uploadImage(){
//
//
//
//        Log.d(TAG,chatIdGlobal.toString()+ " " +filePath.toString())
//
//        val ref = storageReference?.child("Chats/" + UUID.randomUUID().toString() + ".jpg")
//        val uploadTask = ref?.putFile(filePath!!)
//
//        val urlTask =
//            uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
//                if (!task.isSuccessful) {
//                    Log.d(TAG,chatIdGlobal.toString()+ " 1 " +filePath.toString())
//                    task.exception?.let {
//                        Log.d(TAG,chatIdGlobal.toString()+ " 2 " +filePath.toString())
//                        Log.d(TAG,it.localizedMessage)
//                        throw it
//                    }
//                }
//                return@Continuation ref.downloadUrl
//            })?.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val downloadUri = task.result
//
//                    Log.d(TAG,chatIdGlobal.toString()+ " 3 " +filePath.toString())
//
//                    val ref2 = db.collection("chats").document(chatIdGlobal.toString()).collection("messages").document()
//
////            val textMessage = TextMessage(1,message,Timestamp(Date()),"9bBm4sEB6XauE94eiS4gwTZ0LSa2","dwight")
//                    val textMessage = hashMapOf(
//                        "id" to ref2.id,
//                        "message" to downloadUri.toString(),
//                        "createdAt" to Timestamp(Date()),
//                        "senderId" to auth.currentUser?.uid,
//                        "senderName" to firstNameDB + " " +lastNameDB,
//                        "messageType" to "4" )
//
//
//
//                    db.collection("chats").document(chatIdGlobal.toString()).collection("messages").document(ref2.id)
//                        .set(textMessage)
//                    Log.d("END URL",downloadUri.toString())
//
//                } else {
//                    // Handle failures
//                }
//            }?.addOnFailureListener {
//                Log.d(TAG,it.localizedMessage.toString())
//            }
//
//
//
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
//            if (data == null || data.data == null) {
//                return
//            }
//                Log.d(TAG,chatIdGlobal.toString())
//                filePath = data.data
//                uploadImage()
//
//        }
//
//    }
//


}
package com.project22.myapplication.frangments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project22.myapplication.R

import com.project22.myapplication.adapters.DestinationViewHolder
import com.project22.myapplication.model.Destination
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

//    private fun getDestinationList(): ArrayList<Destination> {
//        val db = Firebase.firestore
//        val destinationRef = db.collection("destination").get()
//        val destinationPlaces = ArrayList<Destination>()
//        destinationRef.addOnCompleteListener { task ->
//            if(task.isSuccessful) {
//                for (document in task.result!!) {
//                    val placeName = document.data["placeName"].toString()
//                    val placeImageUrl = document.data["placeImageUrl"].toString()
//                    val traveller = document.data["travellers"].toString().toInt()
//                    destinationPlaces.add(Destination(placeName,traveller,placeImageUrl))
//
//                }
//            }
//
//        }
//
//        Log.d("TAG",destinationRef.result.toString())
//        return destinationPlaces
//    }

    private val TAG = "MainActivity"

    private var adapter: FirestoreRecyclerAdapter<Destination, DestinationViewHolder>? = null

    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    private var destinationList = mutableListOf<Destination>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        firestoreDB = FirebaseFirestore.getInstance()

        val mLayoutManager = LinearLayoutManager(activity?.applicationContext)
        destinationListRecyclerView.layoutManager = mLayoutManager
        destinationListRecyclerView.itemAnimator = DefaultItemAnimator()

        loadNotesList()

        firestoreListener = firestoreDB!!.collection("destination")
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed!", e)
                    return@EventListener
                }

                destinationList = mutableListOf<Destination>()

                if (documentSnapshots != null) {
                    for (doc in documentSnapshots) {
                        val destination = doc.toObject(Destination::class.java)
                        destination.id = doc.id
                        destinationList.add(destination)
                    }
                }

                adapter!!.notifyDataSetChanged()
                destinationListRecyclerView.adapter = adapter
            })
    }



    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }

    private fun loadNotesList() {

        val query = firestoreDB!!.collection("destination")

        val response = FirestoreRecyclerOptions.Builder<Destination>()
            .setQuery(query, Destination::class.java)
            .build()

        adapter = object : FirestoreRecyclerAdapter<Destination, DestinationViewHolder>(response) {
            override fun onBindViewHolder(holder: DestinationViewHolder, position: Int, model: Destination) {
                val dest = destinationList[position]

                holder.placeNameHolder.text = dest.placeName
                holder.travellersHolder.text = dest.travellers.toString()
                activity?.applicationContext?.let {
                    Glide.with(it)
                        .load(dest.placeImageUrl)
                        .placeholder(R.drawable.travel)
                        .into(holder.placeImageUrlHolder)
                }

//                holder.edit.setOnClickListener { updateNote(note) }
//
//                holder.delete.setOnClickListener { deleteNote(note.id!!) }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_home_destination_card, parent, false)

                return DestinationViewHolder(view)
            }

            override fun onError(e: FirebaseFirestoreException) {
                e!!.message?.let { Log.e("error", it) }
            }
        }

        adapter!!.notifyDataSetChanged()
        destinationListRecyclerView.adapter = adapter
    }

    public override fun onStart() {
        super.onStart()

        adapter!!.startListening()
    }

    public override fun onStop() {
        super.onStop()

        adapter!!.stopListening()
    }



}
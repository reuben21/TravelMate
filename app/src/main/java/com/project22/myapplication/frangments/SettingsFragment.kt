package com.project22.myapplication.frangments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project22.myapplication.R
import com.project22.myapplication.authentication.OverViewAuth
import kotlinx.android.synthetic.main.fragment_settings.*
import java.text.SimpleDateFormat


class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    var auth: FirebaseAuth = Firebase.auth
    val db = Firebase.firestore


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val docRef = db.collection("users").document(auth.uid.toString())
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {

                val date =  document.getDate("birthOfDate")
                displayFullName.text = document.data?.get("firstName").toString() + " " +document.data?.get("lastName").toString()
                displayEmailIdSettings.text = document.data?.get("email").toString()
                displayBirthDateSettings.text =  SimpleDateFormat("dd/MM/yyyy").format(date)
            } else {
                Log.d("TAG", "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(context, OverViewAuth::class.java))
        }
    }



}
package com.project22.myapplication.frangments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project22.myapplication.R
import com.project22.myapplication.authentication.OverViewAuth
import kotlinx.android.synthetic.main.fragment_settings.*
import java.lang.reflect.Method
import java.text.SimpleDateFormat


class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)




    }








    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
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

        setting.setOnClickListener{
            val popupMenu: PopupMenu = PopupMenu(this.context,setting)
            popupMenu.menuInflater.inflate(R.menu.overflow_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.action_crick ->
                        Toast.makeText(this.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                    R.id.action_ftbal ->
                        Toast.makeText(this.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                    R.id.action_hockey ->
                        Toast.makeText(this.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                }
                true
            })


            popupMenu.show()
        }

    }



}
package com.project22.myapplication.authentication

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project22.myapplication.MainActivity
import com.project22.myapplication.R
import com.project22.myapplication.database.DatabaseHelper
import kotlinx.android.synthetic.main.activity_over_view_auth.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.text.SimpleDateFormat

class OverViewAuth : AppCompatActivity() {


    public override fun onStart() {
        super.onStart()


    }
    fun showToast(text: String){
        Toast.makeText(this.applicationContext, text, Toast.LENGTH_LONG).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_over_view_auth)

        try {
            // TODO :- To Hide the Toolbar which Comes by Default
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
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

        var dbHelper = DatabaseHelper(this)



        var auth: FirebaseAuth = Firebase.auth
        val db = Firebase.firestore

        if (auth.currentUser != null) {
            val docRef = db.collection("users").document(auth.uid.toString())
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val dateVar =  document.getDate("birthOfDate")
                    try {
                        dbHelper.insertData(
                            auth.uid.toString(),
                            document.data?.get("email").toString(),
                            document.data?.get("firstName").toString(),
                            document.data?.get("lastName").toString(),
                            document.data?.get("profileImageUrl").toString(),
                            SimpleDateFormat("dd/MM/yyyy").format(dateVar).toString(),
                            document.data?.get("phoneNo").toString(),
                        )
                    }catch (e: Exception){
                        try {

                            dbHelper.updateData(
                                auth.uid.toString(),
                                document.data?.get("email").toString(),
                                document.data?.get("firstName").toString(),
                                document.data?.get("lastName").toString(),
                                document.data?.get("profileImageUrl").toString(),
                                SimpleDateFormat("dd/MM/yyyy").format(dateVar).toString(),
                                document.data?.get("phoneNo").toString(),

                                )
                        }catch (e: Exception){
                            e.printStackTrace()
                            showToast(e.message.toString())
                        }
                        e.printStackTrace()
                        showToast(e.message.toString())
                    }




//                    context?.let {
//                        Glide.with(it.applicationContext)
//                            .load(document.data?.get("profileImageUrl").toString())
//                            .placeholder(R.drawable.travel)
//                            .into( displayProfileImageSettingsFragment)
//                    }
//
//                    displayFullName.text = document.data?.get("firstName").toString() + " " +document.data?.get("lastName").toString()
//                    displayEmailIdSettings.text = document.data?.get("email").toString()
//                    displayBirthDateSettings.text =  SimpleDateFormat("dd/MM/yyyy").format(date)
                } else {
                    Log.d("TAG", "No such document")
                }
            }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }
            // User is signed in (getCurrentUser() will be null if not signed in)
            startActivity(Intent(this, MainActivity::class.java))
            finish();
        }



        loginButtonOverview.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        registerButtonOverview.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }


}
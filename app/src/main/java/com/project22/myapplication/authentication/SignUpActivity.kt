package com.project22.myapplication.authentication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project22.myapplication.MainActivity
import com.project22.myapplication.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.registerButton


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO :- Initialize Firebase Auth
        lateinit var auth: FirebaseAuth
        auth = Firebase.auth

        val db = Firebase.firestore

        setContentView(R.layout.activity_sign_up)

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

        // TODO :- To go back to Login Screen
        toolbar_back_to_Login.setNavigationOnClickListener { onBackPressed() }

        // TODO :- Register Functionality

        registerButton.setOnClickListener {
            val email = inputEmailRegister.text.toString()
            val password = inputPasswordRegister.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "createUserWithEmail:success")
                        val user = auth.currentUser
                        if (user != null) {
                            Log.d("USER", user.uid)
                            val userDetails = hashMapOf(
                                "userUID" to user.uid,
                                "firstName" to "Ada",
                                "lastName" to "Lovelace",
                                "email" to email,
                                "birthOfDate" to "21/10/1999"
                            )
                            db.collection("users").document(user.uid)
                                .set(userDetails)
                                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
                                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
//                            startActivity(Intent(this, MainActivity::class.java))
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
//                        updateUI(null)
                    }
                }
        }


    }



}
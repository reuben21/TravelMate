package com.project22.myapplication.authentication

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project22.myapplication.MainActivity
import com.project22.myapplication.R
import com.project22.myapplication.database.DatabaseHelper
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.registerButton
import java.text.SimpleDateFormat


class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO :- Initialize Firebase Auth
        var auth: FirebaseAuth = Firebase.auth
        var dbHelper = DatabaseHelper(this)
        val db = Firebase.firestore

        setContentView(R.layout.activity_login)
        registerButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

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




        loginButton.setOnClickListener {
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()

            if (email.validator().nonEmpty().validEmail().addErrorCallback {
                    EmailLayoutLogin.error = it
                    // it will contain the right message.
                    // For example, if edit text is empty,
                    // then 'it' will show "Can't be Empty" message
                }.check()) {
                EmailLayoutLogin.error = null
                if (password.validator()
                        .nonEmpty()
                        .atleastOneNumber()
                        .atleastOneSpecialCharacters()
                        .atleastOneUpperCase()
                        .addErrorCallback {
                            PasswordLayoutLogin.error = it
                            // it will contain the right message.
                            // For example, if edit text is empty,
                            // then 'it' will show "Can't be Empty" message
                        }
                        .check()
                ) {
                    PasswordLayoutLogin.error = null

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithEmail:success")
                                val user = auth.currentUser



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
                                                startActivity(Intent(this, MainActivity::class.java))

                                            }catch (e: Exception){

                                                e.printStackTrace()

                                            }


                                        } else {
                                            Log.d("TAG", "No such document")
                                        }
                                    }
                                        .addOnFailureListener { exception ->
                                            Log.d("TAG", "get failed with ", exception)
                                        }
                                    // User is signed in (getCurrentUser() will be null if not signed in)

                                }
                                startActivity(Intent(this, MainActivity::class.java))


                        }

                }
            }


        }


        toolbar_back_to_overviewScreen.setNavigationOnClickListener { onBackPressed() }

    }


}
package com.project22.myapplication.authentication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project22.myapplication.MainActivity
import com.project22.myapplication.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.registerButton
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO :- Initialize Firebase Auth
//        lateinit var auth: FirebaseAuth
        var auth: FirebaseAuth = Firebase.auth

        val db = Firebase.firestore

        setContentView(R.layout.activity_sign_up)

        var currentSelectedDate: Long? = null

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

        fun onDateSelected(dateTimeStampInMillis: Long) {
            currentSelectedDate = dateTimeStampInMillis
            val dateTime: LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(
                currentSelectedDate!!
            ), ZoneId.systemDefault())
            val dateAsFormattedText: String = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            dateTextInput.hint = dateAsFormattedText
        }

        fun showDatePicker() {
            val selectedDateInMillis = currentSelectedDate ?: System.currentTimeMillis()

            MaterialDatePicker.Builder.datePicker().setSelection(selectedDateInMillis).build().apply {
                addOnPositiveButtonClickListener { dateInMillis -> onDateSelected(dateInMillis) }
            }.show(supportFragmentManager, MaterialDatePicker::class.java.canonicalName)
        }

        dateTextInputEditText.setOnClickListener {
            showDatePicker()
        }




        // TODO :- Register Functionality
        registerButton.setOnClickListener {
            val email = inputEmailRegister.text.toString()
            val password = inputPasswordRegister.text.toString()
            val firstName = inputFirstName.text.toString()
            val lastName = inputLastName.text.toString()

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
                                "firstName" to firstName,
                                "lastName" to lastName,
                                "email" to email,
                                "birthOfDate" to "21/10/1999"
                            )
                            db.collection("users").document(user.uid)
                                .set(userDetails)
                                .addOnSuccessListener {
                                    startActivity(Intent(this, MainActivity::class.java))
                                    Log.d("TAG", "DocumentSnapshot successfully written!")

                                }
                                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

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
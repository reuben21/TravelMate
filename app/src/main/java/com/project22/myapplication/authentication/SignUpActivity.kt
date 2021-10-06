package com.project22.myapplication.authentication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.project22.myapplication.R
import com.project22.myapplication.database.DatabaseHelper
import com.project22.myapplication.screens.TravelDestination
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class SignUpActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO :- Initialize Firebase Auth
//        lateinit var auth: FirebaseAuth
        var auth: FirebaseAuth = Firebase.auth

        val db = Firebase.firestore

        setContentView(R.layout.activity_sign_up)
        var finalTimestampDate: Timestamp? = null
        var currentSelectedDate: Long? = null

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

        // TODO :- To go back to Login Screen
        toolbar_back_to_Login.setNavigationOnClickListener { onBackPressed() }



        fun onDateSelected(dateTimeStampInMillis: Long) {
            currentSelectedDate = dateTimeStampInMillis

            val dateTime: LocalDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(
                    currentSelectedDate!!
                ), ZoneId.systemDefault()
            )
            val dateAsFormattedText: String =
                dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            finalTimestampDate = Timestamp(Date(dateTimeStampInMillis))
            (dateTextInputEditText as TextView).text = dateAsFormattedText
        }

        fun showDatePicker() {
            val selectedDateInMillis = currentSelectedDate ?: System.currentTimeMillis()

            val today = MaterialDatePicker.todayInUtcMilliseconds()
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

            calendar.timeInMillis = today
            calendar.add(Calendar.YEAR, -18)

            val thisDay = calendar.timeInMillis

            val calendar2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

            calendar2.timeInMillis = today
            calendar2.add(Calendar.YEAR, -55)
            val thisDay2 = calendar2.timeInMillis


            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setStart(thisDay2)
                    .setEnd(thisDay)

            MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraintsBuilder.build())
                .setSelection(selectedDateInMillis).build()
                .apply {
                    addOnPositiveButtonClickListener { dateInMillis -> onDateSelected(dateInMillis) }
                }.show(supportFragmentManager, MaterialDatePicker::class.java.canonicalName)
        }

        dateTextInputEditText.setOnClickListener {
            showDatePicker()
        }


        // TODO :- Register Functionality
        registerButton.setOnClickListener {
            val email = inputEmailRegister.text.toString()
            val phoneNo = inputPhoneRegister.text.toString()
            val date = dateTextInputEditText.text.toString()
            val password = inputPasswordRegister.text.toString()
            val firstName = inputFirstName.text.toString()
            val lastName = inputLastName.text.toString()

            if (firstName.validator()
                    .nonEmpty()
                    .noNumbers()
                    .noSpecialCharacters()
                    .addErrorCallback {
                        firstNameLayout.error = it
                        // it will contain the right message.
                        // For example, if edit text is empty,
                        // then 'it' will show "Can't be Empty" message
                    }.check()
            ) {
                firstNameLayout.error = null
                if (lastName.validator()
                        .nonEmpty()
                        .noNumbers()
                        .noSpecialCharacters()
                        .addErrorCallback {
                            lastNameLayout.error = it
                            // it will contain the right message.
                            // For example, if edit text is empty,
                            // then 'it' will show "Can't be Empty" message
                        }.check()
                ) {
                    lastNameLayout.error = null
                    if (date.validator().nonEmpty().addErrorCallback {
                            dateTextInputLayout.error = it
                            // it will contain the right message.
                            // For example, if edit text is empty,
                            // then 'it' will show "Can't be Empty" message
                        }.check()) {

                        dateTextInputLayout.error = null

                    if (phoneNo.validator().nonEmpty().validNumber().addErrorCallback {
                            PhoneLayout.error = "Add A Proper Phone Number"
                        }.check()) {
                        PhoneLayout.error = null
                        if (email.validator().nonEmpty().validEmail().addErrorCallback {
                                EmailLayout.error = it
                                // it will contain the right message.
                                // For example, if edit text is empty,
                                // then 'it' will show "Can't be Empty" message
                            }.check()) {
                            EmailLayout.error = null
                            if (password.validator()
                                    .nonEmpty()
                                    .atleastOneNumber()
                                    .atleastOneSpecialCharacters()
                                    .atleastOneUpperCase()
                                    .addErrorCallback {
                                        PasswordLayout.error = it
                                        // it will contain the right message.
                                        // For example, if edit text is empty,
                                        // then 'it' will show "Can't be Empty" message
                                    }
                                    .check()
                            ) {
                                PasswordLayout.error = null

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
                                                    "birthOfDate" to finalTimestampDate,
                                                    "phoneNo" to phoneNo,

                                                )

                                                db.collection("users").document(user.uid)
                                                    .set(userDetails)
                                                    .addOnSuccessListener {
                                                        val ref = db.collection("chats").document()
                                                        val chatAdmin = hashMapOf(
                                                            "chatId" to ref.id,
                                                            "chatImageHolder" to "https://firebasestorage.googleapis.com/v0/b/travelmate-89b60.appspot.com/o/Destinations%2Feba48840-3a90-477d-acbb-9a14ef8bee51.jpg?alt=media&token=859417f9-f129-4573-8931-600cc3a6679d",
                                                            "chatName" to "Admin",
                                                            )
                                                        val textMessage = hashMapOf(
                                                            "id" to ref.id,
                                                            "message" to "Admin Chat created by default for user ${firstName} ${lastName}",
                                                            "createdAt" to Timestamp(Date()),
                                                            "senderId" to auth.currentUser?.uid,
                                                            "senderName" to firstName + " " +lastName,
                                                            "messageType" to "3",

                                                            )
                                                        db.collection("chats").document(ref.id).collection("messages").document(ref.id)
                                                            .set(textMessage)
                                                            .addOnSuccessListener {
                                                                db.collection("users").document("wbjnaRO5MedStcTJ2ggmm7pnw2x2").collection("chats").document(ref.id)
                                                                    .set(chatAdmin)
                                                                    .addOnSuccessListener {
                                                                        db.collection("users").document(user.uid).collection("chats").document(ref.id)
                                                                            .set(chatAdmin)
                                                                            .addOnSuccessListener {
                                                                                val intent = Intent(this,
                                                                                    ProfilePictureActivity::class.java)
                                                                                intent.putExtra("userUID",user.uid)
                                                                                startActivity(
                                                                                    intent
                                                                                )
                                                                            }
                                                                    }
                                                            }



                                                        Log.d(
                                                            "TAG",
                                                            "DocumentSnapshot successfully written!"
                                                        )

                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.w(
                                                            "TAG",
                                                            "Error writing document",
                                                            e
                                                        )
                                                    }

                                            }

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(
                                                "TAG", "createUserWithEmail:failure",
                                                task.exception
                                            )
                                            Toast.makeText(
                                                baseContext,
                                                task.exception?.localizedMessage,
                                                Toast.LENGTH_LONG
                                            ).show()

                                        }
                                    }

                            }

                        }
                    }

                    }

                }
            }


        }


    }


}




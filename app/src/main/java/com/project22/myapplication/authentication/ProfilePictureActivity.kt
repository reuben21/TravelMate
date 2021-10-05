package com.project22.myapplication.authentication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.project22.myapplication.R
import com.project22.myapplication.database.DatabaseHelper
import kotlinx.android.synthetic.main.activity_profile_picture.*
import java.io.ByteArrayOutputStream
import java.util.*

class ProfilePictureActivity : AppCompatActivity() {

    var profileImageUrl: String? = null
    private var filePath: Uri? = null
    var userUID: String? = null

    private fun uploadImage(){

        val db = Firebase.firestore

        val storage = FirebaseStorage.getInstance()
        // Create a storage reference from our app
        // Create a storage reference from our app
        val storageRef = storage.reference
        val riversRef = storageRef.child("users/" + userUID.toString()+".jpg")

        val bitmap = (displayProfileImageSignUpPage.getDrawable() as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data: ByteArray = baos.toByteArray()


        val uploadTask: UploadTask = riversRef.putBytes(data)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            riversRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                profileImageUrl = downloadUri.toString()
                val userDetails = hashMapOf(
                    "profileImageUrl" to downloadUri.toString()
                )
                db.collection("users").document(userUID.toString()).update(userDetails as Map<String, Any>)

                    .addOnSuccessListener {

                        Toast.makeText(this, "Image was Uploaded Successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,LoginActivity::class.java))

                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            "TAG",
                            "Error writing document",
                            e
                        )
                    }
                (userProfilePictureTextInputEdit as TextView).text = "Profile Picture Uploaded"


                Toast.makeText(
                    baseContext,
                    "Image Uploaded Successfully",
                    Toast.LENGTH_LONG
                ).show()

                Log.d("END URL",downloadUri.toString())
            } else {
                // Handle failures
                // ...
            }
        }

    }


    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(intent)

    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            filePath = data?.data

            Log.d("TAG filePath", filePath.toString())
            val imageBitmap = data?.extras?.get("data") as Bitmap

            Log.d("TAG filePath", imageBitmap.toString())
            displayProfileImageSignUpPage.setImageBitmap(imageBitmap)

            uploadImage()

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_picture)

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

        val userId = intent?.getStringExtra("userUID").toString()
        userUID = userId
        userProfilePictureTextInputEdit.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }
}
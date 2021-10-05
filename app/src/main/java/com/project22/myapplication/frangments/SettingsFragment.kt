package com.project22.myapplication.frangments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.FileProvider
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.project22.myapplication.R
import com.project22.myapplication.authentication.OverViewAuth
import kotlinx.android.synthetic.main.activity_travel_destination_form.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.io.File
import java.io.IOException
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.drawable.BitmapDrawable
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream
import com.google.android.gms.tasks.OnSuccessListener

import com.google.android.gms.tasks.OnFailureListener
import com.project22.myapplication.authentication.LoginActivity
import com.project22.myapplication.database.DatabaseHelper
import kotlinx.android.synthetic.main.activity_travel_destination.*



class SettingsFragment : Fragment() {

    var userIdDB: String? = null
    var emailDB: String? = null
    var firstNameDB: String? = null
    var lastNameDB: String? = null
    var profileImageUrlDB: String? = null
    var birthDateDB: String? = null
    var phoneDb: String? = null
    // TODO: Rename and change types of parameters

    private var filePath: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    private fun uploadImage(){
        var auth: FirebaseAuth = Firebase.auth
        val db = Firebase.firestore

        val storage = FirebaseStorage.getInstance()
        // Create a storage reference from our app
        // Create a storage reference from our app
        val storageRef = storage.reference
        val riversRef = storageRef.child("users/" + auth.currentUser?.uid.toString()+".jpg")

        val bitmap = (displayProfileImageSettingsFragment.getDrawable() as BitmapDrawable).bitmap
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
                val userDetails = hashMapOf(
                    "profileImageUrl" to downloadUri.toString()
                )
                db.collection("users").document(auth.currentUser?.uid.toString()).update(userDetails as Map<String, Any>)

                    .addOnSuccessListener {
                        //TODO: onupdate
                        val dbHelper = this.context?.let { DatabaseHelper(it) }
                        if (dbHelper != null) {
                            dbHelper.updateData(

                                auth.uid.toString(),
                                emailDB.toString(),firstNameDB.toString(),lastNameDB.toString(),downloadUri.toString(),birthDateDB.toString(),phoneDb.toString(),

                                )
                        }
                        Toast.makeText(this.context, "Image was upated Successfully", Toast.LENGTH_SHORT).show()

                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            "TAG",
                            "Error writing document",
                            e
                        )
                    }
                Log.d("END URL",downloadUri.toString())
            } else {
                // Handle failures
                // ...
            }
        }
//        uploadTask.addOnFailureListener {
//            // Handle unsuccessful uploads
//            Toast.makeText(this.context, "Please Upload an Image", Toast.LENGTH_SHORT).show()
//
//        }.addOnSuccessListener {
//
//
//            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//            // ...
//        }.addOnCompleteListener {
//
//        }
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
            displayProfileImageSettingsFragment.setImageBitmap(imageBitmap)

            uploadImage()

        }
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

        val dbHelper = this.context?.let { DatabaseHelper(it) }
        val res = dbHelper?.allData
        while (res?.moveToNext() == true) {
            userIdDB  = res?.getString(0)
            emailDB = res?.getString(1)
            firstNameDB = res?.getString(2)
            lastNameDB = res?.getString(3)
            profileImageUrlDB = res?.getString(4)
            birthDateDB  = res?.getString(5)
            phoneDb=res?.getString(6)
        }
        Log.d("TAG DB HELPER",userIdDB.toString())
        Log.d("TAG DB HELPER",emailDB.toString())

        context?.let {
            Glide.with(it.applicationContext)
                .load(profileImageUrlDB.toString())
                .placeholder(R.drawable.travel)
                .into( displayProfileImageSettingsFragment)
        }

        displayFullName.text = firstNameDB?.toUpperCase(Locale.ROOT) + " " + lastNameDB?.toUpperCase(Locale.ROOT)
        displayEmailIdSettings.text = emailDB
        displayBirthDateSettings.text =  birthDateDB
        phoneNoSettings.text =  phoneDb




        setting.setOnClickListener{
            val popupMenu: PopupMenu = PopupMenu(this.context,setting)
            popupMenu.menuInflater.inflate(R.menu.overflow_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.editProfileButton ->{
                        dispatchTakePictureIntent()
                        Toast.makeText(this.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                    }
                    R.id.notifButton ->{

                    }
                    R.id.logoutButton -> {
                        if (dbHelper != null) {
                            userIdDB?.let { it1 -> dbHelper.deleteData(it1) }
                        }
                        auth.signOut()
                        startActivity(Intent(context, OverViewAuth::class.java))
                    }
                }
                true
            })

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true)
            }else{
                try {
                    val fields = popupMenu.javaClass.declaredFields
                    for (field in fields) {
                        if ("mPopup" == field.name) {
                            field.isAccessible = true
                            val menuPopupHelper = field[popupMenu]
                            val classPopupHelper =
                                Class.forName(menuPopupHelper.javaClass.name)
                            val setForceIcons: Method = classPopupHelper.getMethod(
                                "setForceShowIcon",
                                Boolean::class.javaPrimitiveType
                            )
                            setForceIcons.invoke(menuPopupHelper, true)
                            break
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            popupMenu.show()
        }

    }



}
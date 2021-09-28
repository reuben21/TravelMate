package com.project22.myapplication.frangments

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
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
import kotlinx.android.synthetic.main.fragment_settings.*
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*


class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    val REQUEST_IMAGE_CAPTURE = 1
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    var ticketImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)




    }

    private fun uploadImage(){
        if(filePath != null){
            val ref = storageReference?.child("travel/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    ticketImageUrl = downloadUri.toString()
                    Log.d("UPLOAD URL OF IMAGE",downloadUri.toString())
                } else {
                    // Handle failures
                }
            }?.addOnFailureListener{

            }
        }else{
            Toast.makeText(this.context, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            val imageBitmap = data?.extras?.get("data") as Bitmap
            displayProfileImage.setImageBitmap(imageBitmap)

        }
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



        setting.setOnClickListener{
            val popupMenu: PopupMenu = PopupMenu(this.context,setting)
            popupMenu.menuInflater.inflate(R.menu.overflow_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.editProfileButton ->{
                        dispatchTakePictureIntent()
                        Toast.makeText(this.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                    }
                    R.id.notifButton ->
                        Toast.makeText(this.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                    R.id.logoutButton -> {
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
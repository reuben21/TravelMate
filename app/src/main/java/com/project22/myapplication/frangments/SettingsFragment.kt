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


class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    val REQUEST_IMAGE_CAPTURE = 1
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    var ticketImageUrl: String = ""
    lateinit var currentPhotoPath: String

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

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES),timeStamp)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

//    private fun dispatchTakePictureIntent() {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            // Ensure that there's a camera activity to handle the intent
//            context?.let {
//                takePictureIntent.resolveActivity(it.packageManager)?.also {
//                    // Create the File where the photo should go
//                    val photoFile: File? = try {
//                        createImageFile()
//                    } catch (ex: IOException) {
//                        // Error occurred while creating the File
//
//                        null
//                    }
//                    // Continue only if the File was successfully created
//                    photoFile?.also {
//                        val photoURI: Uri = FileProvider.getUriForFile(
//                            requireContext(),
//                            "com.example.android.fileprovider",
//                            it
//                        )
//                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//                    }
//                }
//            }
//        }
//    }



    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(intent)
        Log.d("TAG resultLauncher","resultLauncher")
    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("TAG resultLauncher 2",result.data.toString())
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data


            filePath = data?.data

            Log.d("TAG filePath", filePath.toString())
            val imageBitmap = data?.extras?.get("data") as Bitmap
            Log.d("TAG filePath", imageBitmap.toString())
//            try {
//                Log.d("TAG FILEDATA",data.dataString.toString())
//                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, filePath)
//                displayProfileImage.setImageBitmap(bitmap)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
            displayProfileImage.setImageBitmap(imageBitmap)
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
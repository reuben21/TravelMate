package com.project22.myapplication.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.project22.myapplication.R
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import kotlinx.android.synthetic.main.activity_travel_destination_form.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class TravelDestinationForm : AppCompatActivity() {
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    var buttonLocation: String = ""
    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    var originName: String = ""
    var destinationName: String = ""

    var originNameLatitude: Double = 0.0
    var originNameLongitude: Double = 0.0

    var destinationNameLatitude: Double = 0.0
    var destinationNameLongitude: Double = 0.0

    var startDateVar: Long = 0
    var endDateVar: Long = 0

    var chatName: String = ""
    var ticketImageUrl: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_destination_form)

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

        if (!Places.isInitialized()) {
            Places.initialize(
                getApplicationContext(),
                getString(R.string.google_maps_key),
                Locale.US
            );
        }

        toolbar_back_to_Login.setNavigationOnClickListener { onBackPressed() }

        inputOrigin.setOnClickListener {
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            buttonLocation = "inputOrigin"
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        inputDestination.setOnClickListener {
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            buttonLocation = "inputDestination"
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        inputTicketUpload.setOnClickListener { launchGallery() }
//        uploadTicketButton.setOnClickListener { uploadImage() }


        SubmitButtonTravelForm.setOnClickListener {
            chatName = inputChatName.text.toString()

            if (originName.validator()
                    .nonEmpty()
                    .addErrorCallback {
                        originLayout.error = it
                        // it will contain the right message.
                        // For example, if edit text is empty,
                        // then 'it' will show "Can't be Empty" message
                    }.check()
            ) {
                if (destinationName.validator()
                        .nonEmpty()
                        .addErrorCallback {
                            destinationLayout.error = it
                            // it will contain the right message.
                            // For example, if edit text is empty,
                            // then 'it' will show "Can't be Empty" message
                        }.check()
                ) {
                    if (inputStartDate.validator()
                            .nonEmpty()
                            .addErrorCallback {
                                startDate.error = it
                                // it will contain the right message.
                                // For example, if edit text is empty,
                                // then 'it' will show "Can't be Empty" message
                            }.check()
                    ) {
                        if (inputEndDate.validator()
                                .nonEmpty()
                                .addErrorCallback {
                                    EndDate.error = it
                                    // it will contain the right message.
                                    // For example, if edit text is empty,
                                    // then 'it' will show "Can't be Empty" message
                                }.check()
                        ) {

                            if (chatName.validator()
                                    .nonEmpty()
                                    .addErrorCallback {
                                        chatNameLayout.error = it
                                        // it will contain the right message.
                                        // For example, if edit text is empty,
                                        // then 'it' will show "Can't be Empty" message
                                    }.check()
                            ) {

                                if (ticketImageUrl.validator()
                                        .nonEmpty()
                                        .addErrorCallback {
                                            TicketUploadLayout.error = it
                                            // it will contain the right message.
                                            // For example, if edit text is empty,
                                            // then 'it' will show "Can't be Empty" message
                                        }.check()
                                ) {

                                    val destinationDetail = hashMapOf(
                                        "originName" to originName,
                                        "destinationName" to destinationName,
                                        "originLatitude" to originNameLatitude,
                                        "originLongitude" to originNameLongitude,
                                        "destinationLatitude" to destinationNameLatitude,
                                        "destinationLongitude" to destinationNameLongitude,
                                        "startDate" to Timestamp(Date(startDateVar)) ,
                                        "endDate" to Timestamp(Date(endDateVar)) ,
                                        "chatName" to chatName,
                                        "ticketImageUrl" to ticketImageUrl

                                    )
                                    Log.d("FORM DETAILS",destinationDetail.toString())

                                }


                            }

                        }
                    }
                }
            }



        }


        dateTextInputEditText.setOnClickListener {
            showDataRangePicker()
        }


    }

    private fun showDataRangePicker() {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        calendar.timeInMillis = today
        val thisDay = calendar.timeInMillis

        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

//        val constraintsBuilder =
//            CalendarConstraints.Builder()
//                .setStart(thisDay)

        val dateRangePicker =
            MaterialDatePicker
                .Builder.dateRangePicker()
                .setCalendarConstraints(constraintsBuilder.build())
                .setTitleText("Select Travel Date")
                .build()

        dateRangePicker.show(
            supportFragmentManager,
            "date_range_picker"
        )

        dateRangePicker.addOnPositiveButtonClickListener { dateSelected ->

            val startDate = dateSelected.first
            val endDate = dateSelected.second

            if (startDate != null && endDate != null) {

                (inputStartDate as TextView).text = convertLongToTime(startDate)
                (inputEndDate as TextView).text = convertLongToTime(endDate)
                (dateTextInputEditText as TextView).text = "Date Selected"
                startDateVar = startDate
                endDateVar = endDate
//                dateTextInputEditText as Text =
//                    "Reserved\nStartDate: ${convertLongToTime(startDate)}\n" +
//                            "EndDate: ${convertLongToTime(endDate)}"
            }
        }

    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat(
            "dd.MM.yyyy",
            Locale.getDefault()
        )
        return format.format(date)
    }

    private fun uploadImage(){
        if(filePath != null){
            val ref = storageReference?.child("travel/" + UUID.randomUUID().toString()+".jpg")
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
                    TicketUploadLayout.setEndIconDrawable(R.drawable.ic_tickmark)
                    (inputTicketUpload as TextView).text = "Ticket Uploaded Successfully"
                } else {
                    // Handle failures
                }
            }?.addOnFailureListener{

            }
        }else{
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                ticketImage.setImageBitmap(bitmap)
                uploadImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i("TAG", "Place: ${place.name}, ${place.latLng?.longitude}")
                        if (buttonLocation == "inputDestination") {
                            (inputDestination as TextView).text = place.name

                            destinationName = place.name.toString()
                            destinationNameLongitude = place.latLng?.longitude!!
                            destinationNameLatitude = place.latLng?.latitude!!


                        } else if (buttonLocation == "inputOrigin") {
                            (inputOrigin as TextView).text = place.name
                            originName = place.name.toString()
                            originNameLongitude = place.latLng?.longitude!!
                            originNameLatitude = place.latLng?.latitude!!
                        }


                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        status.statusMessage?.let { it1 -> Log.i("TAG", it1) }
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }

    }


}


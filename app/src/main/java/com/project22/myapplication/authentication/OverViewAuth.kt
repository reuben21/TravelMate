package com.project22.myapplication.authentication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project22.myapplication.MainActivity
import com.project22.myapplication.R
import com.project22.myapplication.database.DatabaseHelper
import kotlinx.android.synthetic.main.activity_over_view_auth.*

class OverViewAuth : AppCompatActivity() {


    internal var dbHelper = DatabaseHelper(this)

    public override fun onStart() {
        super.onStart()


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_over_view_auth)

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

        var auth: FirebaseAuth = Firebase.auth


        if (auth.currentUser != null) {
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
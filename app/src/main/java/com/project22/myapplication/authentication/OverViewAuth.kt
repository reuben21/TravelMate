package com.project22.myapplication.authentication

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.project22.myapplication.R
import kotlinx.android.synthetic.main.activity_over_view_auth.*

class OverViewAuth : AppCompatActivity() {
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


        loginButtonOverview.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        registerButtonOverview.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }


}
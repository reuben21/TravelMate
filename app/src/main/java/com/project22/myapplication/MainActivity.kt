package com.project22.myapplication

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.project22.myapplication.frangments.HomeFragment
import com.project22.myapplication.frangments.ChatFragment
import com.project22.myapplication.frangments.SettingsFragment
import com.project22.myapplication.frangments.TravelFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val settingsFragment = SettingsFragment()
    private val chatFragment = ChatFragment()
    private val travelFragment = TravelFragment()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        replaceFragment(homeFragment)

        bottom_navigation?.setOnItemSelectedListener {
            when(it.itemId) {
                    R.id.ic_home -> replaceFragment(homeFragment)
                    R.id.ic_settings->replaceFragment(settingsFragment)
                    R.id.ic_chats -> replaceFragment(chatFragment)
                    R.id.ic_travel -> replaceFragment(travelFragment)
            }
            true

        }




    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container,fragment)
        transaction.commit()
    }

}
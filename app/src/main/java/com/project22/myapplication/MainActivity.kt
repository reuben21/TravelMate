package com.project22.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.project22.myapplication.frangments.HomeFragment
import com.project22.myapplication.frangments.ProfileFragment
import com.project22.myapplication.frangments.SettingsFragment
import com.project22.myapplication.frangments.TravelFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val settingsFragment = SettingsFragment()
    private val profileFragment = ProfileFragment()
    private val travelFragment = TravelFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(homeFragment)

        bottom_navigation?.setOnItemSelectedListener {
            when(it.itemId) {
                    R.id.ic_home -> replaceFragment(homeFragment)
                    R.id.ic_settings->replaceFragment(settingsFragment)
                    R.id.ic_profile -> replaceFragment(profileFragment)
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
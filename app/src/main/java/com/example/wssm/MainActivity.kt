package com.example.wssm

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.wssm.screens.NavGraph
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {

    // 1. Permissions required for a 2026 Safety App
    private val requiredPermissions = mutableListOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.ACTIVITY_RECOGNITION
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val sms = permissions[Manifest.permission.SEND_SMS] ?: false
        val loc = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (!sms || !loc) {
            Toast.makeText(this, "Emergency features require SMS and Location.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Initialize Places with your PLACES API KEY
        setupPlaces()

        // 3. Request Permissions
        requestPermissionLauncher.launch(requiredPermissions)

        // 4. Load UI
        setContent {
            NavGraph()
        }
    }

    private fun setupPlaces() {
        try {
            if (!Places.isInitialized()) {
                // YOUR PLACES API KEY
                val placesKey = "AIzaSyCOIIlekYMOBtX4_tOsUXSwsOjpsNeNjVI"
                Places.initialize(applicationContext, placesKey)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
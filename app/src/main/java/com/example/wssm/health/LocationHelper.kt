package com.example.wssm.health

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationHelper(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun fetchLocation(onLocationReceived: (String) -> Unit) {
        val cts = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cts.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                // Success: Return coordinates
                onLocationReceived("${location.latitude}, ${location.longitude}")
            } else {
                // Failsafe: GPS might be off or no signal
                onLocationReceived("Location unavailable (Check if GPS is ON)")
            }
        }.addOnFailureListener { e ->
            // Failsafe: Hardware or Permission error
            onLocationReceived("Error: ${e.localizedMessage}")
        }
    }
}
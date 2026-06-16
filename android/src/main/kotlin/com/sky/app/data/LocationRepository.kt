package com.sky.app.data

import android.Manifest
import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.content.pm.PackageManager

class LocationRepository(private val application: Application) {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(application)
    }

    fun getCurrentLocation(callback: (Location?) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            callback(null)
            return
        }

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    callback(location)
                } else {
                    getDefaultLocation(callback)
                }
            }
        } catch (e: Exception) {
            getDefaultLocation(callback)
        }
    }

    private fun getDefaultLocation(callback: (Location?) -> Unit) {
        val location = Location(LocationManager.NETWORK_PROVIDER).apply {
            latitude = 31.7683
            longitude = 35.2137
        }
        callback(location)
    }
}

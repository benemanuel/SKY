package com.sky.app.data

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationRepository(private val application: Application) {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(application)
    }

    fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            application, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                application, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

    /**
     * Requests a current GPS fix. Falls back to the last known location, then
     * to null (the caller keeps its default coordinates). [callback] runs on
     * the main thread.
     */
    fun getCurrentLocation(callback: (Location?) -> Unit) {
        if (!hasPermission()) {
            callback(null)
            return
        }

        try {
            val cts = CancellationTokenSource()
            fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { location ->
                    if (location != null) callback(location) else lastKnown(callback)
                }
                .addOnFailureListener { lastKnown(callback) }
        } catch (e: SecurityException) {
            callback(null)
        }
    }

    private fun lastKnown(callback: (Location?) -> Unit) {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { callback(it) }
                .addOnFailureListener { callback(null) }
        } catch (e: SecurityException) {
            callback(null)
        }
    }
}

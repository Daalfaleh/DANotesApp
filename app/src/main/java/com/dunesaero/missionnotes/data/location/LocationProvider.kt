package com.dunesaero.missionnotes.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

sealed class LocationResult {
    data class Success(val latitude: Double, val longitude: Double) : LocationResult()
    data class Error(val message: String) : LocationResult()
    object PermissionDenied : LocationResult()
    object GpsDisabled : LocationResult() // Consider adding a check for this
    object Fetching : LocationResult()
}

@Singleton
class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fun getCurrentLocation(): Flow<LocationResult> = callbackFlow {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            trySend(LocationResult.PermissionDenied)
            channel.close()
            return@callbackFlow
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) &&
            !locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
            trySend(LocationResult.GpsDisabled)
            channel.close()
            return@callbackFlow
        }

        trySend(LocationResult.Fetching)

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000) // 5 seconds interval
            .setMinUpdateIntervalMillis(1000) // 1 second min interval
            .setMaxUpdates(1) // We only need one update
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                locationResult.lastLocation?.let { location ->
                    launch {
                        trySend(LocationResult.Success(location.latitude, location.longitude)).isSuccess
                        channel.close() // Close the flow after sending the location
                    }
                } ?: launch {
                    trySend(LocationResult.Error("Unable to get location.")).isSuccess
                    channel.close()
                }
                fusedLocationClient.removeLocationUpdates(this) // Stop updates after getting a result
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    launch {
                        trySend(LocationResult.Error("Location not available.")).isSuccess
                        channel.close()
                    }
                    fusedLocationClient.removeLocationUpdates(this) // Stop updates
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            .addOnFailureListener { e ->
                launch {
                    trySend(LocationResult.Error("Failed to request location updates: ${e.message}")).isSuccess
                    channel.close()
                }
            }

        awaitClose {
            // Ensure updates are removed if the flow is cancelled
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}

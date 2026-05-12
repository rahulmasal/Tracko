package com.tracko.app.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.coroutines.resume

object LocationUtils {

    private const val EARTH_RADIUS_KM = 6371.0

    fun calculateDistanceBetween(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    fun calculateDistanceInMeters(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        return calculateDistanceBetween(lat1, lng1, lat2, lng2) * 1000
    }

    fun isWithinGeofence(
        lat: Double, lng: Double,
        centerLat: Double, centerLng: Double,
        radiusMeters: Double
    ): Boolean {
        return calculateDistanceInMeters(lat, lng, centerLat, centerLng) <= radiusMeters
    }

    fun getLocationAccuracy(accuracy: Float): String {
        return when {
            accuracy < 10 -> "HIGH"
            accuracy < 50 -> "MEDIUM"
            accuracy < 100 -> "LOW"
            else -> "POOR"
        }
    }

    fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location? {
        return try {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            val cancellationTokenSource = CancellationTokenSource()

            suspendCancellableCoroutine { continuation ->
                fusedClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location ->
                    if (!continuation.isCompleted) {
                        continuation.resume(location)
                    }
                }.addOnFailureListener {
                    if (!continuation.isCompleted) {
                        continuation.resume(null)
                    }
                }
                continuation.invokeOnCancellation {
                    cancellationTokenSource.cancel()
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}

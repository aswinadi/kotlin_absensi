package com.maxmar.attendance.ui.screens.checkin

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Location manager for getting current location and calculating distance.
 */
class LocationManager(context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    /**
     * Get current location with timeout and fallback to last known location.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        return try {
            // Try to get current location with timeout
            withTimeoutOrNull(10000L) {
                suspendCancellableCoroutine { continuation ->
                    val cancellationToken = CancellationTokenSource()
                    
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cancellationToken.token
                    ).addOnSuccessListener { location ->
                        if (continuation.isActive) {
                            continuation.resume(location)
                        }
                    }.addOnFailureListener { exception ->
                        if (continuation.isActive) {
                            // Return null instead of throwing exception
                            continuation.resume(null)
                        }
                    }
                    
                    continuation.invokeOnCancellation {
                        cancellationToken.cancel()
                    }
                }
            } ?: getLastKnownLocation() // Fallback to last known location
        } catch (e: Exception) {
            // Fallback to last known location on any error
            getLastKnownLocation()
        }
    }
    
    /**
     * Get last known location as fallback.
     */
    @SuppressLint("MissingPermission")
    private suspend fun getLastKnownLocation(): Location? {
        return try {
            suspendCancellableCoroutine { continuation ->
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (continuation.isActive) {
                            continuation.resume(location)
                        }
                    }
                    .addOnFailureListener {
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }
            }
        } catch (e: Exception) {
            null
        }
    }
    
    companion object {
        /**
         * Calculate distance between two coordinates using Haversine formula.
         * @return Distance in meters
         */
        fun calculateDistance(
            lat1: Double,
            lon1: Double,
            lat2: Double,
            lon2: Double
        ): Double {
            val earthRadius = 6371000.0 // meters
            
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            
            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLon / 2) * sin(dLon / 2)
            
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            
            return earthRadius * c
        }
        
        /**
         * Check if location is within radius.
         */
        fun isWithinRadius(
            userLat: Double,
            userLon: Double,
            officeLat: Double,
            officeLon: Double,
            radiusMeters: Int
        ): Boolean {
            val distance = calculateDistance(userLat, userLon, officeLat, officeLon)
            return distance <= radiusMeters
        }
    }
}

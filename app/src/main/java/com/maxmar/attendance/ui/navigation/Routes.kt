package com.maxmar.attendance.ui.navigation

/**
 * Route constants for navigation.
 * All routes are defined here to avoid typos and enable type-safety.
 */
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val HOME = "home"
    const val FACE_CAPTURE = "face_capture"
    const val CHECK_IN = "check_in"
    const val CHECK_OUT = "check_out"
    const val GEOLOCATION_MAP = "geolocation_map/{userLat}/{userLon}/{officeLat}/{officeLon}/{radius}/{officeName}"
    const val MAP = "map"
    const val HISTORY = "history"
    const val PROFILE = "profile"
    const val ABSENT = "absent"
    const val BUSINESS_TRIP = "business_trip"
    const val BUSINESS_TRIP_DETAIL = "business_trip/{tripId}"
    const val APPROVAL = "approval"
    const val NOTIFICATIONS = "notifications"
    
    // Helper function for routes with arguments
    fun businessTripDetail(tripId: String) = "business_trip/$tripId"
    
    fun geolocationMap(
        userLat: Double,
        userLon: Double,
        officeLat: Double,
        officeLon: Double,
        radius: Int,
        officeName: String
    ) = "geolocation_map/$userLat/$userLon/$officeLat/$officeLon/$radius/${officeName.replace("/", "_")}"
}

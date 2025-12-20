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
}

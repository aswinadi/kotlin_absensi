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
    const val ABSENT_EDIT = "absent/{absentId}"
    const val BUSINESS_TRIP = "business_trip"
    const val BUSINESS_TRIP_DETAIL = "business_trip/{tripId}"
    const val BUSINESS_TRIP_CREATE = "business_trip_create"
    const val BUSINESS_TRIP_EDIT = "business_trip_edit/{tripId}"
    const val APPROVAL = "approval"
    const val NOTIFICATIONS = "notifications"
    const val CHANGE_PASSWORD = "change_password"
    const val COMPLETE_PROFILE = "complete_profile"
    
    // Realization routes
    const val REALIZATION_LIST = "realization"
    const val REALIZATION_FORM = "realization/{tripId}"
    
    // Helper function for routes with arguments
    fun absentEdit(absentId: Int) = "absent/$absentId"
    fun businessTripDetail(tripId: String) = "business_trip/$tripId"
    fun businessTripEdit(tripId: Int) = "business_trip_edit/$tripId"
    fun realizationForm(tripId: Int) = "realization/$tripId"
    
    fun geolocationMap(
        userLat: Double,
        userLon: Double,
        officeLat: Double,
        officeLon: Double,
        radius: Int,
        officeName: String
    ) = "geolocation_map/$userLat/$userLon/$officeLat/$officeLon/$radius/${officeName.replace("/", "_")}"

    // Field Attendance (Dinas Luar)
    const val FIELD_ATTENDANCE_FORM = "field_attendance_form"
    const val FIELD_ATTENDANCE_DEPARTURE = "field_attendance_departure/{fieldAttendanceId}"
    fun fieldAttendanceDeparture(id: Int) = "field_attendance_departure/$id"
}

/**
 * Data class for deep link information from push notifications.
 */
data class DeepLinkData(
    val type: String,      // e.g., "leave_request", "approval", "business_trip"
    val id: String,        // ID of the item to navigate to
    val action: String     // e.g., "acknowledge", "approve"
)


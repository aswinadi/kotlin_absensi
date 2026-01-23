package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName

/**
 * Field Attendance (Dinas Luar) model.
 */
data class FieldAttendance(
    val id: Int = 0,
    val date: String = "",
    @SerializedName("location_name")
    val locationName: String = "",
    val purpose: String = "",
    @SerializedName("arrival_photo_url")
    val arrivalPhotoUrl: String? = null,
    @SerializedName("arrival_latitude")
    val arrivalLatitude: Double? = null,
    @SerializedName("arrival_longitude")
    val arrivalLongitude: Double? = null,
    @SerializedName("arrival_time")
    val arrivalTime: String? = null,
    @SerializedName("departure_photo_url")
    val departurePhotoUrl: String? = null,
    @SerializedName("departure_latitude")
    val departureLatitude: Double? = null,
    @SerializedName("departure_longitude")
    val departureLongitude: Double? = null,
    @SerializedName("departure_time")
    val departureTime: String? = null,
    val status: String = "in_progress",
    @SerializedName("status_label")
    val statusLabel: String = "",
    @SerializedName("has_departure")
    val hasDeparture: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String? = null
)

/**
 * Field Attendance List Response.
 */
data class FieldAttendanceListResponse(
    val success: Boolean = false,
    val data: List<FieldAttendance> = emptyList(),
    val meta: PaginationMeta? = null
)

/**
 * Field Attendance Detail Response.
 */
data class FieldAttendanceDetailResponse(
    val success: Boolean = false,
    val data: FieldAttendance? = null,
    val message: String? = null
)

/**
 * Field Attendance Create/Update Response.
 */
data class FieldAttendanceResponse(
    val success: Boolean = false,
    val data: FieldAttendance? = null,
    val message: String? = null
)

/**
 * Employee info embedded in team field attendance.
 */
data class TeamFieldAttendanceEmployee(
    val id: Int,
    @SerializedName("full_name")
    val fullName: String?,
    @SerializedName("employee_code")
    val employeeCode: String?,
    val position: String?
)

/**
 * Team Field Attendance model (includes employee info).
 */
data class TeamFieldAttendance(
    val id: Int = 0,
    val date: String = "",
    @SerializedName("location_name")
    val locationName: String = "",
    val purpose: String = "",
    @SerializedName("arrival_photo_url")
    val arrivalPhotoUrl: String? = null,
    @SerializedName("arrival_time")
    val arrivalTime: String? = null,
    @SerializedName("departure_photo_url")
    val departurePhotoUrl: String? = null,
    @SerializedName("departure_time")
    val departureTime: String? = null,
    val status: String = "in_progress",
    val employee: TeamFieldAttendanceEmployee? = null
)

/**
 * Team Field Attendance List Response.
 */
data class TeamFieldAttendanceListResponse(
    val success: Boolean = false,
    val data: List<TeamFieldAttendance> = emptyList(),
    val meta: PaginationMeta? = null
)

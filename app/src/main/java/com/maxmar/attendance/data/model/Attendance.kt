package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName

/**
 * Check-in or Check-out details.
 */
data class CheckRecord(
    val time: String?,
    val latitude: Double?,
    val longitude: Double?,
    @SerializedName("is_within_radius")
    val isWithinRadius: Boolean?
)

/**
 * Attendance record from API.
 */
data class Attendance(
    val id: Int,
    val date: String,
    val office: String?,
    @SerializedName("check_in")
    val checkIn: CheckRecord?,
    @SerializedName("check_out")
    val checkOut: CheckRecord?
)

/**
 * Paginated attendance response.
 */
data class AttendanceHistoryResponse(
    val data: List<Attendance>,
    val meta: PaginationMeta?
)

/**
 * Pagination metadata.
 */
data class PaginationMeta(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("last_page")
    val lastPage: Int,
    @SerializedName("per_page")
    val perPage: Int,
    val total: Int
)

/**
 * Monthly attendance summary.
 */
data class AttendanceSummary(
    val year: Int,
    val month: Int,
    @SerializedName("month_name")
    val monthName: String,
    val present: Int,
    val late: Int,
    val sick: Int,
    val leave: Int,
    val absent: Int,
    @SerializedName("work_days")
    val workDays: Int
)

/**
 * Attendance summary API response.
 */
data class AttendanceSummaryResponse(
    val success: Boolean,
    val data: AttendanceSummary?
)

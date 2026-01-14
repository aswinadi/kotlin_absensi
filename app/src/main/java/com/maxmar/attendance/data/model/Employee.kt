package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName

/**
 * Office model with location data.
 */
data class Office(
    val name: String?,
    val latitude: Double?,
    val longitude: Double?,
    val radius: Int?
)

/**
 * Position with level for role-based access.
 * Level: 1 = Director, 2 = Manager, 3+ = Staff
 */
data class Position(
    val id: Int? = null,
    val name: String = "",
    val level: Int = 99
) {
    /** Check if this position is a manager level (level <= 2) */
    val isManager: Boolean get() = level <= 2
}

/**
 * Employee profile from API response.
 */
data class Employee(
    val id: Int,
    @SerializedName("employee_code")
    val employeeCode: String?,
    @SerializedName("full_name")
    val fullName: String,
    val email: String?,
    val phone: String?,
    val position: Position?,
    val department: String?,
    val company: String?,
    @SerializedName("sub_department")
    val subDepartment: String?,
    val office: Office?,
    @SerializedName("employee_type")
    val employeeType: String? = null,
    // Face validation fields
    @SerializedName("photo_url")
    val photoUrl: String? = null,
    @SerializedName("face_embedding")
    val faceEmbedding: List<Float>? = null
)

/**
 * Full employee profile response containing employee, schedule, and leave quota.
 */
data class EmployeeProfileData(
    val employee: Employee,
    val schedule: ScheduleInfo?,
    @SerializedName("leave_quota")
    val leaveQuota: LeaveQuota?
)

/**
 * Schedule info from profile response.
 */
data class ScheduleInfo(
    val name: String?,
    val shifts: List<ShiftInfo>?
)

/**
 * Shift info from profile response.
 */
data class ShiftInfo(
    val day: String?,
    @SerializedName("day_label")
    val dayLabel: String?,
    @SerializedName("start_time")
    val startTime: String?,
    @SerializedName("end_time")
    val endTime: String?,
    @SerializedName("is_overnight")
    val isOvernight: Boolean?
)

/**
 * Leave quota information.
 */
data class LeaveQuota(
    val year: Int?,
    val total: Int?,
    val used: Int?,
    val remaining: Int?,
    @SerializedName("period_start")
    val periodStart: String?,
    @SerializedName("period_end")
    val periodEnd: String?
)


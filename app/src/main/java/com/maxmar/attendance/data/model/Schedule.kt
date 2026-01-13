package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName

/**
 * Schedule model for the Maxmar Attendance API.
 */
data class Schedule(
    val id: Int,
    @SerializedName("schedule_name")
    val scheduleName: String,
    val company: String? = null,
    val department: String? = null
)

/**
 * Shift model representing a work shift.
 */
data class Shift(
    val id: Int,
    @SerializedName("day_of_week")
    val dayOfWeek: String = "",
    @SerializedName("day_label")
    val dayLabel: String = "",
    @SerializedName("start_time")
    val startTime: String? = null,
    @SerializedName("end_time")
    val endTime: String? = null,
    @SerializedName("is_overnight")
    val isOvernight: Boolean = false,
    @SerializedName("time_range")
    val timeRange: String? = null,
    val name: String? = null
)

/**
 * Schedule response with shifts.
 */
data class ScheduleResponse(
    val schedule: Schedule,
    val shifts: List<Shift> = emptyList()
)

/**
 * Today's shift response wrapper.
 */
data class TodayShiftResponse(
    @SerializedName("is_workday")
    val isWorkday: Boolean,
    @SerializedName("schedule_name")
    val scheduleName: String? = null,
    val shift: Shift? = null,
    val message: String? = null
)

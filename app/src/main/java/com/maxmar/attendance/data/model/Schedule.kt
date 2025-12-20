package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName

/**
 * Shift/Schedule model for today's work schedule.
 */
data class Shift(
    val id: Int,
    val name: String?,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    @SerializedName("day_label")
    val dayLabel: String?
)

/**
 * Today's shift response wrapper.
 */
data class TodayShiftResponse(
    @SerializedName("is_workday")
    val isWorkday: Boolean,
    val shift: Shift?
)

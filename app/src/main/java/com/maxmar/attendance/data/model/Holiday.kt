package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Holiday model for employee-specific holidays.
 */
data class Holiday(
    val id: Int,
    val date: String,
    val name: String,
    @SerializedName("applies_to")
    val appliesTo: String = "all",
    @SerializedName("is_optional")
    val isOptional: Boolean = false,
    val description: String? = null
) {
    /**
     * Get display label for applies_to field
     */
    val appliesToLabel: String
        get() = when (appliesTo) {
            "office" -> "Office"
            "operational" -> "Operasional"
            else -> "Semua"
        }

    /**
     * Parse date string to LocalDate
     */
    val dateTime: LocalDate
        get() = LocalDate.parse(date, DateTimeFormatter.ISO_DATE)

    /**
     * Get formatted date in Indonesian format
     */
    val formattedDate: String
        get() {
            val dt = dateTime
            val months = listOf(
                "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
                "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
            )
            return "${dt.dayOfMonth} ${months[dt.monthValue - 1]} ${dt.year}"
        }
}

/**
 * Holiday list response from API.
 */
data class HolidayResponse(
    val year: Int,
    @SerializedName("employee_type")
    val employeeType: String = "office",
    @SerializedName("total_holidays")
    val totalHolidays: Int = 0,
    @SerializedName("mandatory_holidays")
    val mandatoryHolidays: Int = 0,
    @SerializedName("optional_holidays")
    val optionalHolidays: Int = 0,
    val holidays: List<Holiday> = emptyList()
)

/**
 * Upcoming holiday response.
 */
data class UpcomingHoliday(
    val id: Int,
    val date: String,
    @SerializedName("day_label")
    val dayLabel: String = "",
    val name: String,
    @SerializedName("is_optional")
    val isOptional: Boolean = false,
    @SerializedName("days_until")
    val daysUntil: Int = 0
)

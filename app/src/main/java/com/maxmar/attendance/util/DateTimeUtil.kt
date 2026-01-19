package com.maxmar.attendance.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

/**
 * Utility object for formatting dates and times.
 * Handles ISO 8601 strings from the API and converts to local timezone.
 */
object DateTimeUtil {
    
    private val localTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())
    private val localDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
    private val localDateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.getDefault())
    private val customDateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale.getDefault())
    
    /**
     * Format ISO 8601 string to local time (HH:mm:ss).
     * Example: "2026-01-19T03:15:53+00:00" -> "10:15:53" (in UTC+7)
     */
    fun formatToLocalTime(iso8601String: String?): String {
        if (iso8601String.isNullOrEmpty()) return "-"
        
        return try {
            val zonedDateTime = ZonedDateTime.parse(iso8601String)
            val localDateTime = zonedDateTime.withZoneSameInstant(java.time.ZoneId.systemDefault())
            localDateTime.format(localTimeFormatter)
        } catch (e: DateTimeParseException) {
            // Fallback: return as-is if it's already a simple time string
            iso8601String
        }
    }
    
    /**
     * Format ISO 8601 string to local date (dd MMM yyyy).
     */
    fun formatToLocalDate(iso8601String: String?): String {
        if (iso8601String.isNullOrEmpty()) return "-"
        
        return try {
            val zonedDateTime = ZonedDateTime.parse(iso8601String)
            val localDateTime = zonedDateTime.withZoneSameInstant(java.time.ZoneId.systemDefault())
            localDateTime.format(localDateFormatter)
        } catch (e: DateTimeParseException) {
            iso8601String
        }
    }
    
    /**
     * Format ISO 8601 string to local date and time (dd MMM yyyy HH:mm).
     */
    fun formatToLocalDateTime(iso8601String: String?): String {
        if (iso8601String.isNullOrEmpty()) return "-"
        
        return try {
            val zonedDateTime = ZonedDateTime.parse(iso8601String)
            val localDateTime = zonedDateTime.withZoneSameInstant(java.time.ZoneId.systemDefault())
            localDateTime.format(localDateTimeFormatter)
        } catch (e: DateTimeParseException) {
            iso8601String
        }
    }

    /**
     * Format ISO 8601 string to "dd-MM-yyyy HH:mm".
     * Example: "2026-01-19T03:12:30+00:00" -> "19-01-2026 10:12"
     */
    fun formatToDDMMYYYYHHmm(iso8601String: String?): String {
        if (iso8601String.isNullOrEmpty()) return "--:--"
        
        return try {
            val zonedDateTime = ZonedDateTime.parse(iso8601String)
            val localDateTime = zonedDateTime.withZoneSameInstant(java.time.ZoneId.systemDefault())
            localDateTime.format(customDateTimeFormatter)
        } catch (e: DateTimeParseException) {
            iso8601String
        }
    }
}

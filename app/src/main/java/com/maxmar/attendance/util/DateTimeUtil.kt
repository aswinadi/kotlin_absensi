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
    private val shortTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())
    
    /**
     * Format ISO 8601 string to "dd-MM-yyyy".
     */
    fun formatToDDMMYYYY(isoString: String?): String {
        if (isoString.isNullOrEmpty()) return "--- ---"
        return try {
            // Remove 'Z' if it's there to try parsing as LocalDateTime if ZonedDateTime fails
            val cleaned = isoString.replace("Z", "+00:00")
            val zonedDateTime = if (cleaned.contains("+") || cleaned.contains("-", ignoreCase = false) && cleaned.lastIndexOf("-") > cleaned.lastIndexOf("T")) {
                ZonedDateTime.parse(cleaned)
            } else {
                // Try as Instant or specify UTC
                ZonedDateTime.parse(cleaned + "Z")
            }
            val localDateTime = zonedDateTime.withZoneSameInstant(java.time.ZoneId.systemDefault())
            localDateTime.format(dateFormatter)
        } catch (e: Exception) {
            // Fallback: try simple substring if it's YYYY-MM-DD...
            if (isoString!!.length >= 10 && isoString[4] == '-' && isoString[7] == '-') {
                val parts = isoString.substring(0, 10).split("-")
                if (parts.size == 3) {
                    return "${parts[2]}-${parts[1]}-${parts[0]}"
                }
            }
            isoString
        }
    }

    /**
     * Format ISO 8601 string to local time (HH:mm).
     * Example: "2026-01-19T03:15:53+00:00" -> "10:15" (in UTC+7)
     */
    fun formatToHHmm(iso8601String: String?): String {
        if (iso8601String.isNullOrEmpty()) return "--:--"
        
        return try {
            val zonedDateTime = when {
                !iso8601String.contains("T") -> return iso8601String.take(5)
                iso8601String.endsWith("Z") -> ZonedDateTime.parse(iso8601String)
                iso8601String.contains("+") || (iso8601String.lastIndexOf("-") > iso8601String.lastIndexOf("T")) -> ZonedDateTime.parse(iso8601String)
                else -> ZonedDateTime.parse(iso8601String + "Z") // Assume UTC if no TZ info
            }
            val localDateTime = zonedDateTime.withZoneSameInstant(java.time.ZoneId.systemDefault())
            localDateTime.format(shortTimeFormatter)
        } catch (e: Exception) {
            iso8601String.substringAfter("T").take(5)
        }
    }
    
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

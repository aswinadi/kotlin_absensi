package com.maxmar.attendance.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Utility functions for time zone conversions.
 */
object TimeUtils {
    
    private const val TIME_FORMAT = "HH:mm:ss"
    private const val LOCAL_TIMEZONE = "Asia/Jakarta"
    
    /**
     * Convert a UTC time string (HH:mm:ss) to local timezone (Asia/Jakarta).
     * @param utcTime Time string in HH:mm:ss format (UTC)
     * @return Time string in HH:mm:ss format (local timezone)
     */
    fun convertUtcToLocal(utcTime: String?): String? {
        if (utcTime.isNullOrEmpty()) return null
        
        return try {
            val utcFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            
            val localFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            localFormat.timeZone = TimeZone.getTimeZone(LOCAL_TIMEZONE)
            
            val date = utcFormat.parse(utcTime)
            if (date != null) {
                localFormat.format(date)
            } else {
                utcTime
            }
        } catch (e: Exception) {
            utcTime // Return original if parsing fails
        }
    }
    
    /**
     * Convert a UTC time string (HH:mm:ss) to short local time (HH:mm).
     * @param utcTime Time string in HH:mm:ss format (UTC)
     * @return Time string in HH:mm format (local timezone)
     */
    fun convertUtcToLocalShort(utcTime: String?): String? {
        val localTime = convertUtcToLocal(utcTime)
        return localTime?.take(5) // Take just HH:mm
    }
}

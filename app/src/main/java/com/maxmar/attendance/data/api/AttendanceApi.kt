package com.maxmar.attendance.data.api

import com.maxmar.attendance.data.model.AttendanceHistoryResponse
import com.maxmar.attendance.data.model.AttendanceSummaryResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API interface for attendance endpoints.
 */
interface AttendanceApi {
    
    @GET(ApiEndpoints.ATTENDANCE_HISTORY)
    suspend fun getHistory(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("page") page: Int = 1
    ): AttendanceHistoryResponse
    
    @GET(ApiEndpoints.ATTENDANCE_SUMMARY)
    suspend fun getSummary(
        @Query("year") year: Int? = null,
        @Query("month") month: Int? = null
    ): AttendanceSummaryResponse
}

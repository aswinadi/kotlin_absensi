package com.maxmar.attendance.data.api

import com.maxmar.attendance.data.model.ApiResponse
import com.maxmar.attendance.data.model.ScheduleResponse
import com.maxmar.attendance.data.model.TodayShiftResponse
import retrofit2.http.GET

/**
 * Retrofit API interface for schedule-related endpoints.
 */
interface ScheduleApi {
    
    @GET(ApiEndpoints.MY_SCHEDULE)
    suspend fun getMySchedule(): ApiResponse<ScheduleResponse>
    
    @GET(ApiEndpoints.TODAY_SHIFT)
    suspend fun getTodayShift(): ApiResponse<TodayShiftResponse>
}

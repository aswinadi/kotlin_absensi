package com.maxmar.attendance.data.api

import com.maxmar.attendance.data.model.AttendanceHistoryResponse
import com.maxmar.attendance.data.model.AttendanceSummaryResponse
import com.maxmar.attendance.data.model.CheckInOutResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
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
    
    @FormUrlEncoded
    @POST(ApiEndpoints.CHECK_IN)
    suspend fun checkIn(
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double,
        @Field("timestamp") timestamp: String? = null
    ): CheckInOutResponse
    
    @FormUrlEncoded
    @POST(ApiEndpoints.CHECK_OUT)
    suspend fun checkOut(
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double,
        @Field("timestamp") timestamp: String? = null
    ): CheckInOutResponse
}

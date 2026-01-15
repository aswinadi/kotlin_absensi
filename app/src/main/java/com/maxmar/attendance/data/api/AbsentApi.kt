package com.maxmar.attendance.data.api

import com.maxmar.attendance.data.model.AbsentAttendanceListResponse
import com.maxmar.attendance.data.model.AbsentAttendanceResponse
import com.maxmar.attendance.data.model.AbsentTypesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/**
 * Retrofit API interface for absent attendance endpoints.
 */
interface AbsentApi {
    
    @GET(ApiEndpoints.ABSENT_TYPES)
    suspend fun getAbsentTypes(): AbsentTypesResponse
    
    @GET(ApiEndpoints.ABSENT_ATTENDANCE)
    suspend fun getAbsentAttendances(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("year") year: Int? = null,
        @Query("month") month: Int? = null,
        @Query("page") page: Int = 1
    ): AbsentAttendanceListResponse
    
    @Multipart
    @POST(ApiEndpoints.ABSENT_ATTENDANCE)
    suspend fun createAbsentAttendance(
        @Part("absent_type_id") absentTypeId: RequestBody,
        @Part("absent_date") absentDate: RequestBody,
        @Part("notes") notes: RequestBody?,
        @Part attachment: MultipartBody.Part? = null
    ): AbsentAttendanceResponse
}

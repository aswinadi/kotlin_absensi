package com.maxmar.attendance.data.api

import com.maxmar.attendance.data.model.FieldAttendanceDetailResponse
import com.maxmar.attendance.data.model.FieldAttendanceListResponse
import com.maxmar.attendance.data.model.FieldAttendanceResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for Field Attendance (Dinas Luar) endpoints.
 */
interface FieldAttendanceApi {

    @GET(ApiEndpoints.FIELD_ATTENDANCES)
    suspend fun getList(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("page") page: Int = 1
    ): FieldAttendanceListResponse

    @GET("${ApiEndpoints.FIELD_ATTENDANCES}/{id}")
    suspend fun getDetail(
        @Path("id") id: Int
    ): FieldAttendanceDetailResponse

    @Multipart
    @POST(ApiEndpoints.FIELD_ATTENDANCES)
    suspend fun create(
        @Part("date") date: RequestBody,
        @Part("location_name") locationName: RequestBody,
        @Part("purpose") purpose: RequestBody,
        @Part("arrival_latitude") arrivalLatitude: RequestBody,
        @Part("arrival_longitude") arrivalLongitude: RequestBody,
        @Part arrivalPhoto: MultipartBody.Part
    ): FieldAttendanceResponse

    @Multipart
    @POST("${ApiEndpoints.FIELD_ATTENDANCES}/{id}/departure")
    suspend fun recordDeparture(
        @Path("id") id: Int,
        @Part("departure_latitude") departureLatitude: RequestBody,
        @Part("departure_longitude") departureLongitude: RequestBody,
        @Part departurePhoto: MultipartBody.Part
    ): FieldAttendanceResponse
}

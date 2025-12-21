package com.maxmar.attendance.data.api

import com.maxmar.attendance.data.model.BusinessTripDetailResponse
import com.maxmar.attendance.data.model.BusinessTripListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for business trip endpoints.
 */
interface BusinessTripApi {
    
    @GET(ApiEndpoints.BUSINESS_TRIPS)
    suspend fun getBusinessTrips(
        @Query("page") page: Int = 1,
        @Query("status") status: String? = null
    ): BusinessTripListResponse
    
    @GET("business-trips/{id}")
    suspend fun getBusinessTripDetail(
        @Path("id") id: Int
    ): BusinessTripDetailResponse
}

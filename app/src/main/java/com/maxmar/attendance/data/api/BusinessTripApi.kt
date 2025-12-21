package com.maxmar.attendance.data.api

import com.maxmar.attendance.data.model.BusinessTripDetailResponse
import com.maxmar.attendance.data.model.BusinessTripListResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
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
    
    @FormUrlEncoded
    @POST(ApiEndpoints.BUSINESS_TRIPS)
    suspend fun createBusinessTrip(
        @Field("purpose") purpose: String,
        @Field("location") location: String,
        @Field("destination_city") destinationCity: String?,
        @Field("departure_date") departureDate: String,
        @Field("arrival_date") arrivalDate: String,
        @Field("notes") notes: String?
    ): BusinessTripDetailResponse
}


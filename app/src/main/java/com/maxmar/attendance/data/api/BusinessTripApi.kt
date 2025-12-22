package com.maxmar.attendance.data.api

import com.maxmar.attendance.data.model.AllowanceResponse
import com.maxmar.attendance.data.model.AssignableUsersResponse
import com.maxmar.attendance.data.model.BusinessTripDetailResponse
import com.maxmar.attendance.data.model.BusinessTripListResponse
import com.maxmar.attendance.data.model.MasterDataResponse
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
    
    // Master data endpoints
    @GET("business-trip-purposes")
    suspend fun getPurposes(): MasterDataResponse
    
    @GET("business-trip-destinations")
    suspend fun getDestinations(): MasterDataResponse
    
    @GET("business-trips/assignable-users")
    suspend fun getAssignableUsers(): AssignableUsersResponse
    
    @GET("business-trips/allowance")
    suspend fun getEmployeeAllowance(
        @Query("destination_type") destinationType: String
    ): AllowanceResponse
    
    @FormUrlEncoded
    @POST(ApiEndpoints.BUSINESS_TRIPS)
    suspend fun createBusinessTrip(
        @Field("business_trip_purpose_id") purposeId: Int,
        @Field("location") location: String,
        @Field("business_trip_destination_id") destinationId: Int,
        @Field("destination_city") destinationCity: String?,
        @Field("departure_date") departureDate: String,
        @Field("departure_time") departureTime: String?,
        @Field("arrival_date") arrivalDate: String,
        @Field("arrival_time") arrivalTime: String?,
        @Field("assigned_by") assignedBy: Int?,
        @Field("notes") notes: String?
    ): BusinessTripDetailResponse
}




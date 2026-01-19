package com.maxmar.attendance.data.api

import com.maxmar.attendance.data.model.AllowanceResponse
import com.maxmar.attendance.data.model.AssignableUsersResponse
import com.maxmar.attendance.data.model.BusinessTripDetailResponse
import com.maxmar.attendance.data.model.BusinessTripListResponse
import com.maxmar.attendance.data.model.MasterDataResponse
import com.maxmar.attendance.data.model.RealizationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
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
    
    @GET("att/business-trips/{id}")
    suspend fun getBusinessTripDetail(
        @Path("id") id: Int
    ): BusinessTripDetailResponse
    
    // Master data endpoints
    @GET(ApiEndpoints.BUSINESS_TRIP_PURPOSES)
    suspend fun getPurposes(): MasterDataResponse
    
    @GET(ApiEndpoints.BUSINESS_TRIP_DESTINATIONS)
    suspend fun getDestinations(): MasterDataResponse
    
    @GET(ApiEndpoints.BUSINESS_TRIP_ASSIGNABLE_USERS)
    suspend fun getAssignableUsers(): AssignableUsersResponse
    
    @GET(ApiEndpoints.BUSINESS_TRIP_ALLOWANCE)
    suspend fun getEmployeeAllowance(
        @Query("destination_id") destinationId: Int
    ): AllowanceResponse
    
    @FormUrlEncoded
    @POST(ApiEndpoints.BUSINESS_TRIPS)
    suspend fun createBusinessTrip(
        @Field("business_trip_purpose_id") purposeId: Int,
        @Field("location") location: String,
        @Field("business_trip_destination_id") destinationId: Int,
        @Field("destination_city") destinationCity: String,
        @Field("departure_date") departureDate: String,
        @Field("departure_time") departureTime: String?,
        @Field("arrival_date") arrivalDate: String,
        @Field("arrival_time") arrivalTime: String?,
        @Field("assigned_by") assignedBy: Int,
        @Field("cash_advance") cashAdvance: Double?,
        @Field("notes") notes: String?
    ): BusinessTripDetailResponse
    
    // Realization endpoints
    @GET("att/business-trips/pending-realization")
    suspend fun getTripsNeedingRealization(): BusinessTripListResponse
    
    @GET("att/business-trips/{tripId}/realization")
    suspend fun getRealization(
        @Path("tripId") tripId: Int
    ): RealizationResponse
    
    @Multipart
    @POST("att/business-trips/{tripId}/realization")
    suspend fun createRealization(
        @Path("tripId") tripId: Int,
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part documents: List<MultipartBody.Part>?
    ): RealizationResponse
    
    @Multipart
    @PUT("att/business-trips/{tripId}/realization")
    suspend fun updateRealization(
        @Path("tripId") tripId: Int,
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part documents: List<MultipartBody.Part>?
    ): RealizationResponse
    
    @DELETE("att/business-trips/{tripId}/realization/documents/{documentId}")
    suspend fun deleteRealizationDocument(
        @Path("tripId") tripId: Int,
        @Path("documentId") documentId: Int
    ): RealizationResponse
}

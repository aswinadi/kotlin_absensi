package com.maxmar.attendance.data.repository

import com.maxmar.attendance.data.api.BusinessTripApi
import com.maxmar.attendance.data.model.BusinessTrip
import com.maxmar.attendance.data.model.PaginationMeta
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result wrapper for business trip list.
 */
data class BusinessTripListResult(
    val trips: List<BusinessTrip>,
    val meta: PaginationMeta?
)

/**
 * Repository for business trip operations.
 */
@Singleton
class BusinessTripRepository @Inject constructor(
    private val businessTripApi: BusinessTripApi
) {
    
    /**
     * Fetch business trips with optional status filter.
     */
    suspend fun fetchBusinessTrips(
        page: Int = 1,
        status: String? = null
    ): AuthResult<BusinessTripListResult> {
        return try {
            val response = businessTripApi.getBusinessTrips(page, status)
            AuthResult.Success(
                BusinessTripListResult(
                    trips = response.data,
                    meta = response.meta
                )
            )
        } catch (e: retrofit2.HttpException) {
            AuthResult.Error("Error: ${e.code()}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Fetch business trip detail by ID.
     */
    suspend fun fetchBusinessTripDetail(id: Int): AuthResult<BusinessTrip> {
        return try {
            val response = businessTripApi.getBusinessTripDetail(id)
            AuthResult.Success(response.data)
        } catch (e: retrofit2.HttpException) {
            AuthResult.Error("Error: ${e.code()}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Create a new business trip.
     */
    suspend fun createBusinessTrip(
        purpose: String,
        location: String,
        destinationCity: String?,
        departureDate: String,
        arrivalDate: String,
        notes: String?
    ): AuthResult<BusinessTrip> {
        return try {
            val response = businessTripApi.createBusinessTrip(
                purpose = purpose,
                location = location,
                destinationCity = destinationCity,
                departureDate = departureDate,
                arrivalDate = arrivalDate,
                notes = notes
            )
            AuthResult.Success(response.data)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            AuthResult.Error("Error: ${e.code()} - $errorBody")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
}


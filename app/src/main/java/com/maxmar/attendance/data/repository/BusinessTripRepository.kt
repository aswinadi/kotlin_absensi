package com.maxmar.attendance.data.repository

import com.maxmar.attendance.data.api.BusinessTripApi
import com.maxmar.attendance.data.model.AllowanceData
import com.maxmar.attendance.data.model.AssignableUser
import com.maxmar.attendance.data.model.BusinessTrip
import com.maxmar.attendance.data.model.BusinessTripRealization
import com.maxmar.attendance.data.model.MasterDataItem
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
     * Fetch business trip purposes.
     */
    suspend fun fetchPurposes(): AuthResult<List<MasterDataItem>> {
        return try {
            val response = businessTripApi.getPurposes()
            AuthResult.Success(response.data)
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Fetch business trip destinations.
     */
    suspend fun fetchDestinations(): AuthResult<List<MasterDataItem>> {
        return try {
            val response = businessTripApi.getDestinations()
            AuthResult.Success(response.data)
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Fetch assignable users for dropdown.
     */
    suspend fun fetchAssignableUsers(): AuthResult<List<AssignableUser>> {
        return try {
            val response = businessTripApi.getAssignableUsers()
            AuthResult.Success(response.data)
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Fetch allowance based on destination ID.
     */
    suspend fun fetchAllowance(destinationId: Int): AuthResult<AllowanceData?> {
        return try {
            val response = businessTripApi.getEmployeeAllowance(destinationId)
            AuthResult.Success(response.data)
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
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
        purposeId: Int,
        location: String,
        destinationId: Int,
        destinationCity: String,
        departureDate: String,
        departureTime: String?,
        arrivalDate: String,
        arrivalTime: String?,
        assignedBy: Int,
        cashAdvance: Double?,
        notes: String?
    ): AuthResult<BusinessTrip> {
        return try {
            val response = businessTripApi.createBusinessTrip(
                purposeId = purposeId,
                location = location,
                destinationId = destinationId,
                destinationCity = destinationCity,
                departureDate = departureDate,
                departureTime = departureTime,
                arrivalDate = arrivalDate,
                arrivalTime = arrivalTime,
                assignedBy = assignedBy,
                cashAdvance = cashAdvance,
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
    
    /**
     * Get trips that need realization (approved + past end date + no realization).
     */
    suspend fun fetchTripsNeedingRealization(): AuthResult<List<BusinessTrip>> {
        return try {
            val response = businessTripApi.getTripsNeedingRealization()
            AuthResult.Success(response.data)
        } catch (e: retrofit2.HttpException) {
            AuthResult.Error("Error: ${e.code()}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Get realization for a trip.
     */
    suspend fun fetchRealization(tripId: Int): AuthResult<BusinessTripRealization?> {
        return try {
            val response = businessTripApi.getRealization(tripId)
            if (response.success) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error(response.message ?: "Realisasi tidak ditemukan")
            }
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) {
                AuthResult.Success(null)
            } else {
                AuthResult.Error("Error: ${e.code()}")
            }
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Create realization for a trip.
     */
    suspend fun createRealization(
        tripId: Int,
        data: Map<String, okhttp3.RequestBody>,
        documents: List<okhttp3.MultipartBody.Part>?
    ): AuthResult<BusinessTripRealization> {
        return try {
            val response = businessTripApi.createRealization(tripId, data, documents)
            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error(response.message ?: "Gagal membuat realisasi")
            }
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            AuthResult.Error("Error: ${e.code()} - $errorBody")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Update realization for a trip.
     */
    suspend fun updateRealization(
        tripId: Int,
        data: Map<String, okhttp3.RequestBody>,
        documents: List<okhttp3.MultipartBody.Part>?
    ): AuthResult<BusinessTripRealization> {
        return try {
            val response = businessTripApi.updateRealization(tripId, data, documents)
            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error(response.message ?: "Gagal mengupdate realisasi")
            }
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            AuthResult.Error("Error: ${e.code()} - $errorBody")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Delete a document from realization.
     */
    suspend fun deleteRealizationDocument(tripId: Int, documentId: Int): AuthResult<Unit> {
        return try {
            val response = businessTripApi.deleteRealizationDocument(tripId, documentId)
            if (response.success) {
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(response.message ?: "Gagal menghapus dokumen")
            }
        } catch (e: retrofit2.HttpException) {
            AuthResult.Error("Error: ${e.code()}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
}

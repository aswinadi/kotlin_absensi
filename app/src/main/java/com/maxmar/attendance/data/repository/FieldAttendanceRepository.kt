package com.maxmar.attendance.data.repository

import com.maxmar.attendance.data.api.FieldAttendanceApi
import com.maxmar.attendance.data.model.FieldAttendance
import com.maxmar.attendance.data.model.PaginationMeta
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result wrapper for field attendance list.
 */
data class FieldAttendanceListResult(
    val fieldAttendances: List<FieldAttendance>,
    val meta: PaginationMeta?
)

/**
 * Repository for Field Attendance (Dinas Luar) operations.
 */
@Singleton
class FieldAttendanceRepository @Inject constructor(
    private val fieldAttendanceApi: FieldAttendanceApi
) {

    /**
     * Fetch field attendance list with optional date filter.
     */
    suspend fun fetchList(
        startDate: String? = null,
        endDate: String? = null,
        page: Int = 1
    ): AuthResult<FieldAttendanceListResult> {
        return try {
            val response = fieldAttendanceApi.getList(startDate, endDate, page)
            if (response.success) {
                AuthResult.Success(
                    FieldAttendanceListResult(
                        fieldAttendances = response.data,
                        meta = response.meta
                    )
                )
            } else {
                AuthResult.Error("Gagal memuat data dinas luar")
            }
        } catch (e: retrofit2.HttpException) {
            AuthResult.Error("Error: ${e.code()}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }

    /**
     * Fetch field attendance detail.
     */
    suspend fun fetchDetail(id: Int): AuthResult<FieldAttendance> {
        return try {
            val response = fieldAttendanceApi.getDetail(id)
            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error(response.message ?: "Data tidak ditemukan")
            }
        } catch (e: retrofit2.HttpException) {
            AuthResult.Error("Error: ${e.code()}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }

    /**
     * Create new field attendance with arrival data.
     */
    suspend fun create(
        date: String,
        locationName: String,
        purpose: String,
        arrivalLatitude: Double,
        arrivalLongitude: Double,
        photoFile: File
    ): AuthResult<FieldAttendance> {
        return try {
            val dateBody = date.toRequestBody("text/plain".toMediaTypeOrNull())
            val locationNameBody = locationName.toRequestBody("text/plain".toMediaTypeOrNull())
            val purposeBody = purpose.toRequestBody("text/plain".toMediaTypeOrNull())
            val latitudeBody = arrivalLatitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val longitudeBody = arrivalLongitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val requestFile = photoFile.asRequestBody("image/*".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("arrival_photo", photoFile.name, requestFile)

            val response = fieldAttendanceApi.create(
                date = dateBody,
                locationName = locationNameBody,
                purpose = purposeBody,
                arrivalLatitude = latitudeBody,
                arrivalLongitude = longitudeBody,
                arrivalPhoto = photoPart
            )

            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error(response.message ?: "Gagal mencatat dinas luar")
            }
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val message = try {
                val json = com.google.gson.Gson().fromJson(errorBody, Map::class.java)
                json["message"]?.toString() ?: "Error: ${e.code()}"
            } catch (_: Exception) {
                "Error: ${e.code()}"
            }
            AuthResult.Error(message)
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }

    /**
     * Record departure for a field attendance.
     */
    suspend fun recordDeparture(
        id: Int,
        departureLatitude: Double,
        departureLongitude: Double,
        photoFile: File
    ): AuthResult<FieldAttendance> {
        return try {
            val latitudeBody = departureLatitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val longitudeBody = departureLongitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val requestFile = photoFile.asRequestBody("image/*".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("departure_photo", photoFile.name, requestFile)

            val response = fieldAttendanceApi.recordDeparture(
                id = id,
                departureLatitude = latitudeBody,
                departureLongitude = longitudeBody,
                departurePhoto = photoPart
            )

            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error(response.message ?: "Gagal mencatat kepulangan")
            }
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val message = try {
                val json = com.google.gson.Gson().fromJson(errorBody, Map::class.java)
                json["message"]?.toString() ?: "Error: ${e.code()}"
            } catch (_: Exception) {
                "Error: ${e.code()}"
            }
            AuthResult.Error(message)
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
}

package com.maxmar.attendance.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.maxmar.attendance.data.api.FieldAttendanceApi
import com.maxmar.attendance.data.model.FieldAttendance
import com.maxmar.attendance.data.model.PaginationMeta
import com.maxmar.attendance.util.DateTimeUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
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
    
    companion object {
        private const val MAX_IMAGE_WIDTH = 1280
        private const val MAX_IMAGE_HEIGHT = 960
        private const val COMPRESSION_QUALITY = 80
    }

    /**
     * Compress and resize image to reduce file size.
     * Max dimensions: 1280x960, quality: 80%
     */
    private fun compressImage(file: File): File {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.absolutePath, options)
            
            // Calculate sample size
            var sampleSize = 1
            while (options.outWidth / sampleSize > MAX_IMAGE_WIDTH || 
                   options.outHeight / sampleSize > MAX_IMAGE_HEIGHT) {
                sampleSize *= 2
            }
            
            // Decode with sample size
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            val bitmap = BitmapFactory.decodeFile(file.absolutePath, decodeOptions)
                ?: return file
            
            // Create compressed file
            val compressedFile = File(file.parent, "compressed_${file.name}")
            FileOutputStream(compressedFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, out)
            }
            bitmap.recycle()
            
            compressedFile
        } catch (e: Exception) {
            // Return original file if compression fails
            file
        }
    }

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

            // Compress image before upload
            val compressedFile = compressImage(photoFile)
            val requestFile = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("arrival_photo", compressedFile.name, requestFile)

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

            // Compress image before upload
            val compressedFile = compressImage(photoFile)
            val requestFile = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("departure_photo", compressedFile.name, requestFile)

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

    /**
     * Fetch team field attendances for supervisors.
     * Returns field attendances from subordinates.
     */
    suspend fun fetchTeamFieldAttendances(
        filter: String = "today"
    ): AuthResult<List<com.maxmar.attendance.ui.screens.fieldattendance.TeamFieldAttendanceItem>> {
        return try {
            val response = fieldAttendanceApi.getTeamList(filter)
            if (response.success) {
                val items = response.data.map { fa ->
                    com.maxmar.attendance.ui.screens.fieldattendance.TeamFieldAttendanceItem(
                        id = fa.id,
                        employeeId = fa.employee?.id ?: 0,
                        employeeName = fa.employee?.fullName ?: "Unknown",
                        employeeCode = fa.employee?.employeeCode,
                        position = fa.employee?.position,
                        date = fa.date,
                        locationName = fa.locationName,
                        purpose = fa.purpose,
                        arrivalTime = DateTimeUtil.formatToHHmm(fa.arrivalTime),
                        departureTime = DateTimeUtil.formatToHHmm(fa.departureTime),
                        arrivalPhotoUrl = fa.arrivalPhotoUrl,
                        departurePhotoUrl = fa.departurePhotoUrl,
                        hasArrived = fa.arrivalTime != null,
                        hasDeparted = fa.departureTime != null
                    )
                }
                AuthResult.Success(items)
            } else {
                AuthResult.Error("Gagal memuat data tim")
            }
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 403) {
                AuthResult.Error("Anda tidak memiliki akses ke fitur ini")
            } else {
                AuthResult.Error("Error: ${e.code()}")
            }
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }

    private fun formatTime(isoTime: String): String {
        return try {
            // Parse ISO 8601 and return just the time part HH:mm
            val parsed = java.time.OffsetDateTime.parse(isoTime)
            parsed.toLocalTime().toString().take(5)
        } catch (e: Exception) {
            isoTime.take(5)
        }
    }
}

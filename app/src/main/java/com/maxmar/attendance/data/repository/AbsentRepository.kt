package com.maxmar.attendance.data.repository

import com.maxmar.attendance.data.api.AbsentApi
import com.maxmar.attendance.data.model.AbsentAttendance
import com.maxmar.attendance.data.model.AbsentAttendanceResult
import com.maxmar.attendance.data.model.AbsentType
import com.maxmar.attendance.data.model.PaginationMeta
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result wrapper for absent attendance list.
 */
data class AbsentAttendanceListResult(
    val absents: List<AbsentAttendance>,
    val meta: PaginationMeta?
)

/**
 * Repository for absent attendance operations.
 */
@Singleton
class AbsentRepository @Inject constructor(
    private val absentApi: AbsentApi
) {
    
    /**
     * Fetch list of absent types.
     */
    suspend fun fetchAbsentTypes(): AuthResult<List<AbsentType>> {
        return try {
            val response = absentApi.getAbsentTypes()
            if (response.success) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error("Gagal mengambil jenis ketidakhadiran")
            }
        } catch (e: retrofit2.HttpException) {
            AuthResult.Error("Error: ${e.code()}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Fetch absent attendance history.
     */
    suspend fun fetchAbsentHistory(
        startDate: String? = null,
        endDate: String? = null,
        page: Int = 1
    ): AuthResult<AbsentAttendanceListResult> {
        return try {
            val response = absentApi.getAbsentAttendances(startDate, endDate, page)
            if (response.success) {
                AuthResult.Success(
                    AbsentAttendanceListResult(
                        absents = response.data,
                        meta = response.meta
                    )
                )
            } else {
                AuthResult.Error("Gagal mengambil riwayat ketidakhadiran")
            }
        } catch (e: retrofit2.HttpException) {
            AuthResult.Error("Error: ${e.code()}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Fetch absent attendance history by month.
     */
    suspend fun fetchAbsentsByMonth(
        year: Int,
        month: Int,
        page: Int = 1
    ): AuthResult<AbsentAttendanceListResult> {
        return try {
            val response = absentApi.getAbsentAttendances(
                year = year,
                month = month,
                page = page
            )
            if (response.success) {
                AuthResult.Success(
                    AbsentAttendanceListResult(
                        absents = response.data,
                        meta = response.meta
                    )
                )
            } else {
                AuthResult.Error("Gagal mengambil riwayat ketidakhadiran")
            }
        } catch (e: retrofit2.HttpException) {
            AuthResult.Error("Error: ${e.code()}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Create absent attendance request.
     */
    suspend fun createAbsentAttendance(
        absentTypeId: Int,
        absentDate: String,
        notes: String?,
        attachmentFile: File? = null
    ): AuthResult<AbsentAttendanceResult> {
        return try {
            val typeIdBody = absentTypeId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val dateBody = absentDate.toRequestBody("text/plain".toMediaTypeOrNull())
            val notesBody = notes?.toRequestBody("text/plain".toMediaTypeOrNull())
            
            val attachmentPart = attachmentFile?.let { file ->
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("attachment", file.name, requestFile)
            }
            
            val response = absentApi.createAbsentAttendance(
                absentTypeId = typeIdBody,
                absentDate = dateBody,
                notes = notesBody,
                attachment = attachmentPart
            )
            
            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error(response.message ?: "Gagal membuat pengajuan")
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

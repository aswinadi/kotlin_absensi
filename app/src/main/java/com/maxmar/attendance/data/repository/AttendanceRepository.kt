package com.maxmar.attendance.data.repository

import com.maxmar.attendance.data.api.AttendanceApi
import com.maxmar.attendance.data.model.Attendance
import com.maxmar.attendance.data.model.AttendanceSummary
import com.maxmar.attendance.data.model.CheckInOutResult
import com.maxmar.attendance.data.model.PaginationMeta
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result wrapper for attendance history.
 */
data class AttendanceHistoryResult(
    val attendances: List<Attendance>,
    val meta: PaginationMeta?
)

/**
 * Repository for attendance operations.
 */
@Singleton
class AttendanceRepository @Inject constructor(
    private val attendanceApi: AttendanceApi
) {
    
    /**
     * Fetch attendance history with optional date filter.
     */
    suspend fun fetchHistory(
        startDate: String? = null,
        endDate: String? = null,
        page: Int = 1
    ): AuthResult<AttendanceHistoryResult> {
        return try {
            val response = attendanceApi.getHistory(startDate, endDate, page)
            AuthResult.Success(
                AttendanceHistoryResult(
                    attendances = response.data,
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
     * Fetch monthly attendance summary.
     */
    suspend fun fetchSummary(
        year: Int? = null,
        month: Int? = null
    ): AuthResult<AttendanceSummary> {
        return try {
            val response = attendanceApi.getSummary(year, month)
            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error("Data tidak ditemukan")
            }
        } catch (e: retrofit2.HttpException) {
            AuthResult.Error("Error: ${e.code()}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Check in with location.
     */
    suspend fun checkIn(
        latitude: Double,
        longitude: Double
    ): AuthResult<CheckInOutResult> {
        return try {
            val response = attendanceApi.checkIn(latitude, longitude)
            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error(response.message ?: "Check-in gagal")
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
     * Check out with location.
     */
    suspend fun checkOut(
        latitude: Double,
        longitude: Double
    ): AuthResult<CheckInOutResult> {
        return try {
            val response = attendanceApi.checkOut(latitude, longitude)
            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error(response.message ?: "Check-out gagal")
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

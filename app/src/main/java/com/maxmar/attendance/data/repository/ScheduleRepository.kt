package com.maxmar.attendance.data.repository

import com.maxmar.attendance.data.api.ScheduleApi
import com.maxmar.attendance.data.model.ScheduleResponse
import com.maxmar.attendance.data.model.TodayShiftResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for schedule-related operations.
 */
@Singleton
class ScheduleRepository @Inject constructor(
    private val scheduleApi: ScheduleApi
) {
    
    /**
     * Fetch full weekly schedule.
     */
    suspend fun fetchMySchedule(): AuthResult<ScheduleResponse> {
        return try {
            val response = scheduleApi.getMySchedule()
            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error(response.message ?: "Gagal memuat jadwal")
            }
        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                404 -> AuthResult.Error("Jadwal tidak ditemukan untuk departemen Anda")
                else -> AuthResult.Error("Error: ${e.code()}")
            }
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Fetch today's shift schedule.
     */
    suspend fun fetchTodayShift(): AuthResult<TodayShiftResponse> {
        return try {
            val response = scheduleApi.getTodayShift()
            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                // No schedule found - not necessarily an error
                AuthResult.Success(TodayShiftResponse(
                    isWorkday = false,
                    message = "Jadwal tidak ditemukan"
                ))
            }
        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                404 -> AuthResult.Success(TodayShiftResponse(
                    isWorkday = false,
                    message = "Jadwal tidak ditemukan"
                ))
                else -> AuthResult.Error("Error: ${e.code()}")
            }
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
}

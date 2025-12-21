package com.maxmar.attendance.data.repository

import com.maxmar.attendance.data.api.EmployeeApi
import com.maxmar.attendance.data.model.Employee
import com.maxmar.attendance.data.model.EmployeeProfileData
import com.maxmar.attendance.data.model.TodayShiftResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for employee-related operations.
 */
@Singleton
class EmployeeRepository @Inject constructor(
    private val employeeApi: EmployeeApi
) {
    
    /**
     * Fetch employee profile.
     */
    suspend fun fetchProfile(): AuthResult<Employee> {
        return try {
            val response = employeeApi.getProfile()
            if (response.success && response.data != null) {
                AuthResult.Success(response.data.employee)
            } else {
                AuthResult.Error(response.message ?: "Gagal mengambil profil")
            }
        } catch (e: retrofit2.HttpException) {
            AuthResult.Error("Error: ${e.code()}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Fetch full employee profile with schedule and leave quota.
     */
    suspend fun fetchFullProfile(): AuthResult<EmployeeProfileData> {
        return try {
            val response = employeeApi.getProfile()
            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error(response.message ?: "Gagal mengambil profil")
            }
        } catch (e: retrofit2.HttpException) {
            AuthResult.Error("Error: ${e.code()}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Fetch today's shift schedule.
     */
    suspend fun fetchTodayShift(): AuthResult<TodayShiftResponse> {
        return try {
            val response = employeeApi.getTodayShift()
            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error(response.message ?: "Gagal mengambil jadwal")
            }
        } catch (e: retrofit2.HttpException) {
            AuthResult.Error("Error: ${e.code()}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
}

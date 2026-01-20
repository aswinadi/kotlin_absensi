package com.maxmar.attendance.data.api

import com.maxmar.attendance.data.model.ApiResponse
import com.maxmar.attendance.data.model.Employee
import com.maxmar.attendance.data.model.EmployeeProfileData
import com.maxmar.attendance.data.model.TodayShiftResponse
import retrofit2.http.GET

/**
 * Retrofit API interface for employee-related endpoints.
 */
interface EmployeeApi {
    
    @GET(ApiEndpoints.EMPLOYEE_PROFILE)
    suspend fun getProfile(): ApiResponse<EmployeeProfileData>
    
    @POST(ApiEndpoints.EMPLOYEE_PROFILE)
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ApiResponse<Employee>
    
    @GET(ApiEndpoints.TODAY_SHIFT)
    suspend fun getTodayShift(): ApiResponse<TodayShiftResponse>
}

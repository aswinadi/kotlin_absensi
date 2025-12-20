package com.maxmar.attendance.data.api

import com.maxmar.attendance.data.model.ApiResponse
import com.maxmar.attendance.data.model.LoginRequest
import com.maxmar.attendance.data.model.LoginResponse
import com.maxmar.attendance.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit API interface for authentication endpoints.
 */
interface AuthApi {
    
    @POST(ApiEndpoints.LOGIN)
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>
    
    @POST(ApiEndpoints.LOGOUT)
    suspend fun logout(): ApiResponse<Unit>
    
    @GET(ApiEndpoints.ME)
    suspend fun me(): ApiResponse<User>
}

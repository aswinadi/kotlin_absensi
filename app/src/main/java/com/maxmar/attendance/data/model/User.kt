package com.maxmar.attendance.data.model

/**
 * User model from API response.
 */
data class User(
    val id: Int,
    val name: String,
    val email: String
)

/**
 * Login response containing user and token.
 */
data class LoginResponse(
    val user: User,
    val token: String,
    val tokenType: String = "Bearer"
)

/**
 * Generic API response wrapper.
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)

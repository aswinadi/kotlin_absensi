package com.maxmar.attendance.data.repository

import com.maxmar.attendance.data.api.AuthApi
import com.maxmar.attendance.data.local.TokenManager
import com.maxmar.attendance.data.model.LoginRequest
import com.maxmar.attendance.data.model.User
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result wrapper for auth operations.
 */
sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}

/**
 * Repository for authentication operations.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {
    
    /**
     * Login with username and password.
     * Saves token on success.
     */
    suspend fun login(username: String, password: String): AuthResult<User> {
        return try {
            val response = authApi.login(LoginRequest(username, password))
            
            if (response.success && response.data != null) {
                tokenManager.saveToken(response.data.token)
                AuthResult.Success(response.data.user)
            } else {
                AuthResult.Error(response.message ?: "Login gagal")
            }
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val message = parseErrorMessage(errorBody) ?: getHttpErrorMessage(e.code())
            AuthResult.Error(message)
        } catch (e: java.net.UnknownHostException) {
            AuthResult.Error("Tidak dapat terhubung ke server")
        } catch (e: java.net.SocketTimeoutException) {
            AuthResult.Error("Koneksi timeout. Periksa koneksi internet Anda.")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Fetch current authenticated user.
     */
    suspend fun fetchCurrentUser(): AuthResult<User> {
        return try {
            val response = authApi.me()
            
            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error(response.message ?: "Gagal mengambil data user")
            }
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 401) {
                tokenManager.clearToken()
            }
            AuthResult.Error(getHttpErrorMessage(e.code()))
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Logout user and clear token.
     */
    suspend fun logout() {
        try {
            authApi.logout()
        } catch (e: Exception) {
            // Ignore logout API errors
        } finally {
            tokenManager.clearToken()
        }
    }
    
    /**
     * Check if user is logged in.
     */
    suspend fun isLoggedIn(): Boolean {
        return tokenManager.hasToken()
    }
    
    /**
     * Check auth status - verify token is valid.
     */
    suspend fun checkAuthStatus(): AuthResult<User> {
        return if (tokenManager.hasToken()) {
            fetchCurrentUser()
        } else {
            AuthResult.Error("Not authenticated")
        }
    }
    
    private fun parseErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrEmpty()) return null
        
        return try {
            val json = com.google.gson.JsonParser.parseString(errorBody).asJsonObject
            when {
                json.has("message") -> json.get("message").asString
                json.has("errors") -> {
                    val errors = json.getAsJsonObject("errors")
                    errors.keySet().firstOrNull()?.let { key ->
                        errors.getAsJsonArray(key)?.firstOrNull()?.asString
                    }
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getHttpErrorMessage(code: Int): String {
        return when (code) {
            401 -> "Username atau password salah"
            403 -> "Akses ditolak"
            404 -> "Tidak ditemukan"
            422 -> "Data tidak valid"
            500 -> "Terjadi kesalahan pada server"
            else -> "Terjadi kesalahan (Error $code)"
        }
    }
    
    /**
     * Register device FCM token.
     */
    suspend fun registerDeviceToken(token: String) {
        try {
            val request = mapOf(
                "token" to token,
                "device_type" to "android"
            )
            authApi.updateDeviceToken(request)
        } catch (e: Exception) {
            // Silently fail for token updates, main flow shouldn't be blocked
        }
    }
    
    /**
     * Register currently stored FCM token if available.
     */
    suspend fun registerCurrentDeviceToken() {
        val token = tokenManager.getFcmToken()
        if (!token.isNullOrEmpty()) {
            registerDeviceToken(token)
        }
    }
}

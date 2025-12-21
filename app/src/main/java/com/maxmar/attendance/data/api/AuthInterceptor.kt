package com.maxmar.attendance.data.api

import com.maxmar.attendance.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp interceptor that adds Authorization header to requests.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Get token synchronously (interceptors don't support suspend)
        val token = runBlocking { tokenManager.getToken() }
        
        val request = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("Accept", "application/json")
                .header("ngrok-skip-browser-warning", "true")
                .build()
        } else {
            originalRequest.newBuilder()
                .header("Accept", "application/json")
                .header("ngrok-skip-browser-warning", "true")
                .build()
        }
        
        return chain.proceed(request)
    }
}

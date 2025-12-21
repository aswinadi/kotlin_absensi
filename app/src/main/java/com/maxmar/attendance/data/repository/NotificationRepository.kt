package com.maxmar.attendance.data.repository

import com.maxmar.attendance.data.api.NotificationApi
import com.maxmar.attendance.data.model.Notification
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for notification operations.
 */
@Singleton
class NotificationRepository @Inject constructor(
    private val notificationApi: NotificationApi
) {
    
    /**
     * Fetch all notifications.
     */
    suspend fun fetchNotifications(): AuthResult<List<Notification>> {
        return try {
            val response = notificationApi.getNotifications()
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
     * Get unread notification count.
     */
    suspend fun fetchUnreadCount(): AuthResult<Int> {
        return try {
            val response = notificationApi.getUnreadCount()
            if (response.success && response.data != null) {
                AuthResult.Success(response.data.count)
            } else {
                AuthResult.Success(0)
            }
        } catch (e: Exception) {
            AuthResult.Success(0)
        }
    }
    
    /**
     * Mark a notification as read.
     */
    suspend fun markAsRead(id: String): AuthResult<Unit> {
        return try {
            val response = notificationApi.markAsRead(id)
            if (response.success) {
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(response.message ?: "Gagal menandai")
            }
        } catch (e: retrofit2.HttpException) {
            AuthResult.Error("Error: ${e.code()}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Mark all notifications as read.
     */
    suspend fun markAllAsRead(): AuthResult<Unit> {
        return try {
            val response = notificationApi.markAllAsRead()
            if (response.success) {
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(response.message ?: "Gagal menandai")
            }
        } catch (e: retrofit2.HttpException) {
            AuthResult.Error("Error: ${e.code()}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
}

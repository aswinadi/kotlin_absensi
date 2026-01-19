package com.maxmar.attendance.data.api

import com.maxmar.attendance.data.model.NotificationActionResponse
import com.maxmar.attendance.data.model.NotificationListResponse
import com.maxmar.attendance.data.model.UnreadCountResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit API interface for notification endpoints.
 */
interface NotificationApi {
    
    @GET(ApiEndpoints.NOTIFICATIONS)
    suspend fun getNotifications(): NotificationListResponse
    
    @GET(ApiEndpoints.NOTIFICATIONS_UNREAD_COUNT)
    suspend fun getUnreadCount(): UnreadCountResponse
    
    @POST("att/notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: String): NotificationActionResponse
    
    @POST(ApiEndpoints.NOTIFICATIONS_MARK_ALL_READ)
    suspend fun markAllAsRead(): NotificationActionResponse
}

package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName

/**
 * Notification from API.
 */
data class Notification(
    val id: String,
    val type: String,
    val title: String,
    val message: String,
    val data: Map<String, Any>?,
    @SerializedName("is_read")
    val isRead: Boolean,
    @SerializedName("read_at")
    val readAt: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("time_ago")
    val timeAgo: String?
) {
    val typeDisplay: String
        get() = when (type) {
            "approval_result" -> "Persetujuan"
            "check_in_reminder" -> "Pengingat"
            "business_trip" -> "Perjalanan Dinas"
            else -> "Notifikasi"
        }
}

/**
 * Notification list response.
 */
data class NotificationListResponse(
    val success: Boolean,
    val data: List<Notification>?
)

/**
 * Unread count response.
 */
data class UnreadCountData(
    val count: Int
)

data class UnreadCountResponse(
    val success: Boolean,
    val data: UnreadCountData?
)

/**
 * Generic action response.
 */
data class NotificationActionResponse(
    val success: Boolean,
    val message: String?
)

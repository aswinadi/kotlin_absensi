package com.maxmar.attendance.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.maxmar.attendance.R
import com.maxmar.attendance.data.local.TokenManager
import com.maxmar.attendance.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Firebase Cloud Messaging service for handling push notifications.
 */
@AndroidEntryPoint
class MaxmarFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var tokenManager: TokenManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "MaxmarFCM"
        private const val CHANNEL_ID = "maxmar_notifications"
        private const val CHANNEL_NAME = "Maxmar Attendance"
        private const val CHANNEL_DESCRIPTION = "Notifications for attendance and business trips"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        
        // Store the token locally for later use
        serviceScope.launch {
            tokenManager.saveFcmToken(token)
            
            // TODO: Send token to server when user is logged in
            // This should be done via the API to register the device token
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message received from: ${message.from}")

        // Handle notification payload
        message.notification?.let { notification ->
            showNotification(
                title = notification.title ?: "Maxmar Attendance",
                body = notification.body ?: ""
            )
        }

        // Handle data payload
        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message data: ${message.data}")
            handleDataMessage(message.data)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"] ?: return
        val title = data["title"] ?: "Maxmar Attendance"
        val body = data["body"] ?: ""
        
        when (type) {
            "attendance" -> showNotification(title, body, NotificationType.ATTENDANCE)
            "business_trip" -> showNotification(title, body, NotificationType.BUSINESS_TRIP)
            "approval" -> showNotification(title, body, NotificationType.APPROVAL)
            else -> showNotification(title, body)
        }
    }

    private fun showNotification(
        title: String,
        body: String,
        type: NotificationType = NotificationType.GENERAL
    ) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent for notification tap
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", type.name)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Show notification with unique ID based on timestamp
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private enum class NotificationType {
        GENERAL,
        ATTENDANCE,
        BUSINESS_TRIP,
        APPROVAL
    }
}

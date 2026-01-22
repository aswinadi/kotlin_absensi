package com.maxmar.attendance.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.maxmar.attendance.data.local.SettingsManager
import com.maxmar.attendance.data.service.MaxmarFirebaseMessagingService
import com.maxmar.attendance.ui.navigation.DeepLinkData
import com.maxmar.attendance.ui.navigation.MaxmarNavHost
import com.maxmar.attendance.ui.theme.MaxmarTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main entry point Activity for the app.
 * Uses Jetpack Compose for UI rendering.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var settingsManager: SettingsManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        // Extract deep link data from intent
        val deepLinkData = extractDeepLinkData(intent)
        
        setContent {
            val isDarkMode by settingsManager.isDarkMode.collectAsState(initial = true)
            val initialDeepLink = remember { mutableStateOf(deepLinkData) }
            
            MaxmarTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MaxmarNavHost(
                        deepLinkData = initialDeepLink.value,
                        onDeepLinkHandled = { initialDeepLink.value = null }
                    )
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Handle new intent when app is already running
        // The activity will be recreated, handling the deep link
    }
    
    private fun extractDeepLinkData(intent: Intent?): DeepLinkData? {
        if (intent == null) return null
        
        val type = intent.getStringExtra(MaxmarFirebaseMessagingService.EXTRA_NOTIFICATION_TYPE)
        val id = intent.getStringExtra(MaxmarFirebaseMessagingService.EXTRA_NOTIFICATION_ID)
        val action = intent.getStringExtra(MaxmarFirebaseMessagingService.EXTRA_NOTIFICATION_ACTION)
        
        if (type.isNullOrEmpty()) return null
        
        return DeepLinkData(
            type = type,
            id = id ?: "",
            action = action ?: ""
        )
    }
}


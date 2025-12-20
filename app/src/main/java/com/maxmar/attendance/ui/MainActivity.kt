package com.maxmar.attendance.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.maxmar.attendance.ui.navigation.MaxmarNavHost
import com.maxmar.attendance.ui.theme.MaxmarTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point Activity for the app.
 * Uses Jetpack Compose for UI rendering.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        setContent {
            MaxmarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MaxmarNavHost()
                }
            }
        }
    }
}

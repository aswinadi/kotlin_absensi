package com.maxmar.attendance.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension property for Settings DataStore
private val Context.settingsDataStore by preferencesDataStore(name = "settings")

/**
 * Manages app settings including theme preference using DataStore.
 */
@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }
    
    /**
     * Flow of current dark mode setting.
     * Defaults to true (dark mode) if not set.
     */
    val isDarkMode: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: true // Default to dark mode
        }
    
    /**
     * Set dark mode preference.
     */
    suspend fun setDarkMode(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }
    
    /**
     * Toggle dark mode.
     */
    suspend fun toggleDarkMode() {
        context.settingsDataStore.edit { preferences ->
            val current = preferences[DARK_MODE_KEY] ?: true
            preferences[DARK_MODE_KEY] = !current
        }
    }
}

package com.maxmar.attendance.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension property for DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "maxmar_prefs")

/**
 * Manages authentication token storage using DataStore.
 */
@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val FCM_TOKEN_KEY = stringPreferencesKey("fcm_token")
        private val REMEMBER_ME_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("remember_me")
    }
    
    /**
     * Save authentication token.
     */
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    /**
     * Save "Remember Me" preference.
     */
    suspend fun saveRememberMe(rememberMe: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMEMBER_ME_KEY] = rememberMe
        }
    }

    /**
     * Check if user wants to be remembered.
     * Defaults to true for backward compatibility.
     */
    suspend fun shouldRememberMe(): Boolean {
        return context.dataStore.data
            .map { preferences -> preferences[REMEMBER_ME_KEY] ?: true }
            .first()
    }
    
    /**
     * Get stored token or null if not exists.
     */
    suspend fun getToken(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[TOKEN_KEY] }
            .first()
    }
    
    /**
     * Clear stored token (logout).
     */
    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
    
    /**
     * Check if user has a stored token.
     */
    suspend fun hasToken(): Boolean {
        return getToken() != null
    }
    
    /**
     * Save FCM token for push notifications.
     */
    suspend fun saveFcmToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[FCM_TOKEN_KEY] = token
        }
    }
    
    /**
     * Get stored FCM token or null if not exists.
     */
    suspend fun getFcmToken(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[FCM_TOKEN_KEY] }
            .first()
    }
}

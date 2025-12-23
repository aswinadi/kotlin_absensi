package com.maxmar.attendance.ui.screens.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxmar.attendance.data.model.Notification
import com.maxmar.attendance.data.repository.AuthResult
import com.maxmar.attendance.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Notification list state.
 */
data class NotificationListState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val error: String? = null,
    val actionSuccess: String? = null
)

/**
 * ViewModel for Notification screen.
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(NotificationListState())
    val state: StateFlow<NotificationListState> = _state.asStateFlow()
    
    init {
        loadNotifications()
    }
    
    /**
     * Load all notifications.
     */
    fun loadNotifications() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            when (val result = repository.fetchNotifications()) {
                is AuthResult.Success -> {
                    val unreadCount = result.data.count { !it.isRead }
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notifications = result.data,
                        unreadCount = unreadCount
                    )
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    /**
     * Mark a notification as read.
     */
    fun markAsRead(notification: Notification) {
        if (notification.isRead) return
        
        viewModelScope.launch {
            when (repository.markAsRead(notification.id)) {
                is AuthResult.Success -> {
                    // Update local state
                    val updated = _state.value.notifications.map {
                        if (it.id == notification.id) it.copy(isRead = true) else it
                    }
                    val unreadCount = updated.count { !it.isRead }
                    _state.value = _state.value.copy(
                        notifications = updated,
                        unreadCount = unreadCount
                    )
                }
                is AuthResult.Error -> {
                    // Silent fail
                }
            }
        }
    }
    
    /**
     * Mark all notifications as read.
     */
    fun markAllAsRead() {
        viewModelScope.launch {
            when (repository.markAllAsRead()) {
                is AuthResult.Success -> {
                    val updated = _state.value.notifications.map { it.copy(isRead = true) }
                    _state.value = _state.value.copy(
                        notifications = updated,
                        unreadCount = 0,
                        actionSuccess = "Semua notifikasi ditandai sudah dibaca"
                    )
                }
                is AuthResult.Error -> {
                    // Silent fail
                }
            }
        }
    }
    
    /**
     * Clear action success message.
     */
    fun clearActionSuccess() {
        _state.value = _state.value.copy(actionSuccess = null)
    }
    
    /**
     * Refresh notifications.
     */
    fun refresh() {
        loadNotifications()
    }
}

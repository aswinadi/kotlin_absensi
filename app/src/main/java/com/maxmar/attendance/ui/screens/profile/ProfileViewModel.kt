package com.maxmar.attendance.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxmar.attendance.data.local.SettingsManager
import com.maxmar.attendance.data.model.Employee
import com.maxmar.attendance.data.model.LeaveQuota
import com.maxmar.attendance.data.repository.AuthRepository
import com.maxmar.attendance.data.repository.AuthResult
import com.maxmar.attendance.data.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Profile screen.
 */
data class ProfileState(
    val isLoading: Boolean = true,
    val employee: Employee? = null,
    val leaveQuota: LeaveQuota? = null,
    val error: String? = null,
    val isLoggingOut: Boolean = false
)

/**
 * ViewModel for Profile screen.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val authRepository: AuthRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {
    
    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()
    
    private val _logoutEvent = MutableStateFlow(false)
    val logoutEvent: StateFlow<Boolean> = _logoutEvent.asStateFlow()
    
    /**
     * Dark mode state flow.
     */
    val isDarkMode: StateFlow<Boolean> = settingsManager.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    
    init {
        loadProfile()
    }
    
    /**
     * Load employee profile data.
     */
    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true, error = null)
            
            when (val result = employeeRepository.fetchFullProfile()) {
                is AuthResult.Success -> {
                    _profileState.value = _profileState.value.copy(
                        employee = result.data.employee,
                        leaveQuota = result.data.leaveQuota,
                        isLoading = false
                    )
                }
                is AuthResult.Error -> {
                    _profileState.value = _profileState.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * Toggle dark mode setting.
     */
    fun toggleDarkMode() {
        viewModelScope.launch {
            settingsManager.toggleDarkMode()
        }
    }
    
    /**
     * Logout user.
     */
    fun logout() {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoggingOut = true)
            authRepository.logout()
            _logoutEvent.value = true
            _logoutEvent.value = true
        }
    }
    
    /**
     * Change password.
     */
    fun changePassword(
        current: String,
        new: String,
        confirm: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            
            val request = com.maxmar.attendance.data.model.ChangePasswordRequest(
                currentPassword = current,
                newPassword = new,
                newPasswordConfirmation = confirm
            )
            
            when (val result = authRepository.changePassword(request)) {
                is AuthResult.Success -> {
                    _profileState.value = _profileState.value.copy(isLoading = false)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _profileState.value = _profileState.value.copy(isLoading = false, error = result.message)
                    onError(result.message)
                }
            }
        }
    }
}


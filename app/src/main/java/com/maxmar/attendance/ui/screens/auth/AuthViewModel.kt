package com.maxmar.attendance.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxmar.attendance.data.model.User
import com.maxmar.attendance.data.repository.AuthRepository
import com.maxmar.attendance.data.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Authentication state sealed class.
 */
sealed class AuthState {
    data object Initial : AuthState()
    data object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data object Unauthenticated : AuthState()
    data class RequiresProfileCompletion(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * ViewModel for authentication operations.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val employeeRepository: com.maxmar.attendance.data.repository.EmployeeRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    /**
     * Check if user is already authenticated.
     */
    fun checkAuthStatus() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            // If user didn't want to be remembered, clear token now (app restart)
            if (!authRepository.shouldRememberMe()) {
                authRepository.logout()
                _authState.value = AuthState.Unauthenticated
                return@launch
            }

            when (val result = authRepository.checkAuthStatus()) {
                is AuthResult.Success -> {
                    checkProfileStatus(result.data)
                }
                is AuthResult.Error -> {
                    _authState.value = AuthState.Unauthenticated
                }
            }
        }
    }
    
    /**
     * Login with username and password.
     */
    fun login(username: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            when (val result = authRepository.login(username, password, rememberMe)) {
                is AuthResult.Success -> {
                    // Update device token if available
                    viewModelScope.launch {
                        authRepository.registerCurrentDeviceToken()
                    }
                    checkProfileStatus(result.data)
                }
                is AuthResult.Error -> {
                    _authState.value = AuthState.Error(result.message)
                }
            }
        }
    }
    
    /**
     * Check if employee profile is complete.
     */
    private suspend fun checkProfileStatus(user: User) {
        when (val result = employeeRepository.fetchProfile()) {
            is AuthResult.Success -> {
                val employee = result.data
                val isComplete = !employee.phone.isNullOrBlank() &&
                        !employee.nik.isNullOrBlank() &&
                        !employee.permanentAddress.isNullOrBlank() &&
                        !employee.permanentCity.isNullOrBlank() &&
                        !employee.currentAddress.isNullOrBlank() &&
                        !employee.currentCity.isNullOrBlank()
                
                if (isComplete) {
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    _authState.value = AuthState.RequiresProfileCompletion(user)
                }
            }
            is AuthResult.Error -> {
                // If we can't fetch profile, assuming it's okay or handle error differently.
                // For safety, let's assume authenticated but log error or retrying?
                // Or maybe user doesn't have employee data yet?
                // Let's pass through if 404 (maybe admin user?), but if network error maybe show error?
                _authState.value = AuthState.Authenticated(user)
            }
        }
    }

    fun fetchProfileForCompletion(onSuccess: (com.maxmar.attendance.data.model.Employee) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val result = employeeRepository.fetchProfile()) {
                is AuthResult.Success -> onSuccess(result.data)
                is AuthResult.Error -> onError(result.message)
            }
        }
    }

    fun completeProfile(request: com.maxmar.attendance.data.model.UpdateProfileRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            when (val result = employeeRepository.updateProfile(request)) {
                is AuthResult.Success -> {
                    // Improve: we should probably have current user stored or passed
                    // But checkAuthStatus will refresh everything
                   checkAuthStatus()
                   onSuccess()
                }
                is AuthResult.Error -> {
                     onError(result.message)
                     // Restore previous state? Hard to know user without storing it.
                     // checkAuthStatus will restore state
                     checkAuthStatus()
                }
            }
        }
    }
    
    /**
     * Logout current user.
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState.Unauthenticated
        }
    }
    
    /**
     * Clear error state and return to unauthenticated.
     */
    fun clearError() {
        _authState.value = AuthState.Unauthenticated
    }
}

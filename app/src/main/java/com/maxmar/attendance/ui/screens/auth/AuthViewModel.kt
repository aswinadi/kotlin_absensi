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
    data class Error(val message: String) : AuthState()
}

/**
 * ViewModel for authentication operations.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    /**
     * Check if user is already authenticated.
     */
    fun checkAuthStatus() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            when (val result = authRepository.checkAuthStatus()) {
                is AuthResult.Success -> {
                    _authState.value = AuthState.Authenticated(result.data)
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
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            when (val result = authRepository.login(username, password)) {
                is AuthResult.Success -> {
                    _authState.value = AuthState.Authenticated(result.data)
                }
                is AuthResult.Error -> {
                    _authState.value = AuthState.Error(result.message)
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

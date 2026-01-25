package com.maxmar.attendance.ui.screens.absent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxmar.attendance.data.model.AbsentAttendance
import com.maxmar.attendance.data.repository.AbsentRepository
import com.maxmar.attendance.data.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AbsentDetailState(
    val isLoading: Boolean = false,
    val absent: AbsentAttendance? = null,
    val userPositionLevel: Int? = null,
    val actionSuccess: String? = null,
    val error: String? = null
)

@HiltViewModel
class AbsentDetailViewModel @Inject constructor(
    private val absentRepository: AbsentRepository,
    private val approvalRepository: com.maxmar.attendance.data.repository.ApprovalRepository,
    private val employeeRepository: com.maxmar.attendance.data.repository.EmployeeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AbsentDetailState())
    val state: StateFlow<AbsentDetailState> = _state.asStateFlow()

    fun loadDetail(absentId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, actionSuccess = null)
            
            // Get current user's level from profile
            val profileResult = employeeRepository.fetchProfile()
            val userLevel = if (profileResult is AuthResult.Success) profileResult.data.positionLevel else null

            when (val result = absentRepository.fetchAbsentDetail(absentId)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        absent = result.data,
                        userPositionLevel = userLevel
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

    fun clearMessages() {
        _state.value = _state.value.copy(actionSuccess = null, error = null)
    }

    fun acknowledge(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = approvalRepository.acknowledge(id)) {
                is AuthResult.Success -> {
                    loadDetail(id) // Refresh
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun approve(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = approvalRepository.approve(id)) {
                is AuthResult.Success -> {
                    loadDetail(id) // Refresh
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun reject(id: Int, reason: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = approvalRepository.reject(id, reason)) {
                is AuthResult.Success -> {
                    loadDetail(id) // Refresh
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }
}

package com.maxmar.attendance.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxmar.attendance.data.model.Employee
import com.maxmar.attendance.data.model.Shift
import com.maxmar.attendance.data.repository.AuthResult
import com.maxmar.attendance.data.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * UI State for Home screen.
 */
data class HomeState(
    val isLoading: Boolean = true,
    val employee: Employee? = null,
    val isWorkday: Boolean = false,
    val shift: Shift? = null,
    val hasCheckedIn: Boolean = false,
    val hasCheckedOut: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for Home screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository
) : ViewModel() {
    
    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()
    
    init {
        loadData()
    }
    
    /**
     * Load all home screen data.
     */
    fun loadData() {
        viewModelScope.launch {
            _homeState.value = _homeState.value.copy(isLoading = true, error = null)
            
            // Fetch profile
            when (val profileResult = employeeRepository.fetchProfile()) {
                is AuthResult.Success -> {
                    _homeState.value = _homeState.value.copy(employee = profileResult.data)
                }
                is AuthResult.Error -> {
                    _homeState.value = _homeState.value.copy(error = profileResult.message)
                }
            }
            
            // Fetch today's shift
            when (val shiftResult = employeeRepository.fetchTodayShift()) {
                is AuthResult.Success -> {
                    _homeState.value = _homeState.value.copy(
                        isWorkday = shiftResult.data.isWorkday,
                        shift = shiftResult.data.shift,
                        isLoading = false
                    )
                }
                is AuthResult.Error -> {
                    _homeState.value = _homeState.value.copy(
                        isLoading = false,
                        error = shiftResult.message
                    )
                }
            }
        }
    }
    
    /**
     * Get greeting based on time of day.
     */
    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Selamat Pagi"
            hour < 15 -> "Selamat Siang"
            hour < 18 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }
}

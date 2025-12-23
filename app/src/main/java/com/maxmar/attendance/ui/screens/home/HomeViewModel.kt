package com.maxmar.attendance.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxmar.attendance.data.model.AttendanceSummary
import com.maxmar.attendance.data.model.Employee
import com.maxmar.attendance.data.model.Shift
import com.maxmar.attendance.data.repository.AttendanceRepository
import com.maxmar.attendance.data.repository.AuthResult
import com.maxmar.attendance.data.repository.EmployeeRepository
import com.maxmar.attendance.data.repository.NotificationRepository
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
    val summary: AttendanceSummary? = null,
    val hasCheckedIn: Boolean = false,
    val hasCheckedOut: Boolean = false,
    val checkInTime: String? = null,
    val checkOutTime: String? = null,
    val unreadNotificationCount: Int = 0,
    val error: String? = null
)

/**
 * ViewModel for Home screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val attendanceRepository: AttendanceRepository,
    private val notificationRepository: NotificationRepository
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
                        shift = shiftResult.data.shift
                    )
                }
                is AuthResult.Error -> {
                    _homeState.value = _homeState.value.copy(error = shiftResult.message)
                }
            }
            
            // Fetch monthly summary
            when (val summaryResult = attendanceRepository.fetchSummary()) {
                is AuthResult.Success -> {
                    _homeState.value = _homeState.value.copy(summary = summaryResult.data)
                }
                is AuthResult.Error -> {
                    // Don't fail the whole screen if summary fails
                }
            }
            
            // Fetch today's attendance for check-in/out times
            when (val todayResult = attendanceRepository.fetchTodayAttendance()) {
                is AuthResult.Success -> {
                    val today = todayResult.data
                    _homeState.value = _homeState.value.copy(
                        hasCheckedIn = today?.checkIn != null,
                        hasCheckedOut = today?.checkOut != null,
                        checkInTime = today?.checkIn?.time,
                        checkOutTime = today?.checkOut?.time
                    )
                }
                is AuthResult.Error -> {
                    // Don't fail for attendance
                }
            }
            
            // Fetch unread notification count
            when (val countResult = notificationRepository.fetchUnreadCount()) {
                is AuthResult.Success -> {
                    _homeState.value = _homeState.value.copy(
                        unreadNotificationCount = countResult.data,
                        isLoading = false
                    )
                }
                is AuthResult.Error -> {
                    _homeState.value = _homeState.value.copy(isLoading = false)
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

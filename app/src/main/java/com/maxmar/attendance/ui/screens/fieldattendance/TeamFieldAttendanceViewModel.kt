package com.maxmar.attendance.ui.screens.fieldattendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxmar.attendance.data.repository.AuthResult
import com.maxmar.attendance.data.repository.FieldAttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Data class for team field attendance item.
 */
data class TeamFieldAttendanceItem(
    val id: Int,
    val employeeId: Int,
    val employeeName: String,
    val employeeCode: String?,
    val position: String?,
    val date: String,
    val locationName: String,
    val purpose: String,
    val arrivalTime: String?,
    val departureTime: String?,
    val hasArrived: Boolean,
    val hasDeparted: Boolean
)

/**
 * State for TeamFieldAttendanceScreen.
 */
data class TeamFieldAttendanceState(
    val isLoading: Boolean = false,
    val items: List<TeamFieldAttendanceItem> = emptyList(),
    val selectedTab: Int = 0, // 0 = Today, 1 = Upcoming
    val error: String? = null
)

/**
 * ViewModel for Team Field Attendance screen.
 */
@HiltViewModel
class TeamFieldAttendanceViewModel @Inject constructor(
    private val repository: FieldAttendanceRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(TeamFieldAttendanceState())
    val state: StateFlow<TeamFieldAttendanceState> = _state.asStateFlow()
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            val filter = if (_state.value.selectedTab == 0) "today" else "upcoming"
            
            when (val result = repository.fetchTeamFieldAttendances(filter)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        items = result.data
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
    
    fun setTab(tab: Int) {
        if (_state.value.selectedTab != tab) {
            _state.value = _state.value.copy(selectedTab = tab)
            loadData()
        }
    }
}

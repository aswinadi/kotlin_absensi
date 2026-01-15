package com.maxmar.attendance.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxmar.attendance.data.model.Attendance
import com.maxmar.attendance.data.repository.AttendanceRepository
import com.maxmar.attendance.data.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.maxmar.attendance.data.model.AbsentAttendance
import com.maxmar.attendance.data.model.AttendanceSummary
import com.maxmar.attendance.data.repository.AbsentRepository
import javax.inject.Inject

/**
 * UI State for History screen.
 */
data class HistoryState(
    val isLoading: Boolean = true,
    val attendances: List<Attendance> = emptyList(),
    val currentPage: Int = 1,
    val lastPage: Int = 1,
    val hasMore: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val selectedYear: Int = LocalDate.now().year,
    val selectedMonth: Int = LocalDate.now().monthValue,
    val absents: List<AbsentAttendance> = emptyList(),
    val summary: AttendanceSummary? = null,
    val isLoadingAbsents: Boolean = false,
    val summaryError: String? = null,
    val selectedTab: Int = 0 // 0: Attendance, 1: Absences, 2: Summary
)

/**
 * ViewModel for History screen.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val absentRepository: AbsentRepository
) : ViewModel() {
    
    private val _historyState = MutableStateFlow(HistoryState())
    val historyState: StateFlow<HistoryState> = _historyState.asStateFlow()
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    init {
        // Initialize with filtering by current month
        val now = LocalDate.now()
        setMonthYear(now.year, now.monthValue)
    }
    
    /**
     * Load attendance history (first page).
     */
    fun loadHistory() {
        viewModelScope.launch {
            _historyState.value = _historyState.value.copy(
                isLoading = true,
                error = null,
                currentPage = 1,
                attendances = emptyList()
            )
            
            val state = _historyState.value
            val startDateStr = state.startDate?.format(dateFormatter)
            val endDateStr = state.endDate?.format(dateFormatter)
            
            when (val result = attendanceRepository.fetchHistory(startDateStr, endDateStr, 1)) {
                is AuthResult.Success -> {
                    _historyState.value = _historyState.value.copy(
                        attendances = result.data.attendances,
                        currentPage = result.data.meta?.currentPage ?: 1,
                        lastPage = result.data.meta?.lastPage ?: 1,
                        hasMore = (result.data.meta?.currentPage ?: 1) < (result.data.meta?.lastPage ?: 1),
                        isLoading = false
                    )
                }
                is AuthResult.Error -> {
                    _historyState.value = _historyState.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * Load more attendances (pagination).
     */
    fun loadMore() {
        val state = _historyState.value
        if (state.isLoadingMore || !state.hasMore) return
        
        viewModelScope.launch {
            _historyState.value = _historyState.value.copy(isLoadingMore = true)
            
            val nextPage = state.currentPage + 1
            val startDateStr = state.startDate?.format(dateFormatter)
            val endDateStr = state.endDate?.format(dateFormatter)
            
            when (val result = attendanceRepository.fetchHistory(startDateStr, endDateStr, nextPage)) {
                is AuthResult.Success -> {
                    _historyState.value = _historyState.value.copy(
                        attendances = state.attendances + result.data.attendances,
                        currentPage = result.data.meta?.currentPage ?: nextPage,
                        lastPage = result.data.meta?.lastPage ?: 1,
                        hasMore = (result.data.meta?.currentPage ?: nextPage) < (result.data.meta?.lastPage ?: 1),
                        isLoadingMore = false
                    )
                }
                is AuthResult.Error -> {
                    _historyState.value = _historyState.value.copy(
                        isLoadingMore = false
                    )
                }
            }
        }
    }
    
    /**
     * Set month and year filter and reload all data.
     */
    fun setMonthYear(year: Int, month: Int) {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.plusMonths(1).minusDays(1)
        
        _historyState.value = _historyState.value.copy(
            selectedYear = year,
            selectedMonth = month,
            startDate = startDate,
            endDate = endDate
        )
        
        loadHistory()
        loadAbsents()
        loadSummary()
    }
    
    /**
     * Load absent attendance records.
     */
    fun loadAbsents() {
        val state = _historyState.value
        
        viewModelScope.launch {
            _historyState.value = _historyState.value.copy(isLoadingAbsents = true)
            
            when (val result = absentRepository.fetchAbsentsByMonth(state.selectedYear, state.selectedMonth)) {
                is AuthResult.Success -> {
                    _historyState.value = _historyState.value.copy(
                        absents = result.data.absents,
                        isLoadingAbsents = false
                    )
                }
                is AuthResult.Error -> {
                    _historyState.value = _historyState.value.copy(
                        isLoadingAbsents = false
                        // Don't show error for absents to avoid disrupting other views
                    )
                }
            }
        }
    }
    
    /**
     * Load attendance summary.
     */
    fun loadSummary() {
         // Note: The existing fetchSummary API returns summary for the CURRENT month/year as per API implementation
         // If the API supports filtering summary by month/year, we should use that. 
         // For now, we'll use the existing call, but ideally attendanceRepository.fetchSummary needs parameters.
         // Wait, the regular fetchSummary might only return "this month".
         // Let's assume for now we use the existing one, but I should check if I can filter it.
         // Based on previous files, fetching summary didn't seem to take params. 
         // I'll stick to calling it for now.
         
        viewModelScope.launch {
            when (val result = attendanceRepository.fetchSummary(
                year = _historyState.value.selectedYear,
                month = _historyState.value.selectedMonth
            )) {
                 is AuthResult.Success -> {
                    _historyState.value = _historyState.value.copy(
                        summary = result.data,
                        summaryError = null
                    )
                }
                is AuthResult.Error -> {
                    _historyState.value = _historyState.value.copy(
                        summary = null,
                        summaryError = result.message
                    )
                }
            }
        }
    }

    /**
     * Set selected tab.
     */
    fun setTab(index: Int) {
        _historyState.value = _historyState.value.copy(selectedTab = index)
    }
    
    /**
     * Set date filter and reload.
     */
    fun setDateFilter(startDate: LocalDate?, endDate: LocalDate?) {
        _historyState.value = _historyState.value.copy(
            startDate = startDate,
            endDate = endDate
        )
        loadHistory()
    }
    
    /**
     * Clear date filter.
     */
    fun clearFilter() {
        _historyState.value = _historyState.value.copy(
            startDate = null,
            endDate = null
        )
        loadHistory()
    }
}

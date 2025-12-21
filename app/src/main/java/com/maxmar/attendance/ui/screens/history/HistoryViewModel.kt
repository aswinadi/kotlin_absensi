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
    val endDate: LocalDate? = null
)

/**
 * ViewModel for History screen.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {
    
    private val _historyState = MutableStateFlow(HistoryState())
    val historyState: StateFlow<HistoryState> = _historyState.asStateFlow()
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    init {
        loadHistory()
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

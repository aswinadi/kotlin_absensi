package com.maxmar.attendance.ui.screens.businesstrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxmar.attendance.data.model.BusinessTrip
import com.maxmar.attendance.data.repository.AuthResult
import com.maxmar.attendance.data.repository.BusinessTripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Business Trip list state.
 */
data class BusinessTripListState(
    val isLoading: Boolean = false,
    val trips: List<BusinessTrip> = emptyList(),
    val selectedFilter: String = "all", // all, pending, approved
    val error: String? = null,
    val hasMore: Boolean = true,
    val currentPage: Int = 1
)

/**
 * Business Trip detail state.
 */
data class BusinessTripDetailState(
    val isLoading: Boolean = false,
    val trip: BusinessTrip? = null,
    val error: String? = null
)

/**
 * ViewModel for Business Trip screens.
 */
@HiltViewModel
class BusinessTripViewModel @Inject constructor(
    private val repository: BusinessTripRepository
) : ViewModel() {
    
    private val _listState = MutableStateFlow(BusinessTripListState())
    val listState: StateFlow<BusinessTripListState> = _listState.asStateFlow()
    
    private val _detailState = MutableStateFlow(BusinessTripDetailState())
    val detailState: StateFlow<BusinessTripDetailState> = _detailState.asStateFlow()
    
    init {
        loadTrips()
    }
    
    /**
     * Load business trips.
     */
    fun loadTrips(refresh: Boolean = false) {
        if (_listState.value.isLoading) return
        
        val page = if (refresh) 1 else _listState.value.currentPage
        val status = when (_listState.value.selectedFilter) {
            "pending" -> "pending"
            "approved" -> "approved"
            else -> null
        }
        
        viewModelScope.launch {
            _listState.value = _listState.value.copy(
                isLoading = true,
                error = null,
                trips = if (refresh) emptyList() else _listState.value.trips
            )
            
            when (val result = repository.fetchBusinessTrips(page, status)) {
                is AuthResult.Success -> {
                    val newTrips = if (refresh) {
                        result.data.trips
                    } else {
                        _listState.value.trips + result.data.trips
                    }
                    
                    val hasMore = result.data.meta?.let {
                        it.currentPage < it.lastPage
                    } ?: false
                    
                    _listState.value = _listState.value.copy(
                        isLoading = false,
                        trips = newTrips,
                        currentPage = if (hasMore) page + 1 else page,
                        hasMore = hasMore
                    )
                }
                is AuthResult.Error -> {
                    _listState.value = _listState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    /**
     * Set filter and reload.
     */
    fun setFilter(filter: String) {
        if (_listState.value.selectedFilter != filter) {
            _listState.value = _listState.value.copy(
                selectedFilter = filter,
                currentPage = 1,
                hasMore = true
            )
            loadTrips(refresh = true)
        }
    }
    
    /**
     * Load more trips (pagination).
     */
    fun loadMore() {
        if (_listState.value.hasMore && !_listState.value.isLoading) {
            loadTrips()
        }
    }
    
    /**
     * Load trip detail.
     */
    fun loadTripDetail(tripId: Int) {
        viewModelScope.launch {
            _detailState.value = BusinessTripDetailState(isLoading = true)
            
            when (val result = repository.fetchBusinessTripDetail(tripId)) {
                is AuthResult.Success -> {
                    _detailState.value = BusinessTripDetailState(
                        isLoading = false,
                        trip = result.data
                    )
                }
                is AuthResult.Error -> {
                    _detailState.value = BusinessTripDetailState(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    /**
     * Refresh trips list.
     */
    fun refresh() {
        loadTrips(refresh = true)
    }
}

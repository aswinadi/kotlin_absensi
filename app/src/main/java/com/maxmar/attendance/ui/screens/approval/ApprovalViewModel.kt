package com.maxmar.attendance.ui.screens.approval

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxmar.attendance.data.model.Approval
import com.maxmar.attendance.data.repository.ApprovalRepository
import com.maxmar.attendance.data.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Approval list state.
 */
data class ApprovalListState(
    val isLoading: Boolean = false,
    val pendingItems: List<Approval> = emptyList(),
    val processedItems: List<Approval> = emptyList(),
    val selectedFilter: String = "pending", // pending, processed
    val selectedCategory: String = "all", // all, izin, perdin
    val error: String? = null,
    val actionSuccess: String? = null,
    val actionError: String? = null
)

/**
 * ViewModel for Approval screen.
 */
@HiltViewModel
class ApprovalViewModel @Inject constructor(
    private val repository: ApprovalRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ApprovalListState())
    val state: StateFlow<ApprovalListState> = _state.asStateFlow()
    
    init {
        loadApprovals()
    }
    
    /**
     * Load all approvals.
     */
    fun loadApprovals() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            when (val result = repository.fetchApprovals()) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        pendingItems = result.data.pending,
                        processedItems = result.data.processed
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
    
    /**
     * Set filter tab.
     */
    fun setFilter(filter: String) {
        _state.value = _state.value.copy(selectedFilter = filter)
    }
    
    /**
     * Set category filter (all, izin, perdin).
     */
    fun setCategory(category: String) {
        _state.value = _state.value.copy(selectedCategory = category)
    }
    
    /**
     * Acknowledge a request.
     */
    fun acknowledge(approval: Approval) {
        viewModelScope.launch {
            _state.value = _state.value.copy(actionSuccess = null, actionError = null)
            
            when (val result = repository.acknowledge(approval.id)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        actionSuccess = "Berhasil diketahui"
                    )
                    loadApprovals() // Refresh list
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        actionError = result.message
                    )
                }
            }
        }
    }
    
    /**
     * Approve a request.
     */
    fun approve(approval: Approval) {
        viewModelScope.launch {
            _state.value = _state.value.copy(actionSuccess = null, actionError = null)
            
            when (val result = repository.approve(approval.id)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        actionSuccess = "Berhasil disetujui"
                    )
                    loadApprovals() // Refresh list
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        actionError = result.message
                    )
                }
            }
        }
    }
    
    /**
     * Reject a request.
     */
    fun reject(approval: Approval, reason: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(actionSuccess = null, actionError = null)
            
            when (val result = repository.reject(approval.id, reason)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        actionSuccess = "Berhasil ditolak"
                    )
                    loadApprovals() // Refresh list
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        actionError = result.message
                    )
                }
            }
        }
    }
    
    /**
     * Clear action messages.
     */
    fun clearActionMessages() {
        _state.value = _state.value.copy(actionSuccess = null, actionError = null)
    }
    
    /**
     * Refresh approvals.
     */
    fun refresh() {
        loadApprovals()
    }
}

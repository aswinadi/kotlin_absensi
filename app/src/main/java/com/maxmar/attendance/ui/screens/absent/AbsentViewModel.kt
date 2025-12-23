package com.maxmar.attendance.ui.screens.absent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxmar.attendance.data.model.AbsentType
import com.maxmar.attendance.data.repository.AbsentRepository
import com.maxmar.attendance.data.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * UI State for Absent Form screen.
 */
data class AbsentFormState(
    val isLoading: Boolean = false,
    val isLoadingTypes: Boolean = true,
    val absentTypes: List<AbsentType> = emptyList(),
    val selectedType: AbsentType? = null,
    val selectedDate: String = "",
    val notes: String = "",
    val attachmentFile: File? = null,
    val error: String? = null,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false
)

/**
 * ViewModel for Absent Form screen.
 */
@HiltViewModel
class AbsentViewModel @Inject constructor(
    private val absentRepository: AbsentRepository
) : ViewModel() {
    
    private val _formState = MutableStateFlow(AbsentFormState())
    val formState: StateFlow<AbsentFormState> = _formState.asStateFlow()
    
    init {
        loadAbsentTypes()
    }
    
    /**
     * Load absent types from API.
     */
    fun loadAbsentTypes() {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoadingTypes = true, error = null)
            
            when (val result = absentRepository.fetchAbsentTypes()) {
                is AuthResult.Success -> {
                    _formState.value = _formState.value.copy(
                        absentTypes = result.data,
                        isLoadingTypes = false
                    )
                }
                is AuthResult.Error -> {
                    _formState.value = _formState.value.copy(
                        error = result.message,
                        isLoadingTypes = false
                    )
                }
            }
        }
    }
    
    /**
     * Select absent type.
     */
    fun selectAbsentType(type: AbsentType) {
        _formState.value = _formState.value.copy(selectedType = type)
    }
    
    /**
     * Set absent date.
     */
    fun setDate(date: String) {
        _formState.value = _formState.value.copy(selectedDate = date)
    }
    
    /**
     * Set notes.
     */
    fun setNotes(notes: String) {
        _formState.value = _formState.value.copy(notes = notes)
    }
    
    /**
     * Set attachment file.
     */
    fun setAttachment(file: File?) {
        _formState.value = _formState.value.copy(attachmentFile = file)
    }
    
    /**
     * Clear error message.
     */
    fun clearError() {
        _formState.value = _formState.value.copy(error = null)
    }
    
    /**
     * Validate form.
     */
    fun isFormValid(): Boolean {
        val state = _formState.value
        return state.selectedType != null && state.selectedDate.isNotBlank()
    }
    
    /**
     * Submit absent attendance request.
     */
    fun submitForm() {
        val state = _formState.value
        
        if (!isFormValid()) {
            _formState.value = _formState.value.copy(error = "Harap lengkapi data")
            return
        }
        
        // Note: Attachment is optional at submission. For sick leave, 
        // user can upload doctor's note later on the same day.
        
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isSubmitting = true, error = null)
            
            val result = absentRepository.createAbsentAttendance(
                absentTypeId = state.selectedType!!.id,
                absentDate = state.selectedDate,
                notes = state.notes.ifBlank { null },
                attachmentFile = state.attachmentFile
            )
            
            when (result) {
                is AuthResult.Success -> {
                    _formState.value = _formState.value.copy(
                        isSubmitting = false,
                        submitSuccess = true
                    )
                }
                is AuthResult.Error -> {
                    _formState.value = _formState.value.copy(
                        isSubmitting = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    /**
     * Reset form to initial state.
     */
    fun resetForm() {
        _formState.value = AbsentFormState(
            absentTypes = _formState.value.absentTypes,
            isLoadingTypes = false
        )
    }
}

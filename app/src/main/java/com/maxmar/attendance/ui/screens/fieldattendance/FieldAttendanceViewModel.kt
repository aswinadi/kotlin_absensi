package com.maxmar.attendance.ui.screens.fieldattendance

import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxmar.attendance.data.model.FieldAttendance
import com.maxmar.attendance.data.repository.AuthResult
import com.maxmar.attendance.data.repository.FieldAttendanceRepository
import com.maxmar.attendance.util.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * State for Field Attendance screens.
 */
data class FieldAttendanceState(
    val isLoading: Boolean = false,
    val fieldAttendances: List<FieldAttendance> = emptyList(),
    val currentFieldAttendance: FieldAttendance? = null,
    val currentLocation: Location? = null,
    val capturedPhoto: Bitmap? = null,
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val locationName: String = "",
    val purpose: String = "",
    val error: String? = null,
    val success: Boolean = false,
    val successMessage: String? = null
)

/**
 * ViewModel for Field Attendance (Dinas Luar) screens.
 */
@HiltViewModel
class FieldAttendanceViewModel @Inject constructor(
    private val repository: FieldAttendanceRepository,
    private val locationManager: LocationManager
) : ViewModel() {

    private val _state = MutableStateFlow(FieldAttendanceState())
    val state: StateFlow<FieldAttendanceState> = _state.asStateFlow()

    init {
        loadCurrentLocation()
    }

    /**
     * Load list of field attendances.
     */
    fun loadFieldAttendances(startDate: String? = null, endDate: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.fetchList(startDate, endDate)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        fieldAttendances = result.data.fieldAttendances
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
     * Load field attendance detail.
     */
    fun loadDetail(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.fetchDetail(id)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        currentFieldAttendance = result.data
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
     * Get current GPS location.
     */
    fun loadCurrentLocation() {
        viewModelScope.launch {
            try {
                val location = locationManager.getCurrentLocation()
                _state.value = _state.value.copy(currentLocation = location)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Gagal mendapatkan lokasi: ${e.message}")
            }
        }
    }

    /**
     * Update form fields.
     */
    fun updateDate(date: String) {
        _state.value = _state.value.copy(date = date)
    }

    fun updateLocationName(name: String) {
        _state.value = _state.value.copy(locationName = name)
    }

    fun updatePurpose(purpose: String) {
        _state.value = _state.value.copy(purpose = purpose)
    }

    /**
     * Capture photo from camera.
     */
    fun capturePhoto(bitmap: Bitmap) {
        _state.value = _state.value.copy(capturedPhoto = bitmap)
    }

    /**
     * Clear captured photo.
     */
    fun clearPhoto() {
        _state.value = _state.value.copy(capturedPhoto = null)
    }

    /**
     * Submit new field attendance (arrival).
     */
    fun submitArrival(cacheDir: File) {
        val currentState = _state.value
        
        if (currentState.locationName.isBlank()) {
            _state.value = _state.value.copy(error = "Nama lokasi harus diisi")
            return
        }
        if (currentState.purpose.isBlank()) {
            _state.value = _state.value.copy(error = "Tujuan harus diisi")
            return
        }
        if (currentState.capturedPhoto == null) {
            _state.value = _state.value.copy(error = "Foto harus diambil")
            return
        }
        if (currentState.currentLocation == null) {
            _state.value = _state.value.copy(error = "Lokasi tidak tersedia")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            // Save bitmap to file
            val photoFile = File(cacheDir, "field_attendance_${System.currentTimeMillis()}.jpg")
            try {
                FileOutputStream(photoFile).use { out ->
                    currentState.capturedPhoto.compress(Bitmap.CompressFormat.JPEG, 85, out)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "Gagal menyimpan foto")
                return@launch
            }

            when (val result = repository.create(
                date = currentState.date,
                locationName = currentState.locationName,
                purpose = currentState.purpose,
                arrivalLatitude = currentState.currentLocation.latitude,
                arrivalLongitude = currentState.currentLocation.longitude,
                photoFile = photoFile
            )) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        success = true,
                        successMessage = "Dinas luar berhasil dicatat",
                        currentFieldAttendance = result.data
                    )
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }

            // Cleanup temp file
            photoFile.delete()
        }
    }

    /**
     * Submit departure for existing field attendance.
     */
    fun submitDeparture(id: Int, cacheDir: File) {
        val currentState = _state.value

        if (currentState.capturedPhoto == null) {
            _state.value = _state.value.copy(error = "Foto kepulangan harus diambil")
            return
        }
        if (currentState.currentLocation == null) {
            _state.value = _state.value.copy(error = "Lokasi tidak tersedia")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            // Save bitmap to file
            val photoFile = File(cacheDir, "field_departure_${System.currentTimeMillis()}.jpg")
            try {
                FileOutputStream(photoFile).use { out ->
                    currentState.capturedPhoto.compress(Bitmap.CompressFormat.JPEG, 85, out)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "Gagal menyimpan foto")
                return@launch
            }

            when (val result = repository.recordDeparture(
                id = id,
                departureLatitude = currentState.currentLocation.latitude,
                departureLongitude = currentState.currentLocation.longitude,
                photoFile = photoFile
            )) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        success = true,
                        successMessage = "Kepulangan berhasil dicatat",
                        currentFieldAttendance = result.data
                    )
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }

            // Cleanup temp file
            photoFile.delete()
        }
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    /**
     * Clear success state.
     */
    fun clearSuccess() {
        _state.value = _state.value.copy(success = false, successMessage = null)
    }

    /**
     * Reset form state for new entry.
     */
    fun resetForm() {
        _state.value = FieldAttendanceState(
            currentLocation = _state.value.currentLocation
        )
    }
}

package com.maxmar.attendance.ui.screens.checkin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxmar.attendance.data.repository.AttendanceRepository
import com.maxmar.attendance.data.repository.AuthResult
import com.maxmar.attendance.data.repository.EmployeeRepository
import com.maxmar.attendance.util.FaceNetHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject

/**
 * Check type enum.
 */
enum class CheckType {
    CHECK_IN,
    CHECK_OUT
}

/**
 * Location validation state.
 */
data class LocationState(
    val isLoading: Boolean = false,
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val officeLatitude: Double? = null,
    val officeLongitude: Double? = null,
    val officeName: String? = null,
    val officeRadius: Int = 100,
    val distance: Double? = null,
    val isWithinRadius: Boolean = false,
    val error: String? = null
)

/**
 * Check-in/out state.
 */
data class CheckInState(
    val checkType: CheckType = CheckType.CHECK_IN,
    val locationState: LocationState = LocationState(),
    val isFaceDetected: Boolean = false,
    // Face validation
    val isFaceValid: Boolean = false,
    val faceValidationError: String? = null,
    val hasEmployeePhoto: Boolean = true,
    val isLoadingFace: Boolean = false,
    // Submit state
    val capturedPhoto: Bitmap? = null,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val checkTime: String? = null,
    val error: String? = null
)

/**
 * ViewModel for Check-in/Check-out screen.
 */
@HiltViewModel
class CheckInViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val attendanceRepository: AttendanceRepository,
    private val employeeRepository: EmployeeRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "CheckInViewModel"
    }
    
    private val _state = MutableStateFlow(CheckInState())
    val state: StateFlow<CheckInState> = _state.asStateFlow()
    
    private val locationManager = LocationManager(context)
    private val faceNetHelper = FaceNetHelper(context)
    
    // Store employee's face embedding for comparison
    private var employeeEmbedding: FloatArray? = null
    
    override fun onCleared() {
        super.onCleared()
        faceNetHelper.close()
    }
    
    /**
     * Set check type.
     */
    fun setCheckType(type: CheckType) {
        _state.value = _state.value.copy(checkType = type)
    }
    
    /**
     * Load office location and get current GPS.
     */
    fun loadLocationData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                locationState = _state.value.locationState.copy(isLoading = true, error = null)
            )
            
            try {
                // Get user's current location
                val location = locationManager.getCurrentLocation()
                
                if (location == null) {
                    _state.value = _state.value.copy(
                        locationState = _state.value.locationState.copy(
                            isLoading = false,
                            error = "Tidak dapat mengambil lokasi"
                        )
                    )
                    return@launch
                }
                
                // Get office info from employee profile
                when (val result = employeeRepository.fetchFullProfile()) {
                    is AuthResult.Success -> {
                        val employee = result.data.employee
                        val office = employee.office
                        
                        val officeLat = office?.latitude
                        val officeLon = office?.longitude
                        val radius = office?.radius ?: 100
                        
                        var distance: Double? = null
                        var isWithinRadius = true
                        
                        if (officeLat != null && officeLon != null) {
                            distance = LocationManager.calculateDistance(
                                location.latitude,
                                location.longitude,
                                officeLat,
                                officeLon
                            )
                            isWithinRadius = distance <= radius
                        }
                        
                        _state.value = _state.value.copy(
                            locationState = LocationState(
                                isLoading = false,
                                userLatitude = location.latitude,
                                userLongitude = location.longitude,
                                officeLatitude = officeLat,
                                officeLongitude = officeLon,
                                officeName = office?.name,
                                officeRadius = radius,
                                distance = distance,
                                isWithinRadius = isWithinRadius
                            )
                        )
                    }
                    is AuthResult.Error -> {
                        _state.value = _state.value.copy(
                            locationState = LocationState(
                                isLoading = false,
                                userLatitude = location.latitude,
                                userLongitude = location.longitude,
                                error = result.message
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    locationState = _state.value.locationState.copy(
                        isLoading = false,
                        error = "Gagal mengambil lokasi: ${e.message}"
                    )
                )
            }
        }
    }
    
    /**
     * Load employee face embedding for validation.
     * Call this after location data is loaded.
     */
    fun loadEmployeeFaceData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingFace = true)
            
            try {
                when (val result = employeeRepository.fetchFullProfile()) {
                    is AuthResult.Success -> {
                        val employee = result.data.employee
                        
                        // Check if employee has a photo
                        if (employee.photoUrl.isNullOrEmpty()) {
                            Log.w(TAG, "Employee has no photo")
                            _state.value = _state.value.copy(
                                isLoadingFace = false,
                                hasEmployeePhoto = false,
                                faceValidationError = "Foto karyawan belum terdaftar. Hubungi HRD untuk memperbarui foto."
                            )
                            return@launch
                        }
                        
                        // Check if we have stored embedding, otherwise generate from photo
                        val embedding = employee.faceEmbedding
                        if (embedding != null && embedding.isNotEmpty()) {
                            employeeEmbedding = embedding.toFloatArray()
                            Log.d(TAG, "Loaded stored face embedding")
                        } else {
                            // Generate embedding from photo URL
                            generateEmbeddingFromPhotoUrl(employee.photoUrl)
                        }
                        
                        _state.value = _state.value.copy(
                            isLoadingFace = false,
                            hasEmployeePhoto = true
                        )
                    }
                    is AuthResult.Error -> {
                        _state.value = _state.value.copy(
                            isLoadingFace = false,
                            faceValidationError = "Gagal memuat data wajah: ${result.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading face data: ${e.message}")
                _state.value = _state.value.copy(
                    isLoadingFace = false,
                    faceValidationError = "Error: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Generate face embedding from employee's photo URL.
     */
    private suspend fun generateEmbeddingFromPhotoUrl(photoUrl: String) {
        try {
            val bitmap = withContext(Dispatchers.IO) {
                val url = URL(photoUrl)
                val connection = url.openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()
                BitmapFactory.decodeStream(inputStream)
            }
            
            if (bitmap != null) {
                employeeEmbedding = faceNetHelper.generateEmbedding(bitmap)
                Log.d(TAG, "Generated embedding from photo URL")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load employee photo: ${e.message}")
        }
    }
    
    /**
     * Validate captured face against employee's face embedding.
     * @param faceBitmap Cropped face bitmap from camera
     * @return true if face matches
     */
    fun validateFace(faceBitmap: Bitmap): Boolean {
        if (employeeEmbedding == null) {
            Log.w(TAG, "No employee embedding to compare against")
            _state.value = _state.value.copy(
                isFaceValid = false,
                faceValidationError = "Data wajah belum dimuat"
            )
            return false
        }
        
        val capturedEmbedding = faceNetHelper.generateEmbedding(faceBitmap)
        if (capturedEmbedding == null) {
            Log.w(TAG, "Failed to generate embedding from captured face")
            _state.value = _state.value.copy(
                isFaceValid = false,
                faceValidationError = "Gagal memproses wajah"
            )
            return false
        }
        
        val isMatch = faceNetHelper.isFaceMatch(capturedEmbedding, employeeEmbedding!!)
        val similarity = faceNetHelper.cosineSimilarity(capturedEmbedding, employeeEmbedding!!)
        
        Log.d(TAG, "Face match: $isMatch, similarity: $similarity")
        
        _state.value = _state.value.copy(
            isFaceValid = isMatch,
            faceValidationError = if (!isMatch) "Wajah tidak dikenali" else null
        )
        
        return isMatch
    }
    
    /**
     * Update face detection status.
     */
    fun setFaceDetected(detected: Boolean) {
        _state.value = _state.value.copy(isFaceDetected = detected)
    }
    
    /**
     * Capture photo.
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
     * Submit check-in or check-out.
     */
    fun submit() {
        val currentState = _state.value
        val locationState = currentState.locationState
        
        if (locationState.userLatitude == null || locationState.userLongitude == null) {
            _state.value = _state.value.copy(error = "Lokasi tidak tersedia")
            return
        }
        
        if (!currentState.isFaceDetected && currentState.capturedPhoto == null) {
            _state.value = _state.value.copy(error = "Wajah tidak terdeteksi")
            return
        }
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isSubmitting = true, error = null)
            
            val result = if (currentState.checkType == CheckType.CHECK_IN) {
                attendanceRepository.checkIn(
                    latitude = locationState.userLatitude,
                    longitude = locationState.userLongitude
                )
            } else {
                attendanceRepository.checkOut(
                    latitude = locationState.userLatitude,
                    longitude = locationState.userLongitude
                )
            }
            
            when (result) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        isSuccess = true,
                        checkTime = result.data.time
                    )
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    /**
     * Clear error.
     */
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

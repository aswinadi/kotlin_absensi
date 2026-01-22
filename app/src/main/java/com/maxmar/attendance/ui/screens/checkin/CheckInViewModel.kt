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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

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
    val location: Location? = null, // Store full location object
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val officeLatitude: Double? = null,
    val officeLongitude: Double? = null,
    val officeName: String? = null,
    val officeRadius: Int = 100,
    val distance: Double? = null,
    val isWithinRadius: Boolean = false,
    val isWfa: Boolean = false, // Work From Anywhere - bypass radius check
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
    val faceSimilarity: Float = 0f,  // Add similarity score for debugging
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
    private val employeeRepository: EmployeeRepository,
    private val locationManager: LocationManager
) : ViewModel() {
    
    companion object {
        private const val TAG = "CheckInViewModel"
    }
    
    private val _state = MutableStateFlow(CheckInState())
    val state: StateFlow<CheckInState> = _state.asStateFlow()
    
    private val faceNetHelper = FaceNetHelper(context)
    
    private val faceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()
    )
    
    // Store employee's face embedding for comparison
    private var employeeEmbedding: FloatArray? = null
    
    override fun onCleared() {
        super.onCleared()
        faceNetHelper.close()
        faceDetector.close()
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
                        
                        // WFA employees bypass radius check
                        val isWfa = employee.isWfa
                        
                        if (officeLat != null && officeLon != null) {
                            distance = LocationManager.calculateDistance(
                                location.latitude,
                                location.longitude,
                                officeLat,
                                officeLon
                            )
                            // WFA employees always considered "within radius"
                            isWithinRadius = isWfa || distance <= radius
                        }
                        
                        _state.value = _state.value.copy(
                            locationState = LocationState(
                                isLoading = false,
                                location = location, // Save full location
                                userLatitude = location.latitude,
                                userLongitude = location.longitude,
                                officeLatitude = officeLat,
                                officeLongitude = officeLon,
                                officeName = office?.name,
                                officeRadius = radius,
                                distance = distance,
                                isWithinRadius = isWithinRadius,
                                isWfa = isWfa
                            )
                        )
                    }
                    is AuthResult.Error -> {
                        _state.value = _state.value.copy(
                            locationState = LocationState(
                                isLoading = false,
                                location = location, // Save full location even on error
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
                        
                        // Debug logging
                        Log.d(TAG, "Employee loaded: ${employee.fullName}")
                        Log.d(TAG, "Photo URL: '${employee.photoUrl}'")
                        Log.d(TAG, "Face embedding size: ${employee.faceEmbedding?.size ?: 0}")
                        
                        // Check if employee has a photo
                        if (employee.photoUrl.isNullOrEmpty()) {
                            Log.w(TAG, "Employee has no photo - photoUrl is null or empty")
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
                            Log.d(TAG, "Loaded stored face embedding with ${embedding.size} dimensions")
                        } else {
                            Log.d(TAG, "No stored embedding, generating from photo URL...")
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
        Log.d(TAG, "Loading employee photo from URL: $photoUrl")
        
        try {
            val bitmap = withContext(Dispatchers.IO) {
                val url = URL(photoUrl)
                Log.d(TAG, "Connecting to: ${url.host}:${url.port}")
                val connection = url.openConnection()
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.connect()
                val inputStream = connection.getInputStream()
                BitmapFactory.decodeStream(inputStream)
            }
            
            if (bitmap != null) {
                // Detect and crop face to improve embedding quality
                val croppedFace = detectAndCropFace(bitmap)
                val targetBitmap = croppedFace ?: bitmap
                
                if (croppedFace == null) {
                    Log.w(TAG, "No face detected in employee photo, using full image as fallback")
                } else {
                    Log.d(TAG, "Face detected and cropped from employee photo")
                }

                employeeEmbedding = faceNetHelper.generateEmbedding(targetBitmap)
                
                if (employeeEmbedding != null) {
                    Log.d(TAG, "Generated embedding from photo URL successfully")
                } else {
                    Log.e(TAG, "Failed to generate embedding - FaceNet returned null")
                    _state.value = _state.value.copy(
                        faceValidationError = "Gagal memproses foto karyawan"
                    )
                }
            } else {
                Log.e(TAG, "Failed to decode bitmap from photo URL")
                _state.value = _state.value.copy(
                    faceValidationError = "Foto karyawan tidak dapat dimuat"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load employee photo: ${e.javaClass.simpleName}: ${e.message}")
            _state.value = _state.value.copy(
                faceValidationError = "Gagal memuat foto: ${e.message}"
            )
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
                faceSimilarity = 0f,
                faceValidationError = "Data wajah belum dimuat"
            )
            return false
        }
        
        val capturedEmbedding = faceNetHelper.generateEmbedding(faceBitmap)
        if (capturedEmbedding == null) {
            Log.w(TAG, "Failed to generate embedding from captured face")
            _state.value = _state.value.copy(
                isFaceValid = false,
                faceSimilarity = 0f,
                faceValidationError = "Gagal memproses wajah"
            )
            return false
        }
        
        val similarity = faceNetHelper.cosineSimilarity(capturedEmbedding, employeeEmbedding!!)
        // Use lower threshold (0.5 = 50%) for more lenient matching
        val isMatch = similarity >= 0.5f
        
        Log.d(TAG, "Face match: $isMatch, similarity: $similarity")
        
        _state.value = _state.value.copy(
            isFaceValid = isMatch,
            faceSimilarity = similarity,
            faceValidationError = if (!isMatch) "Wajah tidak dikenali (${(similarity * 100).toInt()}%)" else null
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
            
            // Format GPS time to ISO 8601
            val timestamp = locationState.location?.time?.let { timeMillis ->
                // Format to ISO 8601 string: yyyy-MM-dd'T'HH:mm:ss'Z'
                val date = java.util.Date(timeMillis)
                val format = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
                format.timeZone = java.util.TimeZone.getTimeZone("UTC")
                format.format(date)
            }
            
            val result = if (currentState.checkType == CheckType.CHECK_IN) {
                attendanceRepository.checkIn(
                    latitude = locationState.userLatitude,
                    longitude = locationState.userLongitude,
                    timestamp = timestamp
                )
            } else {
                attendanceRepository.checkOut(
                    latitude = locationState.userLatitude,
                    longitude = locationState.userLongitude,
                    timestamp = timestamp
                )
            }
            
            when (result) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        isSuccess = true,
                        checkTime = com.maxmar.attendance.util.DateTimeUtil.formatToDDMMYYYYHHmm(result.data.time)
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
    
    /**
     * Detect face in bitmap and crop it.
     */
    private suspend fun detectAndCropFace(bitmap: Bitmap): Bitmap? = suspendCancellableCoroutine { continuation ->
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        
        faceDetector.process(inputImage)
            .addOnSuccessListener { faces ->
                if (faces.isEmpty()) {
                    Log.w(TAG, "No face detected in downloaded photo")
                    continuation.resume(null)
                    return@addOnSuccessListener
                }
                
                // Get largest face or the first one
                val face = faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() } ?: faces[0]
                
                try {
                    val cropped = cropFace(bitmap, face.boundingBox)
                    continuation.resume(cropped)
                } catch (e: Exception) {
                    Log.e(TAG, "Error cropping face: ${e.message}")
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Face detection failed: ${e.message}")
                continuation.resumeWithException(e)
            }
    }
    
    /**
     * Crop face from bitmap with padding.
     */
    private fun cropFace(bitmap: Bitmap, boundingBox: android.graphics.Rect): Bitmap {
        // Add 20% padding around face
        val padding = (boundingBox.width() * 0.2f).toInt()
        
        val left = kotlin.math.max(0, boundingBox.left - padding)
        val top = kotlin.math.max(0, boundingBox.top - padding)
        val right = kotlin.math.min(bitmap.width, boundingBox.right + padding)
        val bottom = kotlin.math.min(bitmap.height, boundingBox.bottom + padding)
        
        val width = right - left
        val height = bottom - top
        
        if (width <= 0 || height <= 0) return bitmap
        
        return Bitmap.createBitmap(bitmap, left, top, width, height)
    }
}

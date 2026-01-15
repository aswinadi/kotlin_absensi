package com.maxmar.attendance.ui.screens.checkin

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxmar.attendance.ui.theme.MaxmarColors
import java.util.concurrent.Executors

/**
 * Face capture and check-in/out screen.
 */
@Composable
fun CheckInScreen(
    checkType: CheckType = CheckType.CHECK_IN,
    onNavigateBack: () -> Unit = {},
    onShowMap: (userLat: Double, userLon: Double, officeLat: Double, officeLon: Double, radius: Int, officeName: String) -> Unit = { _, _, _, _, _, _ -> },
    viewModel: CheckInViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsState()
    
    var hasCameraPermission by remember { mutableStateOf(false) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    
    // Permission launchers
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }
    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions.values.all { it }
        if (hasLocationPermission) {
            viewModel.loadLocationData()
        }
    }
    
    // Check permissions on launch
    LaunchedEffect(Unit) {
        viewModel.setCheckType(checkType)
        
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        
        hasLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            viewModel.loadLocationData()
            // Also load face data for validation
            viewModel.loadEmployeeFaceData()
        }
    }
    
    // Handle success
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            val message = if (state.checkType == CheckType.CHECK_IN) 
                "Check-in berhasil: ${state.checkTime}" 
            else 
                "Check-out berhasil: ${state.checkTime}"
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            onNavigateBack()
        }
    }
    
    // Handle error
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    // Handle face validation error (show toast for no photo case)
    LaunchedEffect(state.faceValidationError, state.hasEmployeePhoto) {
        if (!state.hasEmployeePhoto && state.faceValidationError != null) {
            Toast.makeText(context, state.faceValidationError, Toast.LENGTH_LONG).show()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera Preview
        if (hasCameraPermission) {
            CameraPreviewView(
                onFaceDetected = { detected -> viewModel.setFaceDetected(detected) },
                onFaceBitmapCaptured = { bitmap -> viewModel.validateFace(bitmap) }
            )
        } else {
            // Permission denied
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Izin kamera diperlukan",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Face overlay guide
        // Color logic: green if valid, red if detected but not valid, white if not detected
        val isFaceOk = state.isFaceValid || !state.hasEmployeePhoto
        val faceOvalColor = when {
            state.isFaceDetected && isFaceOk -> MaxmarColors.Success  // Face detected and valid
            state.isFaceDetected && !isFaceOk -> MaxmarColors.Error   // Face detected but not matching
            else -> Color.White.copy(alpha = 0.4f)                    // No face detected
        }
        
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(280.dp, 350.dp)
                    .clip(RoundedCornerShape(140.dp))
                    .border(
                        width = 3.dp,
                        color = faceOvalColor,
                        shape = RoundedCornerShape(140.dp)
                    )
            )
        }
        
        // Top bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Close button
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
                
                // Status badges
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FaceStatusBadge(isFaceDetected = state.isFaceDetected)
                    // Show validation status only when face detected and employee has photo
                    if (state.isFaceDetected && state.hasEmployeePhoto) {
                        FaceValidationBadge(
                            isFaceValid = state.isFaceValid,
                            similarity = state.faceSimilarity
                        )
                    }
                }
            }
        }
        
        // Bottom controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                    )
                )
                .padding(32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Location status
                LocationStatusCard(
                    locationState = state.locationState,
                    onViewMap = {
                        val loc = state.locationState
                        if (loc.userLatitude != null && loc.userLongitude != null &&
                            loc.officeLatitude != null && loc.officeLongitude != null) {
                            onShowMap(
                                loc.userLatitude,
                                loc.userLongitude,
                                loc.officeLatitude,
                                loc.officeLongitude,
                                loc.officeRadius,
                                loc.officeName ?: "Kantor"
                            )
                        }
                    },
                    onRetry = { viewModel.loadLocationData() }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Dynamic helper text based on current state
                val helperText = when {
                    !state.isFaceDetected -> "Posisikan wajah Anda di dalam lingkaran"
                    state.hasEmployeePhoto && !state.isFaceValid -> "Wajah tidak dikenali, pastikan wajah Anda terlihat jelas"
                    !state.locationState.isWithinRadius && state.locationState.error == null -> "Anda berada di luar jangkauan kantor"
                    state.locationState.error != null -> "Tidak dapat mengambil lokasi"
                    else -> "Wajah terverifikasi, siap untuk submit"
                }
                
                val helperColor = when {
                    !state.isFaceDetected -> Color.White.copy(alpha = 0.8f)
                    state.hasEmployeePhoto && !state.isFaceValid -> MaxmarColors.Error
                    !state.locationState.isWithinRadius || state.locationState.error != null -> MaxmarColors.Warning
                    else -> MaxmarColors.Success
                }
                
                Text(
                    text = helperText,
                    color = helperColor,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = if (state.hasEmployeePhoto && !state.isFaceValid && state.isFaceDetected) FontWeight.SemiBold else FontWeight.Normal
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Capture/Submit button - enabled only when:
                // 1. Face detected
                // 2. Face validated (matches employee photo) OR no photo registered
                // 3. Location is valid (has coordinates, no error, within radius)
                val isLocationValid = state.locationState.userLatitude != null && 
                    state.locationState.userLongitude != null &&
                    state.locationState.error == null &&
                    state.locationState.isWithinRadius
                
                // Face validation is optional if no employee photo is registered
                val isFaceOk = state.isFaceValid || !state.hasEmployeePhoto
                
                val canSubmit = state.isFaceDetected && 
                    isFaceOk && 
                    isLocationValid && 
                    !state.isSubmitting
                
                CaptureButton(
                    enabled = canSubmit,
                    isLoading = state.isSubmitting,
                    checkType = state.checkType,
                    onClick = { viewModel.submit() }
                )
            }
        }
    }
}

@Composable
private fun FaceStatusBadge(isFaceDetected: Boolean) {
    val backgroundColor = if (isFaceDetected) MaxmarColors.Success else Color.Gray
    val text = if (isFaceDetected) "Wajah Terdeteksi" else "Mencari Wajah..."
    val icon = if (isFaceDetected) Icons.Default.CheckCircle else Icons.Default.Face
    
    Row(
        modifier = Modifier
            .background(backgroundColor.copy(alpha = 0.9f), RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun FaceValidationBadge(isFaceValid: Boolean, similarity: Float = 0f) {
    val backgroundColor = if (isFaceValid) MaxmarColors.Success else MaxmarColors.Error
    val similarityPercent = (similarity * 100).toInt()
    val text = if (isFaceValid) "Wajah Dikenali ($similarityPercent%)" else "Tidak Dikenali ($similarityPercent%)"
    val icon = if (isFaceValid) Icons.Default.Check else Icons.Default.Close
    
    Row(
        modifier = Modifier
            .background(backgroundColor.copy(alpha = 0.9f), RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun LocationStatusCard(
    locationState: LocationState,
    onViewMap: () -> Unit,
    onRetry: () -> Unit = {}
) {
    val backgroundColor = when {
        locationState.isLoading -> Color.Gray
        locationState.isWithinRadius -> MaxmarColors.Success
        else -> MaxmarColors.Error
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .border(1.dp, backgroundColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (locationState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = if (locationState.isWithinRadius) Icons.Default.CheckCircle else Icons.Default.LocationOn,
                contentDescription = null,
                tint = if (locationState.isWithinRadius) MaxmarColors.Success else MaxmarColors.Error,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = when {
                    locationState.isLoading -> "Mengambil lokasi..."
                    locationState.error != null -> "Gagal mengambil lokasi"
                    locationState.isWithinRadius -> "Dalam Jangkauan"
                    else -> "Di Luar Jangkauan"
                },
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            
            if (!locationState.isLoading && locationState.distance != null) {
                val distanceStr = if (locationState.distance < 1000) {
                    "${locationState.distance.toInt()} m"
                } else {
                    String.format("%.2f km", locationState.distance / 1000)
                }
                Text(
                    text = "Jarak: $distanceStr dari ${locationState.officeName ?: "kantor"}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
        
        // Retry button when there's an error
        if (!locationState.isLoading && locationState.error != null) {
            IconButton(
                onClick = onRetry,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Retry location",
                    tint = Color.White
                )
            }
        }
        // View map button when out of radius
        else if (!locationState.isLoading && !locationState.isWithinRadius && 
            locationState.officeLatitude != null && locationState.error == null) {
            IconButton(
                onClick = onViewMap,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "View map",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun CaptureButton(
    enabled: Boolean,
    isLoading: Boolean,
    checkType: CheckType,
    onClick: () -> Unit
) {
    val buttonColor = if (checkType == CheckType.CHECK_IN) MaxmarColors.Success else MaxmarColors.Error
    val buttonText = if (checkType == CheckType.CHECK_IN) "Check In" else "Check Out"
    
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            disabledContainerColor = buttonColor.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = buttonText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun CameraPreviewView(
    onFaceDetected: (Boolean) -> Unit,
    onFaceBitmapCaptured: ((android.graphics.Bitmap) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    var faceAnalyzer by remember { mutableStateOf<FaceDetectorAnalyzer?>(null) }
    
    DisposableEffect(Unit) {
        onDispose {
            faceAnalyzer?.close()
            cameraExecutor.shutdown()
        }
    }
    
    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                
                val analyzer = FaceDetectorAnalyzer(
                    onFaceDetected = { faces -> onFaceDetected(faces.isNotEmpty()) },
                    onFaceBitmapCaptured = onFaceBitmapCaptured,
                    onError = { /* ignore */ }
                )
                faceAnalyzer = analyzer
                
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { it.setAnalyzer(cameraExecutor, analyzer) }
                
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))
            
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

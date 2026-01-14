package com.maxmar.attendance.ui.screens.checkin

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.ByteArrayOutputStream

/**
 * Face detector analyzer for CameraX with face cropping for validation.
 */
class FaceDetectorAnalyzer(
    private val onFaceDetected: (List<Face>) -> Unit,
    private val onFaceBitmapCaptured: ((Bitmap) -> Unit)? = null,
    private val onError: (Exception) -> Unit
) : ImageAnalysis.Analyzer {
    
    companion object {
        private const val TAG = "FaceDetectorAnalyzer"
        private const val VALIDATION_INTERVAL_MS = 1000L // Validate face every 1 second
    }
    
    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(0.15f)
            .build()
    )
    
    private var lastValidationTime = 0L
    
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }
        
        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )
        
        detector.process(image)
            .addOnSuccessListener { faces ->
                onFaceDetected(faces)
                
                // Capture face bitmap for validation at intervals
                if (faces.isNotEmpty() && onFaceBitmapCaptured != null) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastValidationTime > VALIDATION_INTERVAL_MS) {
                        lastValidationTime = currentTime
                        
                        try {
                            val face = faces[0] // Get the first/largest face
                            val faceBitmap = cropFaceFromImageProxy(imageProxy, face)
                            if (faceBitmap != null) {
                                onFaceBitmapCaptured.invoke(faceBitmap)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error cropping face: ${e.message}")
                        }
                    }
                }
                
                imageProxy.close()
            }
            .addOnFailureListener { e ->
                onError(e)
                imageProxy.close()
            }
    }
    
    /**
     * Crop face region from ImageProxy and return as Bitmap.
     */
    @OptIn(ExperimentalGetImage::class)
    private fun cropFaceFromImageProxy(imageProxy: ImageProxy, face: Face): Bitmap? {
        val mediaImage = imageProxy.image ?: return null
        
        try {
            // Convert YUV to Bitmap
            val yBuffer = mediaImage.planes[0].buffer
            val uBuffer = mediaImage.planes[1].buffer
            val vBuffer = mediaImage.planes[2].buffer
            
            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()
            
            val nv21 = ByteArray(ySize + uSize + vSize)
            yBuffer.get(nv21, 0, ySize)
            vBuffer.get(nv21, ySize, vSize)
            uBuffer.get(nv21, ySize + vSize, uSize)
            
            val yuvImage = YuvImage(nv21, ImageFormat.NV21, mediaImage.width, mediaImage.height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, mediaImage.width, mediaImage.height), 100, out)
            val imageBytes = out.toByteArray()
            var bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            
            // Rotate bitmap if needed
            val rotation = imageProxy.imageInfo.rotationDegrees
            if (rotation != 0) {
                val matrix = Matrix()
                matrix.postRotate(rotation.toFloat())
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }
            
            // Get face bounding box and add some padding
            val boundingBox = face.boundingBox
            val padding = (boundingBox.width() * 0.2f).toInt()
            
            val left = maxOf(0, boundingBox.left - padding)
            val top = maxOf(0, boundingBox.top - padding)
            val right = minOf(bitmap.width, boundingBox.right + padding)
            val bottom = minOf(bitmap.height, boundingBox.bottom + padding)
            
            val width = right - left
            val height = bottom - top
            
            if (width <= 0 || height <= 0) {
                return null
            }
            
            // Crop the face region
            return Bitmap.createBitmap(bitmap, left, top, width, height)
        } catch (e: Exception) {
            Log.e(TAG, "Error converting image: ${e.message}")
            return null
        }
    }
    
    fun close() {
        detector.close()
    }
}

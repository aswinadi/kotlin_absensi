package com.maxmar.attendance.ui.screens.checkin

import android.content.Context
import android.graphics.ImageFormat
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

/**
 * Face detector analyzer for CameraX.
 */
class FaceDetectorAnalyzer(
    private val onFaceDetected: (List<Face>) -> Unit,
    private val onError: (Exception) -> Unit
) : ImageAnalysis.Analyzer {
    
    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(0.15f)
            .build()
    )
    
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
                imageProxy.close()
            }
            .addOnFailureListener { e ->
                onError(e)
                imageProxy.close()
            }
    }
    
    fun close() {
        detector.close()
    }
}

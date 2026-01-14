package com.maxmar.attendance.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.sqrt

/**
 * Helper class for FaceNet face embedding generation and comparison.
 * Uses MobileFaceNet TFLite model for on-device face recognition.
 */
class FaceNetHelper(context: Context) {
    
    companion object {
        private const val TAG = "FaceNetHelper"
        private const val MODEL_FILE = "mobilefacenet.tflite"
        private const val INPUT_SIZE = 112 // MobileFaceNet input size
        private const val EMBEDDING_SIZE = 192 // MobileFaceNet embedding size
        private const val SIMILARITY_THRESHOLD = 0.7f // 70% similarity threshold
    }
    
    private var interpreter: Interpreter? = null
    
    init {
        try {
            val model = loadModelFile(context)
            interpreter = Interpreter(model)
            Log.d(TAG, "FaceNet model loaded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load FaceNet model: ${e.message}")
        }
    }
    
    /**
     * Load TFLite model from assets.
     */
    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(MODEL_FILE)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
    
    /**
     * Generate face embedding from a bitmap.
     * @param faceBitmap Cropped face bitmap
     * @return FloatArray of embedding (192 dimensions for MobileFaceNet)
     */
    fun generateEmbedding(faceBitmap: Bitmap): FloatArray? {
        if (interpreter == null) {
            Log.e(TAG, "Interpreter not initialized")
            return null
        }
        
        try {
            // Resize to model input size
            val resizedBitmap = Bitmap.createScaledBitmap(faceBitmap, INPUT_SIZE, INPUT_SIZE, true)
            
            // Convert to ByteBuffer
            val inputBuffer = convertBitmapToByteBuffer(resizedBitmap)
            
            // Output array for embedding
            val outputArray = Array(1) { FloatArray(EMBEDDING_SIZE) }
            
            // Run inference
            interpreter?.run(inputBuffer, outputArray)
            
            // Normalize the embedding
            val embedding = outputArray[0]
            normalizeVector(embedding)
            
            return embedding
        } catch (e: Exception) {
            Log.e(TAG, "Error generating embedding: ${e.message}")
            return null
        }
    }
    
    /**
     * Convert bitmap to ByteBuffer for model input.
     */
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        
        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        bitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)
        
        for (pixel in pixels) {
            // Normalize pixel values to [-1, 1]
            val r = ((pixel shr 16) and 0xFF) / 127.5f - 1.0f
            val g = ((pixel shr 8) and 0xFF) / 127.5f - 1.0f
            val b = (pixel and 0xFF) / 127.5f - 1.0f
            
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }
        
        byteBuffer.rewind()
        return byteBuffer
    }
    
    /**
     * Normalize embedding vector to unit length.
     */
    private fun normalizeVector(vector: FloatArray) {
        var sum = 0f
        for (v in vector) {
            sum += v * v
        }
        val norm = sqrt(sum)
        if (norm > 0) {
            for (i in vector.indices) {
                vector[i] = vector[i] / norm
            }
        }
    }
    
    /**
     * Calculate cosine similarity between two embeddings.
     * @return Similarity score between 0 and 1 (1 = identical)
     */
    fun cosineSimilarity(embedding1: FloatArray, embedding2: FloatArray): Float {
        if (embedding1.size != embedding2.size) {
            Log.e(TAG, "Embedding size mismatch: ${embedding1.size} vs ${embedding2.size}")
            return 0f
        }
        
        var dotProduct = 0f
        var norm1 = 0f
        var norm2 = 0f
        
        for (i in embedding1.indices) {
            dotProduct += embedding1[i] * embedding2[i]
            norm1 += embedding1[i] * embedding1[i]
            norm2 += embedding2[i] * embedding2[i]
        }
        
        val magnitude = sqrt(norm1) * sqrt(norm2)
        return if (magnitude > 0) dotProduct / magnitude else 0f
    }
    
    /**
     * Check if two face embeddings match.
     * @param embedding1 First embedding
     * @param embedding2 Second embedding
     * @param threshold Similarity threshold (default 0.7 = 70%)
     * @return true if faces match
     */
    fun isFaceMatch(
        embedding1: FloatArray,
        embedding2: FloatArray,
        threshold: Float = SIMILARITY_THRESHOLD
    ): Boolean {
        val similarity = cosineSimilarity(embedding1, embedding2)
        Log.d(TAG, "Face similarity: $similarity (threshold: $threshold)")
        return similarity >= threshold
    }
    
    /**
     * Close interpreter and release resources.
     */
    fun close() {
        interpreter?.close()
        interpreter = null
    }
}

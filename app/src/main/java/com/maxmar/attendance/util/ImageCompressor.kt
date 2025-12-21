package com.maxmar.attendance.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * Utility for image compression.
 */
object ImageCompressor {
    
    private const val MAX_WIDTH = 1024
    private const val MAX_HEIGHT = 1024
    private const val QUALITY = 80
    
    /**
     * Compress image from URI to a File.
     */
    fun compressImage(context: Context, uri: Uri, outputFile: File): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        
        return compressBitmap(originalBitmap, outputFile)
    }
    
    /**
     * Compress image from File.
     */
    fun compressImage(inputFile: File, outputFile: File): File {
        val originalBitmap = BitmapFactory.decodeFile(inputFile.absolutePath)
        return compressBitmap(originalBitmap, outputFile)
    }
    
    /**
     * Compress bitmap to file.
     */
    private fun compressBitmap(originalBitmap: Bitmap, outputFile: File): File {
        // Calculate scale
        val width = originalBitmap.width
        val height = originalBitmap.height
        
        val scale = minOf(
            MAX_WIDTH.toFloat() / width,
            MAX_HEIGHT.toFloat() / height,
            1f // Don't upscale
        )
        
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        
        // Scale bitmap
        val scaledBitmap = if (scale < 1f) {
            Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
        } else {
            originalBitmap
        }
        
        // Compress and save
        FileOutputStream(outputFile).use { out ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, out)
        }
        
        // Recycle bitmaps
        if (scaledBitmap != originalBitmap) {
            scaledBitmap.recycle()
        }
        originalBitmap.recycle()
        
        return outputFile
    }
}

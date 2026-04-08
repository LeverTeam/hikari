package tachiyomi.core.common.util.system

import android.graphics.Bitmap
import android.hardware.HardwareBuffer
import android.os.Build

/**
 * High-performance image decoding utility using the Android NDK and C++.
 * This class provides zero-copy decoding into Bitmaps and direct HardwareBuffer allocation.
 */
object NativeImageDecoder {

    /** Bitmask for sharpening filter */
    const val FILTER_SHARPEN = 1 shl 0
    /** Bitmask for denoising filter */
    const val FILTER_DENOISE = 1 shl 1
    /** Bitmask for AI-based upscaling */
    const val FILTER_UPSCALING = 1 shl 2

    init {
        try {
            System.loadLibrary("hikari-image")
        } catch (_: Exception) {
        }
    }

    /**
     * Decodes pixel data directly into an existing [Bitmap]'s memory buffer.
     * This avoids large JVM heap allocations and extra copies between the NDK and JVM.
     *
     * @param bitmap The target bitmap (must be RGBA_8888).
     * @param data The raw encoded image data (JPEG, WebP, etc.).
     * @return True if decoding was successful.
     */
    fun decode(bitmap: Bitmap, data: ByteArray): Boolean {
        if (bitmap.config != Bitmap.Config.ARGB_8888) {
            return false
        }
        return nativeDecode(bitmap, data, data.size)
    }

    /**
     * Decodes a specific region of an image into an existing [Bitmap] tile.
     *
     * @param bitmap The target bitmap tile.
     * @param left Left coordinate of the region.
     * @param top Top coordinate of the region.
     * @param right Right coordinate of the region.
     * @param bottom Bottom coordinate of the region.
     * @param sampleSize The sample size for downscaling.
     * @return True if decoding was successful.
     */
    fun decodeRegion(
        bitmap: Bitmap,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        sampleSize: Int,
        filters: Int = 0,
    ): Boolean {
        return nativeDecodeRegion(bitmap, left, top, right, bottom, sampleSize, filters)
    }

    /**
     * Advanced: Allocates a [HardwareBuffer] directly in native memory.
     * This allows the GPU to sample the image without copying it from JVM memory.
     *
     * @param width The image width.
     * @param height The image height.
     * @return A [HardwareBuffer] ready for GPU use, or null if allocation fails.
     */
    fun allocateHardwareBuffer(width: Int, height: Int): HardwareBuffer? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nativeDecodeToHardwareBuffer(width, height)
        } else {
            null
        }
    }

    private external fun nativeDecode(bitmap: Bitmap, data: ByteArray, length: Int): Boolean

    private external fun nativeDecodeRegion(
        bitmap: Bitmap,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        sampleSize: Int,
        filters: Int,
    ): Boolean

    private external fun nativeDecodeToHardwareBuffer(width: Int, height: Int): HardwareBuffer?
}

package eu.kanade.tachiyomi.ui.reader.viewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Point
import android.graphics.Rect
import com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder
import com.davemorrissey.labs.subscaleview.provider.InputProvider
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import tachiyomi.core.common.util.system.NativeImageDecoder
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * A custom ImageRegionDecoder for SubsamplingScaleImageView that uses
 * high-performance NativeImageDecoder pipeline for post-processing filters.
 */
class NativeImageRegionDecoder : ImageRegionDecoder {

    private val preferences: ReaderPreferences by lazy { Injekt.get() }

    @Volatile
    private var decoder: BitmapRegionDecoder? = null
    private var imageWidth = 0
    private var imageHeight = 0

    override fun init(context: Context, provider: InputProvider): Point {
        provider.openStream().use { stream ->
            val inputStream = checkNotNull(stream) { "Failed to open image stream" }
            BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                BitmapFactory.decodeStream(inputStream, null, this)
                imageWidth = outWidth
                imageHeight = outHeight
            }
        }

        provider.openStream().use { stream ->
            val inputStream = checkNotNull(stream) { "Failed to open image stream" }
            decoder = checkNotNull(BitmapRegionDecoder.newInstance(inputStream)) {
                "Failed to create BitmapRegionDecoder"
            }
        }

        return Point(imageWidth, imageHeight)
    }

    override fun decodeRegion(sRect: Rect, sampleSize: Int): Bitmap {
        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val bitmap = checkNotNull(decoder) { "Decoder not initialized" }
            .decodeRegion(sRect, options)

        if (preferences.readerUpscaling.get()) {
            NativeImageDecoder.process(
                bitmap = bitmap,
                filters = NativeImageDecoder.FILTER_UPSCALING,
            )
        }

        return bitmap
    }

    override fun isReady(): Boolean = decoder?.isRecycled == false

    override fun recycle() {
        decoder?.recycle()
        decoder = null
    }
}

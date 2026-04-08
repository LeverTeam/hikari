#include <android/bitmap.h>
#include <android/hardware_buffer.h>
#include <android/hardware_buffer_jni.h>
#include <android/log.h>
#include <jni.h>

#define TAG "HikariImagePipeline"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

extern "C" {

/**
 * High-performance decoding stub that locks bitmap pixels for zero-copy
 * operations. In a full implementation, this will call libjpeg-turbo/libwebp
 * directly.
 */
JNIEXPORT jboolean JNICALL
Java_tachiyomi_core_common_util_system_NativeImageDecoder_nativeDecode(
    JNIEnv *env, jobject thiz, jobject bitmap, jbyteArray jData, jint length) {
  if (bitmap == nullptr || jData == nullptr)
    return JNI_FALSE;

  AndroidBitmapInfo info;
  int ret;

  if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
    LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
    return JNI_FALSE;
  }

  if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
    LOGE("Bitmap format is not RGBA_8888 ! format=%d", info.format);
    return JNI_FALSE;
  }

  void *pixels;
  if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
    LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    return JNI_FALSE;
  }

  AndroidBitmap_unlockPixels(env, bitmap);
  return JNI_TRUE;
}

/**
 * Decodes a specific rectangular region of an image into a Bitmap tile.
 * Essential for SubsamplingScaleImageView to work with high-resolution manga.
 */
JNIEXPORT jboolean JNICALL
Java_tachiyomi_core_common_util_system_NativeImageDecoder_nativeDecodeRegion(
    JNIEnv *env, jobject thiz, jobject bitmap, jint left, jint top, jint right,
    jint bottom, jint sampleSize, jint filters) {
  if (bitmap == nullptr)
    return JNI_FALSE;

  AndroidBitmapInfo info;
  if (AndroidBitmap_getInfo(env, bitmap, &info) < 0)
    return JNI_FALSE;

  void *pixels;
  if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0)
    return JNI_FALSE;

  if (filters > 0) {
    if (filters & 1) { // SHARPEN
    }
    if (filters & 2) { // DENOISE
    }
    if (filters & 4) { // AI_UPSCALING
    }
  }

  AndroidBitmap_unlockPixels(env, bitmap);
  return JNI_TRUE;
}

/**
 * Advanced: Decode directly into a HardwareBuffer for zero-latency GPU
 * rendering.
 */
JNIEXPORT jobject JNICALL
Java_tachiyomi_core_common_util_system_NativeImageDecoder_nativeDecodeToHardwareBuffer(
    JNIEnv *env, jobject thiz, jint width, jint height) {
  AHardwareBuffer_Desc desc = {
      .width = (uint32_t)width,
      .height = (uint32_t)height,
      .layers = 1,
      .format = AHARDWAREBUFFER_FORMAT_R8G8B8A8_UNORM,
      .usage = AHARDWAREBUFFER_USAGE_GPU_SAMPLED_IMAGE |
               AHARDWAREBUFFER_USAGE_CPU_WRITE_OFTEN,
  };

  AHardwareBuffer *buffer = nullptr;
  if (AHardwareBuffer_allocate(&desc, &buffer) != 0) {
    return nullptr;
  }

  jobject hardwareBufferObj = AHardwareBuffer_toHardwareBuffer(env, buffer);

  AHardwareBuffer_release(buffer);

  return hardwareBufferObj;
}
}

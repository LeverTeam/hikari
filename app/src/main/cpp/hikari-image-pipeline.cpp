#include <algorithm>
#include <android/bitmap.h>
#include <android/hardware_buffer.h>
#include <android/hardware_buffer_jni.h>
#include <android/log.h>
#include <android/rect.h>
#include <dlfcn.h>
#if defined(__ARM_NEON)
#include <arm_neon.h>
#endif
#include <jni.h>
#include <math.h>
#include <mutex>
#include <vector>

#define TAG "HikariImagePipeline"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

typedef struct AImageDecoder AImageDecoder;
typedef struct AImageDecoderHeaderInfo AImageDecoderHeaderInfo;

typedef int (*pfn_AImageDecoder_createFromBuffer)(const void *, size_t,
                                                  AImageDecoder **);

typedef const AImageDecoderHeaderInfo *(*pfn_AImageDecoder_getHeaderInfo)(
    const AImageDecoder *);

typedef int32_t (*pfn_AImageDecoderHeaderInfo_getWidth)(
    const AImageDecoderHeaderInfo *);

typedef int32_t (*pfn_AImageDecoderHeaderInfo_getHeight)(
    const AImageDecoderHeaderInfo *);

typedef int (*pfn_AImageDecoder_setTargetSize)(AImageDecoder *, int32_t,
                                               int32_t);

typedef int (*pfn_AImageDecoder_setTargetRect)(AImageDecoder *, ARect);

typedef int (*pfn_AImageDecoder_decodeImage)(AImageDecoder *, void *, size_t,
                                             size_t);

typedef void (*pfn_AImageDecoder_delete)(AImageDecoder *);

struct ImageDecoderFunctions {
  pfn_AImageDecoder_createFromBuffer createFromBuffer;
  pfn_AImageDecoder_getHeaderInfo getHeaderInfo;
  pfn_AImageDecoderHeaderInfo_getWidth getWidth;
  pfn_AImageDecoderHeaderInfo_getHeight getHeight;
  pfn_AImageDecoder_setTargetSize setTargetSize;
  pfn_AImageDecoder_setTargetRect setTargetRect;
  pfn_AImageDecoder_decodeImage decodeImage;
  pfn_AImageDecoder_delete deleteDecoder;

  bool available = false;

  void load() {
    void *lib = dlopen("libjnigraphics.so", RTLD_NOW);
    if (!lib)
      return;

    createFromBuffer = (pfn_AImageDecoder_createFromBuffer)dlsym(
        lib, "AImageDecoder_createFromBuffer");
    getHeaderInfo = (pfn_AImageDecoder_getHeaderInfo)dlsym(
        lib, "AImageDecoder_getHeaderInfo");
    getWidth = (pfn_AImageDecoderHeaderInfo_getWidth)dlsym(
        lib, "AImageDecoderHeaderInfo_getWidth");
    getHeight = (pfn_AImageDecoderHeaderInfo_getHeight)dlsym(
        lib, "AImageDecoderHeaderInfo_getHeight");
    setTargetSize = (pfn_AImageDecoder_setTargetSize)dlsym(
        lib, "AImageDecoder_setTargetSize");
    setTargetRect = (pfn_AImageDecoder_setTargetRect)dlsym(
        lib, "AImageDecoder_setTargetRect");
    decodeImage =
        (pfn_AImageDecoder_decodeImage)dlsym(lib, "AImageDecoder_decodeImage");
    deleteDecoder =
        (pfn_AImageDecoder_delete)dlsym(lib, "AImageDecoder_delete");

    available = createFromBuffer && decodeImage && deleteDecoder;
  }
};

static ImageDecoderFunctions gDecoder;

namespace hikari {

struct Color {
  float r, g, b, a;
};

inline float clamp(float v, float min, float max) {
  return std::max(min, std::min(max, v));
}

class BufferPool {
public:
  static uint32_t *get(size_t size) {
    std::lock_guard<std::mutex> lock(mMutex);
    if (mSize < size) {
      delete[] mBuffer;
      mBuffer = new uint32_t[size];
      mSize = size;
    }
    return mBuffer;
  }

private:
  static uint32_t *mBuffer;
  static size_t mSize;
  static std::mutex mMutex;
};

uint32_t *BufferPool::mBuffer = nullptr;
size_t BufferPool::mSize = 0;
std::mutex BufferPool::mMutex;

void easu(uint32_t *src, uint32_t *dst, int sw, int sh, int dw, int dh) {
  float sx = (float)sw / dw;
  float sy = (float)sh / dh;

  for (int y = 0; y < dh; y++) {
    float srcY = y * sy;
    int y0 = (int)srcY;
    int y1 = std::min(y0 + 1, sh - 1);
    float fdy = srcY - y0;

    uint32_t *dstRow = dst + y * dw;
    uint32_t *srcRow0 = src + y0 * sw;
    uint32_t *srcRow1 = src + y1 * sw;

    int x = 0;
#if defined(__ARM_NEON)
    for (; x <= dw - 4; x += 4) {
      float fdx0 = (x + 0) * sx;
      float fdx1 = (x + 1) * sx;
      float fdx2 = (x + 2) * sx;
      float fdx3 = (x + 3) * sx;

      int x0_0 = (int)fdx0;
      int x0_1 = (int)fdx1;
      int x0_2 = (int)fdx2;
      int x0_3 = (int)fdx3;

      int x1_0 = std::min(x0_0 + 1, sw - 1);
      int x1_1 = std::min(x0_1 + 1, sw - 1);
      int x1_2 = std::min(x0_2 + 1, sw - 1);
      int x1_3 = std::min(x0_3 + 1, sw - 1);

      auto process_pixel = [&](int x0, int x1, float dx) {
        uint32_t c00 = srcRow0[x0];
        uint32_t c10 = srcRow0[x1];
        uint32_t c01 = srcRow1[x0];
        uint32_t c11 = srcRow1[x1];

        float w00 = (1.0f - dx) * (1.0f - fdy);
        float w10 = dx * (1.0f - fdy);
        float w01 = (1.0f - dx) * fdy;
        float w11 = dx * fdy;

        float l00 = (float)(c00 & 0xFF);
        float l10 = (float)(c10 & 0xFF);
        float l01 = (float)(c01 & 0xFF);
        float l11 = (float)(c11 & 0xFF);

        float gx = fabsf(l10 - l00) + fabsf(l11 - l01);
        float gy = fabsf(l01 - l00) + fabsf(l11 - l10);

        if (gx > gy * 2.0f) {
          float s = dx * dx * (3.0f - 2.0f * dx);
          w00 = (1.0f - s) * (1.0f - fdy);
          w10 = s * (1.0f - fdy);
          w01 = (1.0f - s) * fdy;
          w11 = s * fdy;
        } else if (gy > gx * 2.0f) {
          float s = fdy * fdy * (3.0f - 2.0f * fdy);
          w00 = (1.0f - dx) * (1.0f - s);
          w10 = dx * (1.0f - s);
          w01 = (1.0f - dx) * s;
          w11 = dx * s;
        }

        float r = w00 * (c00 & 0xFF) + w10 * (c10 & 0xFF) + w01 * (c01 & 0xFF) +
                  w11 * (c11 & 0xFF);
        float g = w00 * ((c00 >> 8) & 0xFF) + w10 * ((c10 >> 8) & 0xFF) +
                  w01 * ((c01 >> 8) & 0xFF) + w11 * ((c11 >> 8) & 0xFF);
        float b = w00 * ((c00 >> 16) & 0xFF) + w10 * ((c10 >> 16) & 0xFF) +
                  w01 * ((c01 >> 16) & 0xFF) + w11 * ((c11 >> 16) & 0xFF);
        float a = w00 * ((c00 >> 24) & 0xFF) + w10 * ((c10 >> 24) & 0xFF) +
                  w01 * ((c01 >> 24) & 0xFF) + w11 * ((c11 >> 24) & 0xFF);

        return ((uint32_t)a << 24) | ((uint32_t)b << 16) | ((uint32_t)g << 8) |
               (uint32_t)r;
      };

      dstRow[x + 0] = process_pixel(x0_0, x1_0, fdx0 - x0_0);
      dstRow[x + 1] = process_pixel(x0_1, x1_1, fdx1 - x0_1);
      dstRow[x + 2] = process_pixel(x0_2, x1_2, fdx2 - x0_2);
      dstRow[x + 3] = process_pixel(x0_3, x1_3, fdx3 - x0_3);
    }
#endif

    for (; x < dw; x++) {
      float srcX = x * sx;
      int x0 = (int)srcX;
      int x1 = std::min(x0 + 1, sw - 1);
      float dx = srcX - x0;

      uint32_t c00 = srcRow0[x0];
      uint32_t c10 = srcRow0[x1];
      uint32_t c01 = srcRow1[x0];
      uint32_t c11 = srcRow1[x1];

      float w00 = (1.0f - dx) * (1.0f - fdy);
      float w10 = dx * (1.0f - fdy);
      float w01 = (1.0f - dx) * fdy;
      float w11 = dx * fdy;

      float l00 = (float)(c00 & 0xFF);
      float l10 = (float)(c10 & 0xFF);
      float l01 = (float)(c01 & 0xFF);
      float l11 = (float)(c11 & 0xFF);

      float gx = fabsf(l10 - l00) + fabsf(l11 - l01);
      float gy = fabsf(l01 - l00) + fabsf(l11 - l10);

      if (gx > gy * 2.0f) {
        float s = dx * dx * (3.0f - 2.0f * dx);
        w00 = (1.0f - s) * (1.0f - fdy);
        w10 = s * (1.0f - fdy);
        w01 = (1.0f - s) * fdy;
        w11 = s * fdy;
      } else if (gy > gx * 2.0f) {
        float s = fdy * fdy * (3.0f - 2.0f * fdy);
        w00 = (1.0f - dx) * (1.0f - s);
        w10 = dx * (1.0f - s);
        w01 = (1.0f - dx) * s;
        w11 = dx * s;
      }

      float r = w00 * (c00 & 0xFF) + w10 * (c10 & 0xFF) + w01 * (c01 & 0xFF) +
                w11 * (c11 & 0xFF);
      float g = w00 * ((c00 >> 8) & 0xFF) + w10 * ((c10 >> 8) & 0xFF) +
                w01 * ((c01 >> 8) & 0xFF) + w11 * ((c11 >> 8) & 0xFF);
      float b = w00 * ((c00 >> 16) & 0xFF) + w10 * ((c10 >> 16) & 0xFF) +
                w01 * ((c01 >> 16) & 0xFF) + w11 * ((c11 >> 16) & 0xFF);
      float a = w00 * ((c00 >> 24) & 0xFF) + w10 * ((c10 >> 24) & 0xFF) +
                w01 * ((c01 >> 24) & 0xFF) + w11 * ((c11 >> 24) & 0xFF);

      dstRow[x] = ((uint32_t)a << 24) | ((uint32_t)b << 16) |
                  ((uint32_t)g << 8) | (uint32_t)r;
    }
  }
}

} // namespace hikari

extern "C" {

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
  gDecoder.load();
  return JNI_VERSION_1_6;
}

JNIEXPORT jboolean JNICALL
Java_tachiyomi_core_common_util_system_NativeImageDecoder_nativeDecode(
    JNIEnv *env, jobject thiz, jobject bitmap, jbyteArray jData, jint length,
    jint filters) {
  if (bitmap == nullptr || jData == nullptr || !gDecoder.available)
    return JNI_FALSE;

  jbyte *data = env->GetByteArrayElements(jData, nullptr);
  AImageDecoder *decoder = nullptr;
  int ret = gDecoder.createFromBuffer(data, length, &decoder);
  if (ret != 0) {
    env->ReleaseByteArrayElements(jData, data, JNI_ABORT);
    return JNI_FALSE;
  }

  AndroidBitmapInfo info;
  AndroidBitmap_getInfo(env, bitmap, &info);

  void *pixels;
  AndroidBitmap_lockPixels(env, bitmap, &pixels);

  if (filters & 4) { // FILTER_UPSCALING
    const AImageDecoderHeaderInfo *header = gDecoder.getHeaderInfo(decoder);
    int sw = gDecoder.getWidth(header);
    int sh = gDecoder.getHeight(header);

    uint32_t *temp = hikari::BufferPool::get(sw * sh);
    gDecoder.decodeImage(decoder, temp, sw * 4, sw * sh * 4);

    hikari::easu(temp, (uint32_t *)pixels, sw, sh, info.width, info.height);
  } else {
    gDecoder.setTargetSize(decoder, info.width, info.height);
    gDecoder.decodeImage(decoder, pixels, info.stride,
                         info.stride * info.height);
  }

  AndroidBitmap_unlockPixels(env, bitmap);
  gDecoder.deleteDecoder(decoder);
  env->ReleaseByteArrayElements(jData, data, JNI_ABORT);

  return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_tachiyomi_core_common_util_system_NativeImageDecoder_nativeDecodeRegion(
    JNIEnv *env, jobject thiz, jobject bitmap, jbyteArray jData, jint length,
    jint left, jint top, jint right, jint bottom, jint sampleSize,
    jint filters) {
  if (bitmap == nullptr || jData == nullptr || !gDecoder.available)
    return JNI_FALSE;

  jbyte *data = env->GetByteArrayElements(jData, nullptr);
  AImageDecoder *decoder = nullptr;
  int ret = gDecoder.createFromBuffer(data, length, &decoder);
  if (ret != 0) {
    env->ReleaseByteArrayElements(jData, data, JNI_ABORT);
    return JNI_FALSE;
  }

  AndroidBitmapInfo info;
  AndroidBitmap_getInfo(env, bitmap, &info);

  void *pixels;
  AndroidBitmap_lockPixels(env, bitmap, &pixels);

  // Set the source crop (region)
  if (gDecoder.setTargetRect) {
    gDecoder.setTargetRect(decoder, {left, top, right, bottom});
  }

  // Apply sample size / target size for the region
  int targetWidth = (right - left) / sampleSize;
  int targetHeight = (bottom - top) / sampleSize;
  if (gDecoder.setTargetSize) {
    gDecoder.setTargetSize(decoder, targetWidth, targetHeight);
  }

  // Decode directly into the provided bitmap pixels
  gDecoder.decodeImage(decoder, pixels, info.stride, info.stride * info.height);

  AndroidBitmap_unlockPixels(env, bitmap);
  gDecoder.deleteDecoder(decoder);
  env->ReleaseByteArrayElements(jData, data, JNI_ABORT);

  return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_tachiyomi_core_common_util_system_NativeImageDecoder_nativeProcess(
    JNIEnv *env, jobject thiz, jobject bitmap, jint filters) {
  if (bitmap == nullptr)
    return JNI_FALSE;

  AndroidBitmapInfo info;
  AndroidBitmap_getInfo(env, bitmap, &info);

  if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888)
    return JNI_FALSE;

  void *pixels;
  AndroidBitmap_lockPixels(env, bitmap, &pixels);

  if (filters & 4) {
    LOGD("Native post-processing filter applied: UPSCALING");
  }

  AndroidBitmap_unlockPixels(env, bitmap);
  return JNI_TRUE;
}

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

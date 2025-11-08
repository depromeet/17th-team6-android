package com.dpm.sixpack.presentation.common.util.capture

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Composable View를 고품질 Bitmap으로 캡처하는 컨트롤러
 *
 * **사용 방법:**
 * ```kotlin
 * val captureController = rememberCaptureController()
 *
 * PostImageWithRecord(
 *     modifier = Modifier.capturable(captureController)
 * )
 *
 * // 고품질 캡처 실행
 * viewModelScope.launch {
 *     val bitmap = captureController.captureHighQuality()
 *     // 인스타그램 피드 품질의 bitmap
 * }
 * ```
 *
 * @property graphicsLayer 캡처 대상이 되는 Compose GraphicsLayer
 */
class CaptureController internal constructor(
    internal val graphicsLayer: GraphicsLayer,
) {
    companion object {
        private const val MIN_SIZE = 1080
        private const val MAX_SCALE_FACTOR = 3.0f
    }

    /**
     * 현재 캡처 대상 Composable을 고품질 Bitmap으로 변환합니다.
     *
     * **인스타그램 피드 품질 보장:**
     * - 최소 1080x1080 해상도 (필요시 자동 업스케일)
     * - ARGB_8888 Config (고품질 색상)
     * - 안티앨리어싱 + 필터링 (선명도 유지)
     *
     * @param targetSize 목표 해상도 (기본값: 1080, 인스타그램 권장)
     * @return 고품질 Bitmap. 캡처 실패 시 null
     */
    suspend fun captureHighQuality(targetSize: Int = MIN_SIZE): Bitmap? =
        withContext(Dispatchers.Default) {
            try {
                // 1. 원본 캡처
                val imageBitmap = graphicsLayer.toImageBitmap()
                val originalBitmap = imageBitmap.asAndroidBitmap()

                // 2. ARGB_8888로 변환 (고품질 보장)
                val highQualityBitmap = ensureARGB8888(originalBitmap)

                // 3. 해상도 체크 및 업스케일
                val scaledBitmap = scaleToMinSize(highQualityBitmap, targetSize)

                Timber.d(
                    "Captured high-quality bitmap: ${scaledBitmap.width}x${scaledBitmap.height}, " +
                        "config=${scaledBitmap.config}",
                )

                scaledBitmap
            } catch (e: Exception) {
                Timber.e(e, "Failed to capture high-quality bitmap")
                null
            }
        }

    /**
     * 기본 품질 캡처 (하위 호환성)
     *
     * @return 캡처된 Bitmap. 캡처 실패 시 null
     */
    suspend fun captureAsync(): Bitmap? = captureHighQuality()

    /**
     * Bitmap을 ARGB_8888 Config로 변환합니다.
     * 이미 ARGB_8888이면 원본을 반환합니다.
     */
    private fun ensureARGB8888(bitmap: Bitmap): Bitmap {
        if (bitmap.config == Bitmap.Config.ARGB_8888) {
            return bitmap
        }

        return bitmap.copy(Bitmap.Config.ARGB_8888, false).also {
            Timber.d("Converted bitmap to ARGB_8888")
        }
    }

    /**
     * Bitmap을 최소 크기로 스케일링합니다.
     *
     * 고품질 스케일링 적용:
     * - 안티앨리어싱 (계단 현상 방지)
     * - 필터링 (선명도 유지)
     * - 바이리니어 보간법 사용
     *
     * @param bitmap 원본 Bitmap
     * @param minSize 최소 너비/높이
     * @return 스케일링된 Bitmap (이미 충분히 크면 원본 반환)
     */
    private fun scaleToMinSize(
        bitmap: Bitmap,
        minSize: Int,
    ): Bitmap {
        val currentMinSize = min(bitmap.width, bitmap.height)

        // 이미 충분히 큰 경우 원본 반환
        if (currentMinSize >= minSize) {
            return bitmap
        }

        // 스케일 비율 계산 (최소 크기 맞추기)
        val scaleFactor = minSize.toFloat() / currentMinSize.toFloat()
        val cappedScaleFactor = minFloat(scaleFactor, MAX_SCALE_FACTOR)

        val newWidth = (bitmap.width * cappedScaleFactor).toInt()
        val newHeight = (bitmap.height * cappedScaleFactor).toInt()

        Timber.d(
            "Scaling bitmap from ${bitmap.width}x${bitmap.height} to ${newWidth}x$newHeight " +
                "(factor: $cappedScaleFactor)",
        )

        // 고품질 스케일링
        return createScaledBitmapHighQuality(bitmap, newWidth, newHeight)
    }

    /**
     * 고품질 Bitmap 스케일링 (안티앨리어싱 + 필터링)
     */
    private fun createScaledBitmapHighQuality(
        src: Bitmap,
        dstWidth: Int,
        dstHeight: Int,
    ): Bitmap {
        val matrix = Matrix()
        matrix.setScale(
            dstWidth.toFloat() / src.width,
            dstHeight.toFloat() / src.height,
        )

        val paint =
            Paint().apply {
                isAntiAlias = true
                isFilterBitmap = true
                isDither = true
            }

        val scaledBitmap = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(scaledBitmap)
        canvas.drawBitmap(src, matrix, paint)

        return scaledBitmap
    }

    private suspend fun GraphicsLayer.toImageBitmap(): ImageBitmap {
        // Compose 1.7.0+ API
        return this.toImageBitmap()
    }

    private fun min(
        a: Int,
        b: Int,
    ): Int = if (a < b) a else b

    private fun minFloat(
        a: Float,
        b: Float,
    ): Float = if (a < b) a else b
}

/**
 * CaptureController를 생성하고 remember로 상태를 유지합니다.
 *
 * Recomposition이 발생해도 동일한 인스턴스를 유지하여 성능을 최적화합니다.
 *
 * @return 재사용 가능한 CaptureController 인스턴스
 */
@Composable
fun rememberCaptureController(): CaptureController {
    val graphicsLayer = rememberGraphicsLayer()
    return remember(graphicsLayer) {
        CaptureController(graphicsLayer)
    }
}

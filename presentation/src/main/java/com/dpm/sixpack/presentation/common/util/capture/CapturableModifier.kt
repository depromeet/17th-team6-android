package com.dpm.sixpack.presentation.common.util.capture

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.drawLayer

/**
 * Composable을 캡처 가능하게 만드는 Modifier extension
 * **사용 예시:**
 * ```kotlin
 * val captureController = rememberCaptureController()
 *
 * PostImageWithRecord(
 *     postImageUrl = imageUrl,
 *     runningSummary = runningSummary,
 *     modifier = Modifier
 *         .fillMaxWidth()
 *         .capturable(captureController) // 캡처 가능하게 설정
 * )
 *
 * // 캡처 실행 (suspend 함수)
 * viewModelScope.launch {
 *     captureController.captureAsync()?.let { bitmap ->
 *         // bitmap 처리
 *     }
 * }
 * ```
 *
 * @param controller 캡처를 제어하는 CaptureController 인스턴스
 * @return 캡처 가능하게 설정된 Modifier
 */
fun Modifier.capturable(controller: CaptureController): Modifier =
    this.drawWithContent {
        controller.graphicsLayer.record {
            this@drawWithContent.drawContent()
        }
        // 기록된 레이어를 그리기
        drawLayer(controller.graphicsLayer)
    }

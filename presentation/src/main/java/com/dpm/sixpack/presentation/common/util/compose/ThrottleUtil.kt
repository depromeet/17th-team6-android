package com.dpm.sixpack.presentation.common.util.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * 클릭 이벤트에 쓰로틀을 적용하는 Composable 함수
 *
 * @param throttleTimeMillis 쓰로틀 시간 (밀리초). 이 시간 동안은 추가 클릭이 무시됩니다.
 * @param onClick 실제 클릭 이벤트 핸들러
 * @return Pair<쓰로틀이 적용된 클릭 핸들러, 활성화 여부>
 */
@Composable
fun rememberThrottledClick(
    throttleTimeMillis: Long = 1000L,
    onClick: () -> Unit,
): Pair<() -> Unit, Boolean> {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    var isEnabled by remember { mutableStateOf(true) }

    val throttledClick =
        remember(onClick, throttleTimeMillis) {
            {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime >= throttleTimeMillis) {
                    lastClickTime = currentTime
                    isEnabled = false
                    onClick()
                }
            }
        }

    LaunchedEffect(lastClickTime) {
        if (lastClickTime > 0L) {
            delay(throttleTimeMillis)
            isEnabled = true
        }
    }

    return throttledClick to isEnabled
}

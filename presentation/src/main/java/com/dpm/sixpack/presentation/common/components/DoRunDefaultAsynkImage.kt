package com.dpm.sixpack.presentation.common.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.size.Precision
import coil3.size.Size
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun DoRunDefaultAsyncImage(
    model: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Painter = ColorPainter(SixpackTheme.colors.gray200),
    error: Painter = ColorPainter(SixpackTheme.colors.gray500),
) {
    val data = model.ifEmpty { null }
    val context = LocalContext.current

    val imageRequest =
        remember(data) {
            ImageRequest
                .Builder(context)
                .data(data)
                .allowHardware(false) // 맵 마커를 위한 필수 옵션 (유지)
                .crossfade(true)
                // ⬇️ 4. (크기 문제 해결) "정확한 크기" 대신 "부정확한 크기" 허용
                // "캐시된 이미지가 타겟 크기보다 작아도 그냥 사용해"라는 뜻
                .precision(Precision.INEXACT)
                // ⬇️ 5. (캐시 효율성) 캐시에는 원본 크기로 저장
                // (다운스케일된 126px이 아닌, 원본 이미지를 캐싱하여 재사용성 높임)
                .size(Size.ORIGINAL)
                .build()
        }

    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = modifier,
        placeholder = placeholder,
        error = error,
        contentScale = contentScale,
    )
}

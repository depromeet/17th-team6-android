package com.dpm.sixpack.presentation.common.components.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 공통 비동기 이미지 로딩 컴포넌트
 *
 * Coil을 사용하여 최적화된 이미지 로딩을 제공합니다.
 *
 * @param imageUrl 로딩할 이미지 URL
 * @param contentDescription 이미지 설명 (접근성)
 * @param modifier Modifier
 * @param contentScale 이미지 스케일 방식
 * @param alpha 투명도
 * @param colorFilter 색상 필터
 * @param enableCrossfade 크로스페이드 애니메이션 활성화
 */
@Composable
fun DoRunAsyncImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    enableCrossfade: Boolean = true,
) {
    AsyncImage(
        model =
            ImageRequest
                .Builder(LocalContext.current)
                .data(imageUrl)
                .apply {
                    if (enableCrossfade) {
                        crossfade(true)
                    }
                }.build(),
        contentDescription = contentDescription,
        modifier = modifier,
        placeholder = ColorPainter(SixpackTheme.colors.gray100),
        error = ColorPainter(SixpackTheme.colors.gray200),
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
    )
}

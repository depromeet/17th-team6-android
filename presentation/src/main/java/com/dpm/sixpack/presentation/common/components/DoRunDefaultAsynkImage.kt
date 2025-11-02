package com.dpm.sixpack.presentation.common.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun DoRunDefaultAsyncImage(
    model: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Painter = ColorPainter(SixpackTheme.colors.gray200),
    error: Painter = ColorPainter(SixpackTheme.colors.gray500)
) {
    val data = model.ifEmpty { null }

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(data)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        placeholder = placeholder,
        error = error,
        contentScale = contentScale
    )
}

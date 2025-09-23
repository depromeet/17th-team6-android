package com.dpm.sixpack.presentation.routes.session.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningScreenTabItem
import com.dpm.sixpack.presentation.theme.SixpackTheme

private fun ContentDrawScope.drawWithLayer(block: ContentDrawScope.() -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        block()
        restoreToCount(checkPoint)
    }
}

@Composable
internal fun ScreenSelectionTab(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    tabItems: List<RunningScreenTabItem>,
    onSelectionChange: (RunningScreenTabItem) -> Unit,
) {
    val blackColor = SixpackTheme.colors.gray900
    val whiteColor = SixpackTheme.colors.gray0

    BoxWithConstraints(
        modifier
            .padding(8.dp)
            .height(44.dp)
            .clip(SixpackTheme.shapes.round8)
            .background(SixpackTheme.colors.gray50)
            .padding(8.dp),
    ) {
        if (tabItems.isNotEmpty()) {
            val maxWidth = this.maxWidth
            val tabWidth = maxWidth / tabItems.size
            val indicatorOffset by animateDpAsState(
                targetValue = tabWidth * selectedIndex,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
                label = "indicator offset",
            )

            Box(
                modifier =
                    Modifier
                        .offset(x = indicatorOffset)
                        .shadow(4.dp, SixpackTheme.shapes.round8)
                        .width(tabWidth)
                        .fillMaxHeight(),
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .drawWithContent {
                            // This is for setting black tex while drawing on white background
                            drawRoundRect(
                                topLeft = Offset(x = indicatorOffset.toPx(), 0.dp.toPx()),
                                size = Size(size.width / 2, size.height),
                                color = blackColor,
                                cornerRadius = CornerRadius(x = 8.dp.toPx(), y = 8.dp.toPx()),
                            )

                            drawWithLayer {
                                drawContent()

                                // This is white top rounded rectangle
                                drawRoundRect(
                                    topLeft = Offset(x = indicatorOffset.toPx(), 0f),
                                    size = Size(size.width / 2, size.height),
                                    color = whiteColor,
                                    cornerRadius = CornerRadius(x = 8.dp.toPx(), y = 8.dp.toPx()),
                                    blendMode = BlendMode.SrcOut,
                                )
                            }
                        },
            ) {
                tabItems.forEachIndexed { index, tab ->
                    Box(
                        modifier =
                            Modifier
                                .width(tabWidth)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource =
                                        remember {
                                            MutableInteractionSource()
                                        },
                                    indication = null,
                                    onClick = {
                                        onSelectionChange(tabItems[index])
                                    },
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(tab.title),
                            style = SixpackTheme.typography.b1Bold,
                            color = SixpackTheme.colors.gray500,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ScreenSelectionTabPreview() {
    ScreenSelectionTab(
        selectedIndex = 1,
        tabItems = RunningScreenTabItem.entries,
        onSelectionChange = { },
    )
}

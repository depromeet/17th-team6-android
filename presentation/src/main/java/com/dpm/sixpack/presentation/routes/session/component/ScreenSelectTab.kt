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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningScreenTabItem
import com.dpm.sixpack.presentation.theme.SixpackTheme

// 오직 컴포저블로만 구현한 스크린 전환 탭
@Composable
internal fun ScreenSelectTab(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    tabItems: List<RunningScreenTabItem>,
    onSelectionChange: (RunningScreenTabItem) -> Unit,
) {
    Box(
        modifier =
            modifier
                .height(44.dp)
                .clip(SixpackTheme.shapes.round8)
                .background(SixpackTheme.colors.gray50)
                .padding(4.dp),
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
        ) {
            val tabWidth = maxWidth / tabItems.size

            val indicatorOffset by animateDpAsState(
                targetValue = tabWidth * selectedIndex,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
                label = "indicator offset",
            )

            // 흰색 인디케이터 배경
            Box(
                modifier =
                    Modifier
                        .offset(x = indicatorOffset)
                        .shadow(elevation = 4.dp, shape = SixpackTheme.shapes.round8)
                        .width(tabWidth)
                        .fillMaxHeight()
                        .background(
                            color = SixpackTheme.colors.gray0,
                            shape = SixpackTheme.shapes.round8,
                        ).zIndex(1f),
            )

            // 텍스트를 담는 Row
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .zIndex(2f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabItems.forEachIndexed { index, tab ->
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { onSelectionChange(tab) },
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(id = tab.title),
                            style = SixpackTheme.typography.b1Bold,
                            color =
                                if (selectedIndex == index) {
                                    SixpackTheme.colors.gray900
                                } else {
                                    SixpackTheme.colors.gray500
                                },
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewScreenTab() {
    ScreenSelectTab(
        selectedIndex = 1,
        tabItems = RunningScreenTabItem.entries,
        onSelectionChange = { },
    )
}

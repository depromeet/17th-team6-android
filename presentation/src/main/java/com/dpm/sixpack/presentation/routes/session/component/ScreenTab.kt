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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningScreenTabItem
import com.dpm.sixpack.presentation.theme.SixpackTheme

/* 오직 컴포저블로만 구현한 스크린 전환 탭
* 현재 탭 전환시 글자색 안바뀌는 문제 있어서 사용 보류
* */
@Composable
internal fun ScreenTab(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    tabItems: List<RunningScreenTabItem>,
    onSelectionChange: (RunningScreenTabItem) -> Unit,
) {
    BoxWithConstraints(
        modifier =
            modifier
                .padding(8.dp)
                .height(44.dp)
                .clip(SixpackTheme.shapes.round8)
                .background(SixpackTheme.colors.gray50)
                .padding(4.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (tabItems.isNotEmpty()) {
            val maxWidth = this.maxWidth
            val tabWidth = maxWidth / tabItems.size

            val indicatorOffset by animateDpAsState(
                targetValue = tabWidth * selectedIndex,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
                label = "indicator offset",
            )

            // 흰색 배경 인디케이터 + 그림자
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
                        ),
            )

            // 텍스트 레이어들을 담는 컨테이너
            // BoxWithConstraints의 정렬 기준에 따라 위치가 결정됨
            Box {
                // 기본 텍스트 (회색)
                TabTextLayer(
                    tabItems = tabItems,
                    tabWidth = tabWidth,
                    textColor = SixpackTheme.colors.gray500, // 미선택 색상
                    onTabClick = { index -> onSelectionChange(tabItems[index]) },
                )

                // 선택된 텍스트 (검은색) - 슬라이딩 윈도우
                Box(
                    modifier =
                        Modifier
                            .offset(x = indicatorOffset)
                            .width(tabWidth)
                            .fillMaxHeight()
                            .clipToBounds(),
                ) {
                    TabTextLayer(
                        modifier = Modifier.offset(x = -indicatorOffset),
                        tabItems = tabItems,
                        tabWidth = tabWidth,
                        textColor = SixpackTheme.colors.gray900, // 선택 색상
                        onTabClick = null,
                    )
                }
            }
        }
    }
}

@Composable
private fun TabTextLayer(
    tabItems: List<RunningScreenTabItem>,
    tabWidth: Dp,
    textColor: Color,
    modifier: Modifier = Modifier,
    onTabClick: ((Int) -> Unit)?,
) {
    Row(modifier = modifier.width(tabWidth * tabItems.size)) {
        tabItems.forEachIndexed { index, tab ->
            Box(
                modifier =
                    Modifier
                        .width(tabWidth)
                        .fillMaxHeight()
                        .then(
                            if (onTabClick != null) {
                                Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { onTabClick(index) },
                                )
                            } else {
                                Modifier
                            },
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(tab.title),
                    style = SixpackTheme.typography.b1Bold,
                    color = textColor,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewScreenTab() {
    ScreenTab(
        selectedIndex = 1,
        tabItems = RunningScreenTabItem.entries,
        onSelectionChange = { },
    )
}

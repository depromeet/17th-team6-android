package com.dpm.sixpack.presentation.routes.running.map.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.model.FriendUiItem
import com.dpm.sixpack.presentation.routes.freind.components.FriendsLazyColumn
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun DraggableFriendBottomSheet(
    draggableState: AnchoredDraggableState<SheetDragState>,
    friendList: List<FriendUiItem>,
    sheetHeight: Dp,
    startButtonHeight: Dp,
    modifier: Modifier = Modifier,
    onFriendIconClick: () -> Unit = {},
) {
    if (sheetHeight == 0.dp) return

    Surface(
        modifier =
            modifier
                .anchoredDraggable(draggableState, Orientation.Vertical)
                .height(sheetHeight),
        shape = SixpackTheme.shapes.round24,
        color = SixpackTheme.colors.gray0,
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = startButtonHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            // 드래그 핸들 (고정 크기)
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(24.dp),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Spacer(
                    modifier =
                        Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                color = SixpackTheme.colors.gray100,
                                shape = CircleShape,
                            ),
                )
            }

            // 타이틀 (유동 크기)
            FriendSheetTitle(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                onIconClick = onFriendIconClick,
            )

            // 친구 목록
            FriendsLazyColumn(
                modifier =
                    Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                        .weight(1f),
                friendList = friendList,
            )
        }
    }
}

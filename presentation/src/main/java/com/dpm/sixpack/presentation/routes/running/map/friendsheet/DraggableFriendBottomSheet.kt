package com.dpm.sixpack.presentation.routes.running.map.friendsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.dpm.sixpack.presentation.common.model.FriendItem
import com.dpm.sixpack.presentation.routes.running.map.component.SheetDragState
import com.dpm.sixpack.presentation.routes.running.map.contract.MapIntent
import com.dpm.sixpack.presentation.routes.running.map.contract.MapViewState
import com.dpm.sixpack.presentation.theme.SixpackTheme

private val HORIZONTAL_PADDING = 24.dp

@Composable
internal fun DraggableFriendBottomSheet(
    friendSheetState: MapViewState.Friend,
    draggableState: AnchoredDraggableState<SheetDragState>,
    sheetHeight: Dp,
    startButtonHeight: Dp,
    modifier: Modifier = Modifier,
    onIntent: (MapIntent.FriendSheetIntent) -> Unit,
    pagingItems: LazyPagingItems<FriendItem>,
) {
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
                    .padding(bottom = startButtonHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = HORIZONTAL_PADDING),
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

                // 타이틀
                FriendSheetTitle(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    onIconClick = { onIntent(MapIntent.FriendSheetIntent.FriendIconClick) },
                )
            }

            // 친구 목록
            FriendSheetLazyColumn(
                modifier =
                    Modifier.fillMaxWidth(),
                pagingItems = pagingItems,
                itemPadding = PaddingValues(horizontal = HORIZONTAL_PADDING, 12.dp),
                selected = friendSheetState.selectedFriend,
                onRefresh = {
                    onIntent(MapIntent.FriendSheetIntent.PullToRefresh)
                },
                onAwakeClick = { userId ->
                    onIntent(MapIntent.FriendSheetIntent.AwakeFriend(userId))
                },
                onItemClick = { userId ->
                    onIntent(MapIntent.FriendSheetIntent.ClickFriendItem(userId))
                },
            )
        }
    }
}

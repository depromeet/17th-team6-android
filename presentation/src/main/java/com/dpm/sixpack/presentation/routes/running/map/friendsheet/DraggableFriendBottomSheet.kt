package com.dpm.sixpack.presentation.routes.running.map.friendsheet

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.dpm.sixpack.presentation.routes.running.map.component.SheetDragState
import com.dpm.sixpack.presentation.routes.running.map.friendsheet.contract.FriendSheetIntent
import com.dpm.sixpack.presentation.routes.running.map.friendsheet.contract.FriendSheetSideEffect
import com.dpm.sixpack.presentation.theme.SixpackTheme
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun DraggableFriendBottomSheet(
    draggableState: AnchoredDraggableState<SheetDragState>,
    sheetHeight: Dp,
    startButtonHeight: Dp,
    modifier: Modifier = Modifier,
    onShowSnackBar: (String, String?) -> Unit = { _, _ -> },
    onFriendIconClick: () -> Unit = {},
    viewModel: FriendSheetViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val pagingItems = viewModel.friendPagingFlow.collectAsLazyPagingItems()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is FriendSheetSideEffect.UserItemClicked -> {
                // TODO: sideEffect.userId를 사용해 프로필 화면 등으로 이동
            }

            is FriendSheetSideEffect.ShowToast -> {
                val text = context.getString(sideEffect.stringResId, sideEffect.args)
                onShowSnackBar(text, null)
            }
        }
    }

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
            FriendSheetLazyColumn(
                modifier =
                    Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                        .weight(1f),
                pagingItems = pagingItems,
                onAwakeClick = { userId ->
                    viewModel.onIntent(FriendSheetIntent.AwakeFriend(userId))
                },
                onItemClick = { userId ->
                    viewModel.onIntent(FriendSheetIntent.ClickUser(userId))
                },
            )
        }
    }
}

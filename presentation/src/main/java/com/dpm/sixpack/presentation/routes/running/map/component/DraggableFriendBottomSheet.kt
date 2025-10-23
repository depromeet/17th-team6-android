package com.dpm.sixpack.presentation.routes.running.map.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.routes.freind.components.FriendsLazyColumn
import com.dpm.sixpack.presentation.routes.freind.contract.FriendItem
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun DraggableFriendBottomSheet(
    draggableState: AnchoredDraggableState<SheetDragState>,
    friendList: List<FriendItem>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.anchoredDraggable(draggableState, Orientation.Vertical),
        shape = MaterialTheme.shapes.large,
        color = SixpackTheme.colors.gray0,
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Spacer(
                    modifier =
                        Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .background(
                                color = SixpackTheme.colors.gray200,
                                shape = CircleShape,
                            ),
                )
            }
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FriendListTitle()
                // 친구 목록
                FriendsLazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(top = 12.dp),
                    friendList = friendList,
                )
            }
        }
    }
}

@Composable
private fun FriendListTitle(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "친구 두런 현황",
            color = SixpackTheme.colors.gray900,
            style = SixpackTheme.typography.t1Bold,
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.Group,
            contentDescription = "친구 아이콘",
            tint = SixpackTheme.colors.gray800,
        )
    }
}

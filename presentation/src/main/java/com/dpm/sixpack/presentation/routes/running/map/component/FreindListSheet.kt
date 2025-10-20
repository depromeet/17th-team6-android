package com.dpm.sixpack.presentation.routes.running.map.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.freind.components.FriendsLazyColumn
import com.dpm.sixpack.presentation.routes.freind.contract.FriendItem
import com.dpm.sixpack.presentation.routes.freind.contract.FriendUiState

@Composable
internal fun FriendStatusBottomSheet(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    friendUiState: FriendUiState,
    onCheerClick: (Long) -> Unit // userId를 받아 특정 친구를 응원
) {
    // 바텀시트의 상태를 관리 (열림, 닫힘 등)
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false, // 부분 확장 상태(화면 중앙)를 사용하도록 설정
        confirmValueChange = {
            // 전체 확장 상태(Expanded)로 변경되는 것을 막습니다.
            // 즉, 사용자가 화면 중앙 이상으로 시트를 올릴 수 없습니다.
            it != SheetValue.Expanded
        }
    )

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            // 바텀시트 상단의 드래그 핸들
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            // 바텀시트 내부 콘텐츠
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    // 하단 버튼이 시스템 네비게이션 바에 가려지지 않도록 패딩 추가
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. "친구 두런 현황" 타이틀 영역
                FriendListTitle()

                // 2. 친구 목록 (만들어두신 FriendsLazyColumn 재사용)
                // LazyColumn이 Column 내에서 스크롤되려면 weight를 줘야 합니다.
                FriendsLazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false) // 남은 공간을 모두 차지하되, 내용이 적으면 줄어듦
                        .padding(horizontal = 16.dp),
                    friendList = friendUiState.friendList,
                    onCheerClick = onCheerClick
                )
            }
        }
    }
}

@Composable
private fun FriendListTitle() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "친구 두런 현황",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.Group,
            contentDescription = "친구 아이콘",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Preview(showBackground = true)
@Composable
fun FriendStatusBottomSheetPreview() {
    DoRunPreviewWrapper {
        FriendStatusBottomSheet(
            showSheet = true,
            onDismiss = {},
            friendUiState = FriendUiState( // 테스트용 샘플 데이터
                listOf(
                    FriendItem(
                        userId = 12345,
                        nickName = "승규",
                        isMe = true,
                        profileImgUrl = "",
                        lastestRunAt = "2025-10-19T19:57:13Z",
                        distanceInMeter = 5000,
                        latitude = 37.5301,
                        longitude = 127.12345
                    ),

                    FriendItem(
                        userId = 12315,
                        nickName = "소래",
                        isMe = false,
                        profileImgUrl = "",
                        lastestRunAt = "2025-10-20T09:57:13Z",
                        distanceInMeter = 900,
                        latitude = 37.5301,
                        longitude = 127.12345
                    ),

                    FriendItem(
                        userId = 112415,
                        nickName = "소래",
                        isMe = false,
                        profileImgUrl = "",
                        lastestRunAt = "2025-10-16T19:57:13Z",
                        distanceInMeter = 3000,
                        latitude = 37.5301,
                        longitude = 127.12345
                    )
                )
            ),
            onCheerClick = {}
        )
    }
}

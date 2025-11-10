package com.dpm.sixpack.presentation.routes.friend.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.dialog.DialogButtonType
import com.dpm.sixpack.presentation.common.components.dialog.DoRunDefaultDialog
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.common.model.FriendItem
import com.dpm.sixpack.presentation.routes.friend.components.FriendBottomBar
import com.dpm.sixpack.presentation.routes.friend.components.FriendListLazyColumn
import com.dpm.sixpack.presentation.routes.friend.contract.FriendIntent
import com.dpm.sixpack.presentation.routes.friend.contract.FriendListIntent
import com.dpm.sixpack.presentation.routes.friend.contract.FriendUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FriendListScreen(
    state: FriendUiState.FriendList,
    pagingItems: LazyPagingItems<FriendItem>,
    onIntent: (FriendIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            DoRunNavigationTopBar(
                navigateToBack = { onIntent(FriendListIntent.NavigateBackClick) },
                titleContent = {
                    Text(
                        text = stringResource(R.string.friend_list_screen_title),
                        textAlign = TextAlign.Center,
                        style = SixpackTheme.typography.t1Bold,
                        color = SixpackTheme.colors.gray900,
                    )
                },
            )
        },
        bottomBar = {
            FriendBottomBar(
                onAddFriendClick = { onIntent(FriendListIntent.AddFriendClick) },
                onCopyCodeClick = { onIntent(FriendListIntent.MyCodeCopyClick) },
            )
        },
        containerColor = SixpackTheme.colors.gray0,
    ) { paddingValues ->

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.friend_list),
                    style = SixpackTheme.typography.t1Bold,
                    color = SixpackTheme.colors.gray900,
                )
                Text(
                    text = "${pagingItems.itemCount - 1}",
                    style = SixpackTheme.typography.t1Bold,
                    color = SixpackTheme.colors.gray500,
                )
            }

            Spacer(Modifier.height(12.dp))

            FriendListLazyColumn(
                modifier = Modifier.fillMaxSize(),
                showOptionForUserId = state.showOptionForUserId,
                pagingItems = pagingItems,
                onRefresh = { onIntent(FriendListIntent.Refresh) },
                onOptionClick = { userId -> onIntent(FriendListIntent.OptionClick(userId)) },
                onOptionDismiss = { onIntent(FriendListIntent.DismissOptionMenu) },
                onDeleteClick = { userId -> onIntent(FriendListIntent.ShowDeleteDialog(userId)) },
            )
        }
    }

    if (state.showDeleteDialogForUserId != null) {
        val deletedTarget = state.showDeleteDialogForUserId
        DoRunDefaultDialog(
            modifier = Modifier.fillMaxWidth(),
            title = "친구삭제",
            subtitle = "정말로 삭제하시겠어요?",
            onDismissRequest = {
                onIntent(FriendListIntent.DismissDeleteDialog)
            },
            onCancelClick = {
                onIntent(FriendListIntent.DismissDeleteDialog)
            },
            cancelButtonText = "취소",
            confirmButtonText = "삭제",
            onConfirmClick = {
                onIntent(FriendListIntent.ConfirmDeleteFriend(deletedTarget))
            },
            confirmButtonType = DialogButtonType.Destructive,
        )
    }
}

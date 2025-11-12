package com.dpm.sixpack.presentation.routes.friend

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.friend.contract.FriendIntent
import com.dpm.sixpack.presentation.routes.friend.contract.FriendSideEffect
import com.dpm.sixpack.presentation.routes.friend.contract.FriendUiState
import com.dpm.sixpack.presentation.routes.friend.screen.FriendListScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun FriendListRoute(
    navigateToBack: () -> Unit = {},
    navigateToAddFriend: () -> Unit = {},
    onShowSnackBar: (String, String?) -> Unit = { _, _ -> },
    viewModel: FriendViewModel = hiltViewModel(), // (공유 VM으로 주입 가정)
) {
    val context = LocalContext.current

    val state = viewModel.collectAsState()
    val friendState = state.value as? FriendUiState.FriendList

    // Paging Flow (리스트용)
    val pagingItems = viewModel.friendPagingFlow.collectAsLazyPagingItems()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            FriendSideEffect.NavigateToBack -> navigateToBack()
            FriendSideEffect.NavigateToAddFriend -> navigateToAddFriend()
            is FriendSideEffect.ShowToast -> {
                val message = context.getString(sideEffect.resId, sideEffect.args)
                onShowSnackBar(message, null)
            }

            is FriendSideEffect.CopyToClipboard -> {
                val clipboardManager =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                val clip =
                    ClipData.newPlainText(
                        "friend_code", // (클립보드 레이블)
                        sideEffect.content, // (VM이 보내준 실제 복사할 코드)
                    )

                clipboardManager.setPrimaryClip(clip)

                val message = context.getString(R.string.clipboard_copy)
                onShowSnackBar(message, null)
            }

            else -> {
                // doNothing
            }
        }
    }

    val onIntent: (FriendIntent) -> Unit = viewModel::onIntent

    friendState?.let {
        FriendListScreen(
            state = it,
            pagingItems = pagingItems,
            onIntent = onIntent,
        )
    }
}

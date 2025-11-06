package com.dpm.sixpack.presentation.routes.friend

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.friend.contract.AddFriendIntent
import com.dpm.sixpack.presentation.routes.friend.contract.FriendIntent
import com.dpm.sixpack.presentation.routes.friend.contract.FriendSideEffect
import com.dpm.sixpack.presentation.routes.friend.contract.FriendUiState
import com.dpm.sixpack.presentation.routes.friend.screen.AddFriendScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun AddFriendRoute(
    navigateToBack: () -> Unit,
    onShowSnackBar: (String, String?) -> Unit = { _, _ -> },
    viewModel: FriendViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state = viewModel.collectAsState()
    val addState = state.value as? FriendUiState.AddingFriend

    BackHandler {
        viewModel.onIntent(AddFriendIntent.NavigateToFriendList)
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            FriendSideEffect.NavigateToFriendList -> navigateToBack()
            is FriendSideEffect.ShowToast -> {
                val message = context.getString(sideEffect.resId, sideEffect.args)
                onShowSnackBar(message, null)
            }

            else -> {
                // do nothing
            }
        }
    }

    val onIntent: (FriendIntent) -> Unit = viewModel::onIntent

    addState?.let {
        AddFriendScreen(
            state = it,
            onIntent = onIntent,
        )
    }
}

package com.dpm.sixpack.presentation.routes.friendprofile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.dpm.sixpack.presentation.routes.friendprofile.contract.FriendProfileSideEffect
import com.dpm.sixpack.presentation.routes.friendprofile.ui.screen.FriendProfileScreen
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPagePostTabSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun FriendProfileRoute(
    modifier: Modifier = Modifier,
    viewModel: FriendProfileViewModel = hiltViewModel(),
    postTabViewModel: FriendProfilePostTabViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToPostDetail: (Long) -> Unit = {},
) {
    val state by viewModel.collectAsState()

    val gridItemsPagingItems = postTabViewModel.postsPagingFlow.collectAsLazyPagingItems()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is FriendProfileSideEffect.NavigateBack -> onNavigateBack()
            is FriendProfileSideEffect.NavigateToPostDetail -> onNavigateToPostDetail(sideEffect.postId)
        }
    }

    postTabViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MyPagePostTabSideEffect.NavigateToPostDetail -> onNavigateToPostDetail(sideEffect.postId)
        }
    }

    FriendProfileScreen(
        state = state,
        gridItemsPagingItems = gridItemsPagingItems,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

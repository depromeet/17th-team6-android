package com.dpm.sixpack.presentation.routes.mypage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPagePostTabSideEffect
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageRecordTabSideEffect
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageSideEffect
import com.dpm.sixpack.presentation.routes.mypage.ui.screen.MyPageScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MyPageRoute(
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel(),
    postTabViewModel: MyPagePostTabViewModel = hiltViewModel(),
    recordTabViewModel: MyPageRecordTabViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit = {},
    onNavigateToPostDetail: (Long) -> Unit = {},
    onNavigateToRecordDetail: (Long) -> Unit = {},
) {
    val state by viewModel.collectAsState()
    val recordTabState by recordTabViewModel.collectAsState()

    val gridItemsPagingItems = postTabViewModel.postsPagingFlow.collectAsLazyPagingItems()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MyPageSideEffect.NavigateToSettings -> onNavigateToSettings()
        }
    }

    postTabViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MyPagePostTabSideEffect.NavigateToPostDetail -> onNavigateToPostDetail(sideEffect.postId)
        }
    }

    recordTabViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MyPageRecordTabSideEffect.NavigateToRecordDetail -> onNavigateToRecordDetail(sideEffect.recordId)
        }
    }

    MyPageScreen(
        state = state,
        recordTabState = recordTabState,
        gridItemsPagingItems = gridItemsPagingItems,
        onIntent = viewModel::onIntent,
        onPostTabIntent = postTabViewModel::onIntent,
        onRecordTabIntent = recordTabViewModel::onIntent,
        modifier = modifier,
    )
}

package com.dpm.sixpack.presentation.routes.mypage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageSideEffect
import com.dpm.sixpack.presentation.routes.mypage.ui.screen.MyPageScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MyPageRoute(
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit = {},
    onNavigateToRecordDetail: (Long) -> Unit = {},
) {
    val state by viewModel.collectAsState()
    val gridItemsPagingItems = viewModel.postsPagingFlow.collectAsLazyPagingItems()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MyPageSideEffect.NavigateToSettings -> onNavigateToSettings()
            is MyPageSideEffect.NavigateToRecordDetail -> onNavigateToRecordDetail(sideEffect.recordId)
        }
    }

    MyPageScreen(
        state = state,
        gridItemsPagingItems = gridItemsPagingItems,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

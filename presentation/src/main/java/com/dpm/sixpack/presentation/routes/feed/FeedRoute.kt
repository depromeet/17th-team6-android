package com.dpm.sixpack.presentation.routes.feed

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.dpm.sixpack.presentation.common.util.capture.ImageSaver
import com.dpm.sixpack.presentation.routes.feed.contract.FeedSideEffect
import com.dpm.sixpack.presentation.routes.feed.ui.FeedScreen
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun FeedRoute(
    viewModel: FeedViewModel = hiltViewModel(),
    navigateToGroup: () -> Unit,
    navigateToAlarm: () -> Unit,
    navigateToCertifiedUserList: (String) -> Unit,
    navigateToUserProfile: (Long) -> Unit,
    navigateToMyPage: () -> Unit,
    navigateToPostDetail: (Long) -> Unit,
    navigateToCertifiableRecord: () -> Unit,
    navigateToPostEdit: (Long) -> Unit,
) {
    val state by viewModel.collectAsState()
    val feedPagingItems = viewModel.feedPagingData.collectAsLazyPagingItems()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is FeedSideEffect.NavigateToFriend -> navigateToGroup()
            is FeedSideEffect.NavigateToAlarm -> navigateToAlarm()
            is FeedSideEffect.NavigateToCertificationFriend -> navigateToCertifiedUserList(sideEffect.date)
            is FeedSideEffect.NavigateToMyPage -> navigateToMyPage()
            is FeedSideEffect.NavigateToUserPage -> navigateToUserProfile(sideEffect.userId)
            is FeedSideEffect.NavigateToPostDetail -> navigateToPostDetail(sideEffect.post.feedId)
            is FeedSideEffect.NavigateToPostUpload -> navigateToCertifiableRecord()
            is FeedSideEffect.NavigateToPostEdit -> navigateToPostEdit(sideEffect.feedId)
            is FeedSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
            is FeedSideEffect.RefreshPagingList -> feedPagingItems.refresh()
        }
    }

    FeedScreen(
        state = state,
        feedPagingItems = feedPagingItems,
        onIntent = viewModel::onIntent,
        onSaveFeedImage = { post, bitmap ->
            coroutineScope.launch {
                val fileName = "sixpack_feed_${post.feedId}_${System.currentTimeMillis()}"
                ImageSaver
                    .saveToGallery(
                        context = context,
                        bitmap = bitmap,
                        fileName = fileName,
                    ).onSuccess { uri ->
                        Toast
                            .makeText(
                                context,
                                "피드 이미지가 저장되었습니다",
                                Toast.LENGTH_SHORT,
                            ).show()
                    }.onFailure { exception ->
                        Toast
                            .makeText(
                                context,
                                "이미지 저장 실패: ${exception.message}",
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
            }
        },
    )
}

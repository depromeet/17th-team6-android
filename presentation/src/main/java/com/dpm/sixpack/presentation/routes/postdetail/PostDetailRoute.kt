package com.dpm.sixpack.presentation.routes.postdetail

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.common.util.capture.ImageSaver
import com.dpm.sixpack.presentation.routes.postdetail.contract.PostDetailSideEffect
import com.dpm.sixpack.presentation.routes.postdetail.ui.PostDetailScreen
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun PostDetailRoute(
    viewModel: PostDetailViewModel = hiltViewModel(),
    navigateToMyPage: () -> Unit,
    navigateToBack: () -> Unit,
    navigateToUserProfile: (Long) -> Unit,
    navigateToPostEdit: (Long) -> Unit,
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is PostDetailSideEffect.NavigateToBack -> navigateToBack()
            is PostDetailSideEffect.NavigateToMyPage -> navigateToMyPage()
            is PostDetailSideEffect.NavigateToUserPage -> navigateToUserProfile(sideEffect.userId)
            is PostDetailSideEffect.NavigateToPostEdit -> navigateToPostEdit(sideEffect.feedId)
            is PostDetailSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    PostDetailScreen(
        uiState = state,
        onIntent = viewModel::onIntent,
        onSavePostImage = { bitmap ->
            coroutineScope.launch {
                val fileName = "sixpack_post_${state.post?.feedId ?: System.currentTimeMillis()}_${System.currentTimeMillis()}"
                ImageSaver
                    .saveToGallery(
                        context = context,
                        bitmap = bitmap,
                        fileName = fileName,
                    ).onSuccess { uri ->
                        Toast
                            .makeText(
                                context,
                                "포스트 이미지가 저장되었습니다",
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

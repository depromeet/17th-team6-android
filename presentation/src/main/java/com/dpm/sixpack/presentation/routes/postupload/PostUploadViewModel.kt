package com.dpm.sixpack.presentation.routes.postupload

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.destinations.PostUpload
import com.dpm.sixpack.presentation.routes.postupload.contract.PostUploadIntent
import com.dpm.sixpack.presentation.routes.postupload.contract.PostUploadSideEffect
import com.dpm.sixpack.presentation.routes.postupload.contract.PostUploadUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PostUploadViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository,
) : BaseViewModel<PostUploadUiState, PostUploadIntent, PostUploadSideEffect>() {
    override val initialState: PostUploadUiState = PostUploadUiState()

    override val container: Container<PostUploadUiState, PostUploadSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    init {
        initializeState()
    }

    private fun initializeState() =
        intent {
            val route = savedStateHandle.toRoute<PostUpload>()

            reduce {
                state.copy(
                    sessionId = route.sessionId,
                    runningSummary = route.runningSummary,
                    mapImageUrl = route.mapImageUrl,
                )
            }
        }

    override fun onIntent(intent: PostUploadIntent) {
        when (intent) {
            PostUploadIntent.OnBackClick -> handleBackClick()
            PostUploadIntent.OnImageEditButtonClick -> handleImageClick()
            is PostUploadIntent.OnImageSelected -> handleImageSelected(intent.imageUri)
            is PostUploadIntent.OnImagePermissionResult -> handleImagePermissionResult(intent.isGranted)
            PostUploadIntent.OnUploadButtonClick -> handleUploadClick()
        }
    }

    private fun handleBackClick() =
        intent {
            postSideEffect(PostUploadSideEffect.NavigateBack)
        }

    private fun handleImageClick() =
        intent {
            if (state.hasImagePermission) {
                postSideEffect(PostUploadSideEffect.OpenImagePicker)
            } else {
                postSideEffect(PostUploadSideEffect.RequestImagePermission)
            }
        }

    private fun handleImageSelected(imageUri: Uri) =
        intent {
            reduce {
                state.copy(selectedImageUri = imageUri)
            }
        }

    private fun handleImagePermissionResult(isGranted: Boolean) =
        intent {
            reduce {
                state.copy(hasImagePermission = isGranted)
            }
        }

    private fun handleUploadClick() =
        intent {
            reduce { state.copy(isLoading = true) }

            viewModelScope.launch {
                feedRepository
                    .uploadPost(
                        sessionId = state.sessionId,
                        content = null,
                        imageUri = state.selectedImageUri,
                    ).onSuccess {
                        reduce { state.copy(isLoading = false) }
                        postSideEffect(PostUploadSideEffect.ShowToast("게시물이 업로드되었습니다."))
                        postSideEffect(PostUploadSideEffect.NavigateToFeed)
                    }.onError { error ->
                        reduce { state.copy(isLoading = false) }
                        postSideEffect(PostUploadSideEffect.ShowError(error.message ?: "게시물 업로드에 실패했습니다."))
                    }
            }
        }
}

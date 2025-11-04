package com.dpm.sixpack.presentation.routes.feed.postedit

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.feed.postedit.contract.PostEditIntent
import com.dpm.sixpack.presentation.routes.feed.postedit.contract.PostEditSideEffect
import com.dpm.sixpack.presentation.routes.feed.postedit.contract.PostEditUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PostEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository,
) : BaseViewModel<PostEditUiState, PostEditIntent, PostEditSideEffect>() {
    override val initialState: PostEditUiState = PostEditUiState()

    override val container: Container<PostEditUiState, PostEditSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    fun loadPost(feedId: Long) =
        intent {
            reduce { state.copy(isLoading = true) }

            viewModelScope.launch {
                // TODO: Implement getFeedById in FeedRepository
                // For now, use mock data
                // feedRepository.getFeedById(feedId)
                //     .onSuccess { feed ->
                //         reduce {
                //             state.copy(
                //                 originalPost = feed.toPostResource(),
                //                 isLoading = false,
                //             )
                //         }
                //     }
                //     .onError { error ->
                //         postSideEffect(PostEditSideEffect.ShowError(error.message ?: "게시물을 불러올 수 없습니다."))
                //         reduce { state.copy(isLoading = false) }
                //     }

                // Mock implementation - remove when API is ready
                delay(500) // Simulate network delay
                reduce {
                    state.copy(
                        originalPost =
                            state.originalPost.copy(
                                feedId = feedId,
                            ),
                        isLoading = false,
                    )
                }
            }
        }

    override fun onIntent(intent: PostEditIntent) {
        when (intent) {
            PostEditIntent.OnBackClick -> handleBackClick()
            PostEditIntent.OnSaveClick -> handleSaveClick()
            PostEditIntent.OnImageEditButtonClick -> handleImageClick()
            is PostEditIntent.OnImageSelected -> handleImageSelected(intent.imageUri)
            is PostEditIntent.OnImagePermissionResult -> handleImagePermissionResult(intent.isGranted)
            PostEditIntent.OnSubmitClick -> handleSubmitClick()
        }
    }

    private fun handleBackClick() =
        intent {
            postSideEffect(PostEditSideEffect.NavigateBack)
        }

    private fun handleSaveClick() =
        intent {
            val imageUrl = state.selectedImageUri?.toString() ?: state.originalPost.postImageUrl
            if (imageUrl.isNotEmpty()) {
                postSideEffect(PostEditSideEffect.SaveImageToGallery(imageUrl))
            } else {
                postSideEffect(PostEditSideEffect.ShowError("저장할 이미지가 없습니다."))
            }
        }

    private fun handleImageClick() =
        intent {
            if (state.hasImagePermission) {
                postSideEffect(PostEditSideEffect.OpenImagePicker)
            } else {
                postSideEffect(PostEditSideEffect.RequestImagePermission)
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

    private fun handleSubmitClick() =
        intent {
            val selectedImageUri = state.selectedImageUri
            if (selectedImageUri == null) {
                postSideEffect(PostEditSideEffect.ShowToast("변경된 이미지가 없습니다."))
                return@intent
            }

            reduce { state.copy(isLoading = true) }

            viewModelScope.launch {
                feedRepository
                    .updateSelfie(
                        feedId = state.originalPost.feedId,
                        content = "", // TODO: content 추가 필요 시 UiState에 추가
                        imageUri = selectedImageUri,
                        deleteSelfieImage = false,
                    ).onSuccess {
                        reduce { state.copy(isLoading = false) }
                        postSideEffect(PostEditSideEffect.ShowToast("게시물이 수정되었습니다."))
                        postSideEffect(PostEditSideEffect.NavigateBack)
                    }.onError { error ->
                        reduce { state.copy(isLoading = false) }
                        postSideEffect(PostEditSideEffect.ShowError(error.message ?: "게시물 수정에 실패했습니다."))
                    }
            }
        }
}

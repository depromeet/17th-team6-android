package com.dpm.sixpack.presentation.routes.postdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.components.post.PostDropDownActionType
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.PostReaction
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.common.model.ReactingUserInfo
import com.dpm.sixpack.presentation.common.model.toPostResource
import com.dpm.sixpack.presentation.destinations.PostDetail
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedBottomSheetState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedDialogState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.ReactionDetailsUiState
import com.dpm.sixpack.presentation.routes.postdetail.contract.PostDetailIntent
import com.dpm.sixpack.presentation.routes.postdetail.contract.PostDetailSideEffect
import com.dpm.sixpack.presentation.routes.postdetail.contract.PostDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

private const val REACTION_DEBOUNCE_MS = 1000L // 1초 debounce (Feed와 동일)

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository,
) : BaseViewModel<PostDetailUiState, PostDetailIntent, PostDetailSideEffect>() {
    override val initialState: PostDetailUiState = PostDetailUiState()

    override val container: Container<PostDetailUiState, PostDetailSideEffect> =
        container(
            initialState = initialState,
            savedStateHandle = savedStateHandle,
        )

    init{
        initializeState()
    }

    private fun initializeState() {
        val route = savedStateHandle.toRoute<PostDetail>()

        loadPost(route.feedId)
    }

    private fun loadPost(feedId: Long) =
        intent {
            reduce { state.copy(isLoading = true, error = null) }

            viewModelScope.launch {
                feedRepository
                    .getFeedDetail(feedId)
                    .onSuccess { feed ->
                        reduce {
                            state.copy(
                                post = feed.toPostResource(),
                                isLoading = false,
                                error = null,
                            )
                        }
                    }.onError { error ->
                        reduce {
                            state.copy(
                                isLoading = false,
                                error = error.message ?: "게시물을 불러올 수 없습니다.",
                            )
                        }
                    }
            }
        }

    // 리액션별로 독립적인 debounce Job 관리
    private val reactionDebounceJobs = ConcurrentHashMap<Pair<Long, Emoji>, Job>()

    override fun onIntent(intent: PostDetailIntent) {
        when (intent) {
            PostDetailIntent.OnBackClick -> handleBackClick()
            is PostDetailIntent.OnMenuClick -> handleMenuClick(intent.isExpanded)
            is PostDetailIntent.OnUserProfileClick -> handleUserProfileClick(intent.userId, intent.isMe)
            is PostDetailIntent.OnPostReactionClick ->
                handlePostReactionClick(
                    intent.post,
                    intent.emoji,
                    intent.isReacted,
                )

            is PostDetailIntent.OnPostReactionLongClick ->
                handlePostReactionLongClick(
                    intent.reactions,
                    intent.selectedEmoji,
                )

            is PostDetailIntent.OnAddReactionClick -> handleAddReactionClick(intent.post)
            is PostDetailIntent.OnDropDownMenuClick -> handleDropDownMenuClick(intent.post, intent.action)
            PostDetailIntent.OnBottomSheetDismiss -> handleBottomSheetDismiss()
            is PostDetailIntent.OnUserReactionSheetTabClick -> handleUserReactionSheetTabClick(intent.selectedEmoji)
            is PostDetailIntent.OnEmojiSheetEmojiSelected -> handleEmojiSheetEmojiSelected(intent.emoji)
            PostDetailIntent.OnRetryClick -> loadPost(container.stateFlow.value.feedId)
            PostDetailIntent.OnDialogDismiss -> handleDialogDismiss()
            PostDetailIntent.OnDialogConfirmClick -> handleDialogConfirmClick()
        }
    }

    private fun handleBackClick() =
        intent {
            postSideEffect(PostDetailSideEffect.NavigateToBack)
        }

    private fun handleMenuClick(isExpanded: Boolean) =
        intent {
            reduce {
                state.copy(isMenuExpanded = isExpanded)
            }
        }

    private fun handleUserProfileClick(
        userId: Long,
        isMe: Boolean,
    ) = intent {
        if (isMe) {
            postSideEffect(PostDetailSideEffect.NavigateToMyPage)
        } else {
            postSideEffect(PostDetailSideEffect.NavigateToUserPage(userId))
        }
    }

    private fun handlePostReactionClick(
        post: PostResource,
        emoji: Emoji,
        isReacted: Boolean,
    ) = intent {
        val reactionKey = post.feedId to emoji
        val newPost = post.updateReaction(emoji, isReacted)
        reduce {
            state.copy(post = newPost)
        }

        // 리액션별로 독립적으로 동작
        reactionDebounceJobs[reactionKey]?.cancel()
        reactionDebounceJobs[reactionKey] =
            viewModelScope.launch {
                delay(REACTION_DEBOUNCE_MS)
                try {
                    feedRepository
                        .postReaction(
                            selfieId = post.feedId,
                            emojiType = emoji.type,
                        ).onError { exception ->
                            // 실패 시 서버에서 최신 데이터 다시 가져오기
                            loadPost(post.feedId)
                            postSideEffect(
                                PostDetailSideEffect.ShowToast(
                                    exception.message ?: "반응 추가에 실패했습니다.",
                                ),
                            )
                        }
                } finally {
                    reactionDebounceJobs.remove(reactionKey)
                }
            }
    }

    private fun handlePostReactionLongClick(
        reactions: List<PostReaction>,
        selectedEmoji: Emoji,
    ) = intent {
        reduce {
            state.copy(
                bottomSheetState = state.bottomSheetState.copy(reactionUsers = true),
                reactionDetailsUiState =
                    ReactionDetailsUiState.Success(
                        reactions = reactions,
                        selectedEmoji = selectedEmoji,
                    ),
            )
        }
    }

    private fun handleAddReactionClick(post: PostResource) =
        intent {
            reduce {
                state.copy(
                    bottomSheetState = state.bottomSheetState.copy(emojiSelection = true),
                    postForEmojiSelection = post,
                )
            }
        }

    private fun handleDropDownMenuClick(
        post: PostResource,
        action: PostDropDownActionType,
    ) = intent {
        reduce { state.copy(isMenuExpanded = false) }

        when (action) {
            PostDropDownActionType.EDIT -> {
                postSideEffect(PostDetailSideEffect.NavigateToPostEdit(post.feedId))
            }

            PostDropDownActionType.DELETE -> {
                val newDialogState = state.dialogState.copy(deleteFeedId = post.feedId, actionType = action)
                reduce {
                    state.copy(
                        dialogState = newDialogState,
                    )
                }
            }

            PostDropDownActionType.SAVE_IMAGE -> {
                postSideEffect(PostDetailSideEffect.ShowToast("이미지 저장 기능은 준비 중입니다."))
            }

            PostDropDownActionType.REPORT -> {
                val newDialogState = state.dialogState.copy(reportFeedId = post.feedId, actionType = action)
                reduce {
                    state.copy(
                        dialogState = newDialogState,
                    )
                }
            }

            PostDropDownActionType.IDLE -> {}
        }
    }

    private fun handleDialogDismiss() =
        intent {
            reduce { state.copy(dialogState = FeedDialogState()) }
        }

    private fun handleDialogConfirmClick() =
        intent {
            val dialogState = state.dialogState

            reduce { state.copy(dialogState = FeedDialogState()) }

            when (dialogState.actionType) {
                PostDropDownActionType.DELETE -> {
                    dialogState.deleteFeedId?.let { deletePost(it) }
                }

                PostDropDownActionType.REPORT -> {
                    // TODO: 신고 로직
                    postSideEffect(PostDetailSideEffect.ShowToast("신고 기능은 준비 중입니다."))
                }

                else -> {}
            }
        }

    private fun deletePost(feedId: Long) =
        intent {
            viewModelScope.launch {
                feedRepository
                    .deletePost(feedId)
                    .onSuccess {
                        postSideEffect(PostDetailSideEffect.ShowToast("게시물이 삭제되었습니다."))
                        postSideEffect(PostDetailSideEffect.NavigateToBack)
                    }.onError { exception ->
                        postSideEffect(
                            PostDetailSideEffect.ShowToast(
                                exception.message ?: "게시물 삭제에 실패했습니다.",
                            ),
                        )
                    }
            }
        }

    private fun handleBottomSheetDismiss() =
        intent {
            reduce {
                state.copy(
                    bottomSheetState = FeedBottomSheetState(reactionUsers = false, emojiSelection = false),
                    postForEmojiSelection = null,
                )
            }
        }

    private fun handleUserReactionSheetTabClick(selectedEmoji: Emoji) =
        intent {
            val currentReactionState = state.reactionDetailsUiState
            if (currentReactionState is ReactionDetailsUiState.Success) {
                reduce {
                    state.copy(
                        reactionDetailsUiState = currentReactionState.copy(selectedEmoji = selectedEmoji),
                    )
                }
            }
        }

    private fun handleEmojiSheetEmojiSelected(emoji: Emoji) =
        intent {
            val postToUpdate = state.postForEmojiSelection ?: return@intent

            val reactionKey = postToUpdate.feedId to emoji
            val newPost = postToUpdate.updateReaction(emoji, isReacted = true)
            reduce {
                state.copy(
                    post = newPost,
                    bottomSheetState = FeedBottomSheetState(emojiSelection = false),
                    postForEmojiSelection = null,
                )
            }

            // 이전 Job 취소 및 새 Job 시작
            reactionDebounceJobs[reactionKey]?.cancel()
            reactionDebounceJobs[reactionKey] =
                viewModelScope.launch {
                    delay(REACTION_DEBOUNCE_MS)
                    try {
                        feedRepository
                            .postReaction(
                                selfieId = postToUpdate.feedId,
                                emojiType = emoji.type,
                            ).onError { exception ->
                                // 실패 시 서버에서 최신 데이터 다시 가져오기
                                loadPost(postToUpdate.feedId)
                                postSideEffect(
                                    PostDetailSideEffect.ShowToast(
                                        exception.message ?: "리액션 추가에 실패했습니다.",
                                    ),
                                )
                            }
                    } finally {
                        reactionDebounceJobs.remove(reactionKey)
                    }
                }
        }

    private fun PostResource.updateReaction(
        emoji: Emoji,
        isReacted: Boolean,
    ): PostResource {
        val targetReaction = this.reactions.find { it.emoji == emoji }

        // TODO 내 정보 저장해놓고 가져오기 SB
        // 내 정보 생성 (PostDetail에서는 state에서 가져오지 않고 현재 post의 user 정보 사용)
        val myUserInfo = this.user.user

        val currentTime = System.currentTimeMillis().toString()

        val myReactingUserInfo =
            ReactingUserInfo(
                user = myUserInfo,
                reactedAt = currentTime,
                emoji = emoji,
            )

        val newReactions =
            if (targetReaction == null && isReacted) {
                // 새 리액션 추가
                this.reactions +
                    PostReaction(
                        emoji = emoji,
                        count = "1",
                        isReacted = true,
                        users = listOf(myReactingUserInfo),
                    )
            } else if (targetReaction != null) {
                // 기존 리액션 수정
                this.reactions
                    .map {
                        if (it.emoji == emoji) {
                            val newCount = (it.count.toIntOrNull() ?: 0) + (if (isReacted) 1 else -1)
                            val newUsers =
                                if (isReacted) {
                                    // 리액션 추가: 현재 사용자를 리스트에 추가
                                    it.users + myReactingUserInfo
                                } else {
                                    // 리액션 제거: 현재 사용자를 리스트에서 제거
                                    it.users.filterNot { user -> user.user.isMe }
                                }
                            it.copy(
                                count = newCount.toString(),
                                isReacted = isReacted,
                                users = newUsers,
                            )
                        } else {
                            it
                        }
                    }.filter { (it.count.toIntOrNull() ?: 0) > 0 }
            } else {
                this.reactions
            }

        return this.copy(reactions = newReactions)
    }
}

package com.dpm.sixpack.presentation.routes.friend

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.Friend
import com.dpm.sixpack.domain.usecase.friend.AddFriendByCodeUseCase
import com.dpm.sixpack.domain.usecase.friend.DeleteFriendUseCase
import com.dpm.sixpack.domain.usecase.friend.GetFriendRunningStatusUseCase
import com.dpm.sixpack.domain.usecase.friend.GetMyFriendCodeUseCase
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.model.FriendItem
import com.dpm.sixpack.presentation.common.model.toUiItem
import com.dpm.sixpack.presentation.routes.friend.contract.AddFriendIntent
import com.dpm.sixpack.presentation.routes.friend.contract.FriendIntent
import com.dpm.sixpack.presentation.routes.friend.contract.FriendListIntent
import com.dpm.sixpack.presentation.routes.friend.contract.FriendSideEffect
import com.dpm.sixpack.presentation.routes.friend.contract.FriendUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

private const val CODE_INPUT_LENGTH = 8

@HiltViewModel
class FriendViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getFriendListUseCase: GetFriendRunningStatusUseCase,
    private val addFriendByCodeUseCase: AddFriendByCodeUseCase,
    private val getMyCodeUseCase: GetMyFriendCodeUseCase,
    private val deleteFriendUseCase: DeleteFriendUseCase,
) : BaseViewModel<FriendUiState, FriendIntent, FriendSideEffect>() {
    override val initialState: FriendUiState = FriendUiState.FriendList()

    override val container: Container<FriendUiState, FriendSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    private val refreshTrigger = MutableStateFlow(0)

    val friendPagingFlow: Flow<PagingData<FriendItem>> =
        refreshTrigger
            .flatMapLatest {
                getFriendListUseCase()
                    .map { pagingData: PagingData<Friend> ->
                        pagingData.map { friend: Friend ->
                            friend.toUiItem()
                        }
                    }
            }.cachedIn(viewModelScope)

    override fun onIntent(intent: FriendIntent) {
        when (intent) {
            is FriendListIntent -> onFriendListIntent(intent)
            is AddFriendIntent -> onAddFriendIntent(intent)
        }
    }

    private fun onFriendListIntent(intent: FriendListIntent) {
        when (intent) {
            FriendListIntent.NavigateBackClick -> handleNavigateBackClick()
            FriendListIntent.AddFriendClick -> handleAddFriendClick()
            FriendListIntent.MyCodeCopyClick -> handleMyCodeCopyClick()
            is FriendListIntent.OptionClick -> handleOptionClick(intent.userId)
            FriendListIntent.DismissOptionMenu -> handleDismissOptionMenu()
            is FriendListIntent.ShowDeleteDialog -> handleDeleteClick(intent.userId)
            FriendListIntent.DismissDeleteDialog -> handleDismissDeleteDialog()
            is FriendListIntent.ConfirmDeleteFriend -> confirmDeleteFriend(intent.userId)
            FriendListIntent.Refresh -> handleRefresh()
        }
    }

    private fun onAddFriendIntent(intent: AddFriendIntent) {
        when (intent) {
            is AddFriendIntent.InputChanged -> handleInputChange(intent.input)
            is AddFriendIntent.NavigateToFriendList -> handleNavigateToFriendList()
            is AddFriendIntent.AddFriendByCode -> handleAddFriend(intent.code)
        }
    }

    // region FriendList
    private fun handleOptionClick(uid: Long) =
        intent {
            val curState = state as? FriendUiState.FriendList ?: return@intent
            reduce {
                curState.copy(
                    showOptionForUserId = uid,
                )
            }
        }

    private fun handleDismissOptionMenu() =
        intent {
            val curState = state as? FriendUiState.FriendList ?: return@intent
            reduce {
                curState.copy(
                    showOptionForUserId = null,
                )
            }
        }

    private fun handleDeleteClick(uid: Long) =
        intent {
            val curState = state as? FriendUiState.FriendList ?: return@intent
            reduce {
                curState.copy(
                    showDeleteDialogForUserId = uid,
                )
            }
        }

    private fun handleDismissDeleteDialog() =
        intent {
            val curState = state as? FriendUiState.FriendList ?: return@intent
            reduce {
                curState.copy(
                    showDeleteDialogForUserId = null,
                )
            }
        }

    private fun confirmDeleteFriend(uid: Long) =
        intent {
            val curState = state as? FriendUiState.FriendList ?: return@intent
            deleteFriendUseCase(listOf(uid))
                .onSuccess { uids ->
                    Timber.d("Success to delete Friend: ${uids.joinToString(" ")}")
                    handleRefresh()
                }.onError {
                    Timber.w("Failed to delete Friend: ${it.message}")
                }

            reduce {
                curState.copy(
                    showDeleteDialogForUserId = null,
                )
            }
        }

    // Add 화면으로 전환
    private fun handleAddFriendClick() =
        intent {
            reduce {
                FriendUiState.AddingFriend()
            }
            postSideEffect(FriendSideEffect.NavigateToAddFriend)
        }

    private fun handleMyCodeCopyClick() =
        intent {
            getMyCodeUseCase()
                .onSuccess { code ->
                    Timber.d("Success to copy code to clipboard: $code")
                    postSideEffect(
                        FriendSideEffect.CopyToClipboard(code, 0),
                    )
                }.onError {
                    Timber.w("Failed to copy code to clipboard: ${it.message}")
                }
        }

    private fun handleNavigateBackClick() =
        intent {
            postSideEffect(FriendSideEffect.NavigateToBack)
        }

    private fun handleRefresh() =
        intent {
            refreshTrigger.value++
        }

    // endregion

    // region AddingFriend

    private fun handleNavigateToFriendList() =
        intent {
            reduce {
                FriendUiState.FriendList()
            }
            postSideEffect(FriendSideEffect.NavigateToFriendList)
        }

    private fun handleAddFriend(code: String) =
        intent {
            if (state is FriendUiState.AddingFriend) {
                addFriendByCodeUseCase(code)
                    .onSuccess { nickname ->
                        Timber.d("Success to add friend: $code")
                        postSideEffect(FriendSideEffect.ShowToast(R.string.friend_add_success, nickname))
                        reduce {
                            FriendUiState.FriendList()
                        }
                        postSideEffect(FriendSideEffect.NavigateToFriendList)
                        handleRefresh()
                    }.onError { exception ->
                        if (exception is DoRunException.ServerError) {
                            if (exception.code == 400) {
                                postSideEffect(FriendSideEffect.ShowToast(R.string.friend_code_invalid))
                            } else if (exception.code == 404) {
                                postSideEffect(FriendSideEffect.ShowToast(R.string.friend_code_no_exist))
                            }
                        } else {
                            postSideEffect(FriendSideEffect.ShowToast(R.string.friend_code_invalid))
                        }
                        Timber.w("Failed to add friend: ${exception.message}")
                    }
            }
        }

    private fun handleInputChange(input: String) {
        intent {
            val curState = state as? FriendUiState.AddingFriend ?: return@intent

            reduce {
                curState.copy(
                    input = input,
                    enterButtonEnabled = input.length == CODE_INPUT_LENGTH,
                )
            }
        }
    }

    // endregion
}

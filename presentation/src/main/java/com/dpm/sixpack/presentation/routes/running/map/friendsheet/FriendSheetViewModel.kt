package com.dpm.sixpack.presentation.routes.running.map.friendsheet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.dpm.sixpack.domain.model.Friend
import com.dpm.sixpack.domain.usecase.friend.GetFriendRunningStatusUseCase
import com.dpm.sixpack.domain.usecase.friend.PostFriendNotificationUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.model.FriendUiItem
import com.dpm.sixpack.presentation.common.model.toUiItem
import com.dpm.sixpack.presentation.routes.running.map.friendsheet.contract.FriendSheetIntent
import com.dpm.sixpack.presentation.routes.running.map.friendsheet.contract.FriendSheetSideEffect
import com.dpm.sixpack.presentation.routes.running.map.friendsheet.contract.FriendSheetUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FriendSheetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getFriendRunningStatusUseCase: GetFriendRunningStatusUseCase,
    private val postFriendNotificationUseCase: PostFriendNotificationUseCase,
) : BaseViewModel<FriendSheetUiState, FriendSheetIntent, FriendSheetSideEffect>() {
    override val initialState: FriendSheetUiState = FriendSheetUiState()

    override val container: Container<FriendSheetUiState, FriendSheetSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    private val refreshTrigger = MutableStateFlow(Unit)

    val friendPagingFlow: Flow<PagingData<FriendUiItem>> =
        refreshTrigger
            .flatMapLatest {
                // 'refreshTrigger'의 값이 바뀔 때마다 (emit될 때마다)
                //  flatMapLatest가 기존 Flow를 취소하고 getFriendRunningStatusUseCase()를 '새로' 호출합니다.
                getFriendRunningStatusUseCase()
            }.map { pagingData: PagingData<Friend> ->
                pagingData.map { friend: Friend ->
                    friend.toUiItem()
                }
            }.cachedIn(viewModelScope)

    override fun onIntent(intent: FriendSheetIntent) {
        when (intent) {
            is FriendSheetIntent.AwakeFriend -> TODO()
            is FriendSheetIntent.ClickUser -> handleClickUser(intent.userId)
        }
    }

    private fun handleAwakeFriend(userId: Long) =
        intent {
            postFriendNotificationUseCase(userId)
                .onSuccess {
                    refresh()
                }.onError { e ->
                    Timber.w("FriendSheetViewModel: Failed to post friend notification: ${e.message}")
                }
        }

    private fun handleClickUser(userId: Long) =
        intent {
            postSideEffect(FriendSheetSideEffect.UserItemClicked(userId))
        }

    private fun refresh() =
        intent {
            refreshTrigger.emit(Unit)
        }
}

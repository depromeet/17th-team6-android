package com.dpm.sixpack.presentation.routes.feed.certifiedusers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.model.toPostingUserInfo
import com.dpm.sixpack.presentation.routes.feed.certifiedusers.contract.CertifiedUsersIntent
import com.dpm.sixpack.presentation.routes.feed.certifiedusers.contract.CertifiedUsersSideEffect
import com.dpm.sixpack.presentation.routes.feed.certifiedusers.contract.CertifiedUsersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class CertifiedUsersViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository,
) : BaseViewModel<CertifiedUsersUiState, CertifiedUsersIntent, CertifiedUsersSideEffect>() {
    override val initialState: CertifiedUsersUiState = CertifiedUsersUiState()

    override val container: Container<CertifiedUsersUiState, CertifiedUsersSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    fun loadCertifiedUsers(date: String) =
        intent {
            reduce { state.copy(isLoading = true) }

            viewModelScope.launch {
                feedRepository
                    .getCertifiedUsers(date)
                    .onSuccess { certifiedUsers ->
                        reduce {
                            state.copy(
                                users = certifiedUsers.map { it.toPostingUserInfo() },
                                isLoading = false,
                            )
                        }
                    }.onError { error ->
                        reduce { state.copy(isLoading = false) }
                    }
            }
        }

    override fun onIntent(intent: CertifiedUsersIntent) {
        when (intent) {
            CertifiedUsersIntent.OnBackIconClick -> handleBackClick()
            is CertifiedUsersIntent.OnUserProfileClick ->
                handleUserProfileClick(
                    intent.userId,
                    intent.isMe,
                )
        }
    }

    private fun handleBackClick() =
        intent {
            postSideEffect(CertifiedUsersSideEffect.NavigateBack)
        }

    private fun handleUserProfileClick(
        userId: Long,
        isMe: Boolean,
    ) = intent {
        if (isMe) {
            postSideEffect(CertifiedUsersSideEffect.NavigateToMyPage)
        } else {
            postSideEffect(CertifiedUsersSideEffect.NavigateToUserPage(userId))
        }
    }
}

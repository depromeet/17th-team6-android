package com.dpm.sixpack.presentation.routes.sessionreport

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.domain.usecase.running.GetSessionDetailUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.sessionreport.contract.SessionDetailIntent
import com.dpm.sixpack.presentation.routes.sessionreport.contract.SessionDetailSideEffect
import com.dpm.sixpack.presentation.routes.sessionreport.contract.SessionDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSessionDetailUseCase: GetSessionDetailUseCase,
) : BaseViewModel<SessionDetailState, SessionDetailIntent, SessionDetailSideEffect>() {
    override val initialState: SessionDetailState = SessionDetailState.Loading

    override val container: Container<SessionDetailState, SessionDetailSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: SessionDetailIntent) {
        when (intent) {
            is SessionDetailIntent.LoadSessionDetail -> handleLoadSessionDetail(intent.sessionId)
            SessionDetailIntent.NavigateBack -> handleNavigateBack()
            is SessionDetailIntent.NavigateToCertification -> handleNavigateToCertification(intent.sessionId)
            SessionDetailIntent.RetryLoad -> handleRetryLoad()
        }
    }

    private fun handleLoadSessionDetail(sessionId: Long) =
        intent {
            getSessionDetailUseCase(sessionId)
                .onSuccess {
                    Timber.d("Success to get session detail: $it")
                    reduce {
                        SessionDetailState.Success(
                            sessionDetail = it,
                        )
                    }
                }.onError {
                    Timber.w("Failed to get session detail: ${it.message}")
                    reduce {
                        SessionDetailState.Error
                    }
                }
        }

    private fun handleNavigateBack() =
        intent {
            postSideEffect(SessionDetailSideEffect.NavigateBack)
        }

    private fun handleNavigateToCertification(sessionId: Long) =
        intent {
            postSideEffect(SessionDetailSideEffect.NavigateToCertification(sessionId))
        }

    private fun handleRetryLoad() =
        intent {
        }
}

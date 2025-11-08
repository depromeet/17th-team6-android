package com.dpm.sixpack.presentation.routes.report

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.domain.usecase.running.GetSessionDetailUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.report.contract.ReportIntent
import com.dpm.sixpack.presentation.routes.report.contract.ReportSideEffect
import com.dpm.sixpack.presentation.routes.report.contract.ReportState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SessionReportViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSessionDetailUseCase: GetSessionDetailUseCase,
) : BaseViewModel<ReportState, ReportIntent, ReportSideEffect>() {
    override val initialState: ReportState = ReportState.Loading

    override val container: Container<ReportState, ReportSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: ReportIntent) {
        when (intent) {
            is ReportIntent.LoadSessionDetail -> handleLoadSessionDetail(intent.sessionId)
            ReportIntent.NavigateBack -> handleNavigateBack()
            is ReportIntent.NavigateToPostEdit -> handleNavigateToPostEdit(intent.sessionId)
            ReportIntent.RetryLoad -> handleRetryLoad()
        }
    }

    private fun handleLoadSessionDetail(sessionId: Long) =
        intent {
            getSessionDetailUseCase(sessionId)
                .onSuccess {
                    Timber.d("Success to get session detail: $it")
                    reduce {
                        ReportState.Success(
                            sessionDetail = it,
                        )
                    }
                }.onError {
                    Timber.w("Failed to get session detail: ${it.message}")
                    reduce {
                        ReportState.Error
                    }
                }
        }

    private fun handleNavigateBack() =
        intent {
            postSideEffect(ReportSideEffect.NavigateBack)
        }

    private fun handleNavigateToPostEdit(sessionId: Long) =
        intent {
            postSideEffect(ReportSideEffect.NavigateToPostEdit(sessionId))
        }

    private fun handleRetryLoad() =
        intent {
        }
}

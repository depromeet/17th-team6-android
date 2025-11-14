package com.dpm.sixpack.presentation.routes.report

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.MaxPaceData
import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.domain.usecase.feed.GetFeedUploadableUseCase
import com.dpm.sixpack.domain.usecase.running.GetSessionDetailUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.model.toRunningSummary
import com.dpm.sixpack.presentation.routes.report.contract.ReportBottomBarType
import com.dpm.sixpack.presentation.routes.report.contract.ReportIntent
import com.dpm.sixpack.presentation.routes.report.contract.ReportSideEffect
import com.dpm.sixpack.presentation.routes.report.contract.ReportState
import com.dpm.sixpack.presentation.routes.report.contract.toBottomBarType
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SessionReportViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSessionDetailUseCase: GetSessionDetailUseCase,
    private val getFeedUploadableUseCase: GetFeedUploadableUseCase,
) : BaseViewModel<ReportState, ReportIntent, ReportSideEffect>() {
    private val sessionId: Long = savedStateHandle.get<Long>("sessionId") ?: -1L

    override val initialState: ReportState = ReportState.Loading

    override val container: Container<ReportState, ReportSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: ReportIntent) {
        when (intent) {
            is ReportIntent.LoadSessionDetail -> handleLoadSessionDetail(sessionId)
            ReportIntent.NavigateBack -> handleNavigateBack()
            is ReportIntent.NavigateToPostUpload -> handleNavigateToPostEdit()
            ReportIntent.NavigateToPostDetail -> handleNavigateToPostUpload()
        }
    }

    private fun handleLoadSessionDetail(sessionId: Long) =
        intent {
            reduce {
                ReportState.Loading
            }

            if (sessionId != -1L) {
                getSessionDetailUseCase(sessionId)
                    .onSuccess { detail ->
                        Timber.d("Success to get session detail: $detail")
                        getFeedUploadableUseCase(sessionId)
                            .onSuccess { uploadable ->
                                Timber.d("Success to get uploadable: $uploadable")
                                reduce {
                                    ReportState.Success(
                                        sessionDetail = detail,
                                        bottomBarType = uploadable.toBottomBarType(),
                                    )
                                }
                            }.onError {
                                Timber.w("Failed to get uploadable: ${it.message}")
                                reduce {
                                    ReportState.Success(
                                        sessionDetail = detail,
                                        bottomBarType = ReportBottomBarType.NONE,
                                    )
                                }
                            }
                    }.onError {
                        Timber.w("Failed to get session detail: ${it.message}")
                        val code = if (it is DoRunException.DataError) 404 else 0
                        reduce {
                            ReportState.Error(code)
                        }
                    }
            } else {
                // -1 이면 이전 화면에서 종료 API 실패
                reduce {
                    ReportState.Error()
                }
            }
        }

    private fun handleNavigateBack() =
        intent {
            postSideEffect(ReportSideEffect.NavigateBack)
        }

    private fun handleNavigateToPostEdit() =
        intent {
            val sessionDetail = (state as? ReportState.Success)?.sessionDetail ?: return@intent
            val sessionId = sessionDetail.id
            val mapImageUrl = sessionDetail.mapImage
            val runningSummary =
                RunningSessionResult(
                    totalDistanceMeter = sessionDetail.distanceTotal,
                    totalDurationSec = sessionDetail.durationTotal,
                    avgPace = sessionDetail.paceAvg,
                    maxPace =
                        MaxPaceData(
                            value = sessionDetail.paceMax,
                            latitude = 0.0,
                            longitude = 0.0,
                        ),
                    avgCadence = sessionDetail.cadenceAvg,
                    maxCadence = sessionDetail.cadenceMax,
                ).toRunningSummary(sessionDetail.finishedAt)
            postSideEffect(ReportSideEffect.NavigateToPostUpload(sessionId, mapImageUrl, runningSummary))
        }

    private fun handleNavigateToPostUpload() =
        intent {
            val feedId = (state as? ReportState.Success)?.sessionDetail?.feed?.id ?: return@intent
            postSideEffect(ReportSideEffect.NavigateToPostDetail(feedId))
        }

    private fun handleRetryLoad() =
        intent {
        }
}

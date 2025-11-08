package com.dpm.sixpack.presentation.routes.feed.certifiablerecord

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.model.RecordItem
import com.dpm.sixpack.presentation.common.model.RunningSummary
import com.dpm.sixpack.presentation.routes.feed.certifiablerecord.contract.CertifiableRecordIntent
import com.dpm.sixpack.presentation.routes.feed.certifiablerecord.contract.CertifiableRecordSideEffect
import com.dpm.sixpack.presentation.routes.feed.certifiablerecord.contract.CertifiableRecordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class CertifiableRecordViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    // TODO: Repository 추가 시 주입
    // private val runningRepository: RunningRepository,
) : BaseViewModel<CertifiableRecordUiState, CertifiableRecordIntent, CertifiableRecordSideEffect>() {
    override val initialState: CertifiableRecordUiState = CertifiableRecordUiState()

    override val container: Container<CertifiableRecordUiState, CertifiableRecordSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    init {
        loadCertifiableRecords()
    }

    private fun loadCertifiableRecords() =
        intent {
            reduce { state.copy(isLoading = true) }

            // TODO: Repository에서 인증 가능한 러닝 기록 가져오기
            // runningRepository.getUncertifiedRunSessions()
            //     .onSuccess { sessions ->
            //         reduce {
            //             state.copy(
            //                 records = sessions.toRecordItems(),
            //                 isLoading = false,
            //             )
            //         }
            //     }
            //     .onError { error ->
            //         reduce { state.copy(isLoading = false) }
            //     }

            // Mock data for now
            reduce {
                state.copy(
                    records =
                        listOf(
                            RecordItem(
                                sessionId = 1L,
                                runningSummary =
                                    RunningSummary(
                                        totalDistance = "10.50 km",
                                        totalTime = "00:55:12",
                                        averagePace = "5'15''",
                                        cadence = "170",
                                        recordDateTime = "2025.11.08 09:00",
                                    ),
                                mapImageUrl = "https://picsum.photos/id/237/200/300",
                                isPosted = false,
                                postTime = "2025-11-08T09:00:15Z", // ISO 8601 형식
                            ),
                            RecordItem(
                                sessionId = 2L,
                                runningSummary =
                                    RunningSummary(
                                        totalDistance = "5.30 km",
                                        totalTime = "00:28:45",
                                        averagePace = "5'25''",
                                        cadence = "168",
                                        recordDateTime = "2025.11.07 18:30",
                                    ),
                                mapImageUrl = "https://picsum.photos/id/237/200/300",
                                isPosted = false,
                                postTime = "2025-11-07T18:30:00Z",
                            ),
                            RecordItem(
                                sessionId = 3L,
                                runningSummary =
                                    RunningSummary(
                                        totalDistance = "3.00 km",
                                        totalTime = "00:17:30",
                                        averagePace = "5'50''",
                                        cadence = "172",
                                        recordDateTime = "2025.11.05 07:15",
                                    ),
                                mapImageUrl = "https://picsum.photos/id/237/200/300",
                                isPosted = false,
                                postTime = "2025-11-05T07:15:20Z",
                            ),
                        ),
                    isLoading = false,
                )
            }
        }

    override fun onIntent(intent: CertifiableRecordIntent) {
        when (intent) {
            CertifiableRecordIntent.OnBackClick -> handleBackClick()
            is CertifiableRecordIntent.OnRecordClick -> handleRecordClick(intent.record)
            CertifiableRecordIntent.OnUploadClick -> handleUploadClick()
        }
    }

    private fun handleBackClick() =
        intent {
            postSideEffect(CertifiableRecordSideEffect.NavigateBack)
        }

    private fun handleRecordClick(record: RecordItem) =
        intent {
            reduce {
                state.copy(
                    selectedRecord = record,
                )
            }
        }

    private fun handleUploadClick() =
        intent {
            val selectedRecord =
                state.records.find { it.sessionId == state.selectedRecord?.sessionId }

            if (selectedRecord != null) {
                postSideEffect(CertifiableRecordSideEffect.NavigateToPostUpload(selectedRecord))
            } else {
                postSideEffect(CertifiableRecordSideEffect.ShowNoRecordSelectedError)
            }
        }
}

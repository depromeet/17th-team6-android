package com.dpm.sixpack.presentation.routes.feed.certifiablerecord

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.model.RecordItem
import com.dpm.sixpack.presentation.common.model.toRecordItems
import com.dpm.sixpack.presentation.routes.feed.certifiablerecord.contract.CertifiableRecordIntent
import com.dpm.sixpack.presentation.routes.feed.certifiablerecord.contract.CertifiableRecordSideEffect
import com.dpm.sixpack.presentation.routes.feed.certifiablerecord.contract.CertifiableRecordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CertifiableRecordViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    // TODO: Repository 추가 시 주입
    private val runningRepository: RunningSessionRepository,
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
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val today = LocalDateTime.now().toLocalDate()
            val startOfDay = today.atStartOfDay()
            val startDateTime = startOfDay.format(formatter)

            // 1. 먼저 오늘 인증한 기록이 있는지 확인 (isSelfied = true)
            runningRepository
                .getRunSessions(isSelfied = true, startDateTime = startDateTime, endDateTime = null)
                .onSuccess { certifiedSessions ->
                    if (certifiedSessions.isNotEmpty()) {
                        // 오늘 이미 인증한 기록이 있음 -> certifiedRecord에 저장
                        reduce {
                            state.copy(
                                certifiedRecord = certifiedSessions.toRecordItems().first(),
                                records = emptyList(),
                                isLoading = false,
                            )
                        }
                    } else {
                        // 인증한 기록이 없음 -> 인증 가능한 기록 조회 (isSelfied = false)
                        loadUncertifiedRecords(startDateTime)
                    }
                }.onError { error ->
                    // 에러 발생 시에도 인증 가능한 기록 조회 시도
                    loadUncertifiedRecords(startDateTime)
                }
        }

    private fun loadUncertifiedRecords(startDateTime: String) =
        intent {
            runningRepository
                .getRunSessions(isSelfied = false, startDateTime = startDateTime, endDateTime = null)
                .onSuccess { sessions ->
                    reduce {
                        state.copy(
                            records = sessions.toRecordItems(),
                            isLoading = false,
                        )
                    }
                }.onError { error ->
                    reduce { state.copy(isLoading = false) }
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

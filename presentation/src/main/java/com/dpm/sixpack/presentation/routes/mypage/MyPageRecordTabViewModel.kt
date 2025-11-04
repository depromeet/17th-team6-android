package com.dpm.sixpack.presentation.routes.mypage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.mypage.contract.CertificationStatus
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageRecordTabIntent
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageRecordTabSideEffect
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageRecordTabState
import com.dpm.sixpack.presentation.routes.mypage.contract.RecordItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MyPageRecordTabViewModel
@Inject
    constructor(
        savedStateHandle: SavedStateHandle,
    ) : BaseViewModel<MyPageRecordTabState, MyPageRecordTabIntent, MyPageRecordTabSideEffect>() {
        override val initialState: MyPageRecordTabState = MyPageRecordTabState()

        override val container: Container<MyPageRecordTabState, MyPageRecordTabSideEffect> =
            container(initialState = initialState, savedStateHandle = savedStateHandle)

        init {
            loadMockData()
        }

        override fun onIntent(intent: MyPageRecordTabIntent) {
            when (intent) {
                is MyPageRecordTabIntent.OnPreviousMonthClick -> handlePreviousMonthClick()
                is MyPageRecordTabIntent.OnNextMonthClick -> handleNextMonthClick()
                is MyPageRecordTabIntent.OnRecordClick -> handleRecordClick(intent.recordId)
            }
        }

        private fun handlePreviousMonthClick() =
            intent {
                reduce {
                    state.copy(
                        currentYearMonth = state.currentYearMonth.addMonths(-1),
                    )
                }
            }

        private fun handleNextMonthClick() =
            intent {
                reduce {
                    state.copy(
                        currentYearMonth = state.currentYearMonth.addMonths(1),
                    )
                }
            }

        private fun handleRecordClick(recordId: Long) =
            intent {
                postSideEffect(MyPageRecordTabSideEffect.NavigateToRecordDetail(recordId))
            }

        private fun loadMockData() {
            viewModelScope.launch {
                // TODO: Replace with real data from repository
                val mockRecords =
                    listOf(
                        RecordItem(
                            id = 1,
                            date = "2025.09.30 (화)",
                            time = "오전 10:11",
                            distanceKm = 8.02,
                            durationFormatted = "01:12:03",
                            paceFormatted = "6'74\"",
                            cadence = 128,
                            certificationStatus = null,
                        ),
                        RecordItem(
                            id = 2,
                            date = "2025.09.29 (월)",
                            time = "오전 10:11",
                            distanceKm = 8.02,
                            durationFormatted = "01:12:03",
                            paceFormatted = "6'74\"",
                            cadence = 128,
                            certificationStatus = CertificationStatus.AVAILABLE,
                        ),
                        RecordItem(
                            id = 3,
                            date = "2025.09.27 (토)",
                            time = "오전 10:11",
                            distanceKm = 8.02,
                            durationFormatted = "01:12:03",
                            paceFormatted = "6'74\"",
                            cadence = 128,
                            certificationStatus = CertificationStatus.COMPLETED,
                        ),
                    )

                intent {
                    reduce {
                        state.copy(records = mockRecords)
                    }
                }
            }
        }
    }

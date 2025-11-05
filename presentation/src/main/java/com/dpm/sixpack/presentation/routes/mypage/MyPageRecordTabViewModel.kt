package com.dpm.sixpack.presentation.routes.mypage

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.domain.usecase.GetRunSessionsUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageRecordTabIntent
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageRecordTabSideEffect
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageRecordTabState
import com.dpm.sixpack.presentation.routes.mypage.contract.YearMonth
import com.dpm.sixpack.presentation.routes.mypage.util.RunSessionMapper.toRecordItems
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MyPageRecordTabViewModel
@Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val getRunSessionsUseCase: GetRunSessionsUseCase,
    ) : BaseViewModel<MyPageRecordTabState, MyPageRecordTabIntent, MyPageRecordTabSideEffect>() {
        override val initialState: MyPageRecordTabState =
            MyPageRecordTabState(
                currentYearMonth =
                    YearMonth(
                        year = LocalDate.now().year,
                        month = LocalDate.now().monthValue,
                    ),
            )

        override val container: Container<MyPageRecordTabState, MyPageRecordTabSideEffect> =
            container(initialState = initialState, savedStateHandle = savedStateHandle)

        init {
            loadRecords()
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
                loadRecords()
            }

        private fun handleNextMonthClick() =
            intent {
                reduce {
                    state.copy(
                        currentYearMonth = state.currentYearMonth.addMonths(1),
                    )
                }
                loadRecords()
            }

        private fun handleRecordClick(recordId: Long) =
            intent {
                postSideEffect(MyPageRecordTabSideEffect.NavigateToRecordDetail(recordId))
            }

        private fun loadRecords() =
            intent {
                reduce { state.copy(isLoading = true) }

                getRunSessionsUseCase(
                    yearMonth = state.currentYearMonth.year to state.currentYearMonth.month,
                    isSelfied = null,
                ).onSuccess { runSessions ->
                    val records = runSessions.toRecordItems()
                    val navigationState = calculateNavigationState()

                    reduce {
                        state.copy(
                            records = records,
                            isLoading = false,
                            canGoPreviousMonth = navigationState.canGoPrevious,
                            canGoNextMonth = navigationState.canGoNext,
                        )
                    }
                }.onError { exception ->
                    Timber.e("Failed to load run sessions: ${exception.message}")
                    reduce {
                        state.copy(
                            records = emptyList(),
                            isLoading = false,
                            canGoPreviousMonth = false,
                            canGoNextMonth = false,
                        )
                    }
                }
            }

        private fun calculateNavigationState(): NavigationState {
            val currentDate = LocalDate.now()
            val currentYearMonth = YearMonth(currentDate.year, currentDate.monthValue)

            // canGoPrevious는 항상 true
            // canGoNext는 현재 선택된 월이 오늘 기준 월보다 작을 때만 true
            val canGoNext =
                container.stateFlow.value.currentYearMonth.let { it.year * 12 + it.month } < currentYearMonth.let { it.year * 12 + it.month }

            return NavigationState(canGoPrevious = true, canGoNext = canGoNext)
        }

        private data class NavigationState(
            val canGoPrevious: Boolean,
            val canGoNext: Boolean,
        )
    }

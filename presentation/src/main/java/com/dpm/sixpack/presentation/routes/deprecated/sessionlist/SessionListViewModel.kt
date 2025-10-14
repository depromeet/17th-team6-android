package com.dpm.sixpack.presentation.routes.deprecated.sessionlist

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.deprecated.sessionlist.contract.SessionListIntent
import com.dpm.sixpack.presentation.routes.deprecated.sessionlist.contract.SessionListScreenState
import com.dpm.sixpack.presentation.routes.deprecated.sessionlist.contract.SessionListSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SessionListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<SessionListScreenState, SessionListIntent, SessionListSideEffect>() {
    private val goalId by lazy {
        savedStateHandle.get<Long>("goalId") ?: -1L
    }

    override val initialState: SessionListScreenState = SessionListScreenState()

    override val container: Container<SessionListScreenState, SessionListSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    init {
        initializeState()
    }

    private fun initializeState() {
    }

    override fun onIntent(intent: SessionListIntent) {
        when (intent) {
            is SessionListIntent.NavigateBackClick -> handleNavigateBackClick()
            is SessionListIntent.GoalEditClick -> handleGoalEditClick()
            is SessionListIntent.RunningSessionClick -> handleRunningSessionClick(intent)
            is SessionListIntent.StartRunningSessionClick -> handleStartRunningSessionClick(intent)
        }
    }

    private fun handleNavigateBackClick() {
        intent {
            postSideEffect(SessionListSideEffect.NavigateBack)
        }
    }

    private fun handleGoalEditClick() {
        intent {
            postSideEffect(SessionListSideEffect.NavigateToGoalEdit(goalId = goalId))
        }
    }

    private fun handleRunningSessionClick(intent: SessionListIntent.RunningSessionClick) {
        intent {
            val sessionList = state.sessionList
            val clickedSession = sessionList.firstOrNull { it.id == intent.sessionId }
            val nextSession = sessionList.firstOrNull { it.isCompleted.not() }

            when {
                // 클릭된 세션이 다음에 할 세션인 경우 바로 세션으로 이동
                nextSession?.id == clickedSession?.id -> {
                    postSideEffect(SessionListSideEffect.NavigateToSession(sessionId = intent.sessionId))
                }
                // 클릭된 세션이 다음에 할 세션이 아닌데, 완료 상태가 아니라면 에러 메시지 노출
                clickedSession?.isCompleted?.not() == true -> {
                    postSideEffect(SessionListSideEffect.ShowPreviousSessionFirstErrorMessage)
                }
                // 클릭된 세션이 완료된 세션이라면 선택/선택해제 상태 변경
                else -> {
                    // 완료된 세션이 선택된 상태라면, 다른 세션이 선택되지 않도록 함
                    val newSessionList =
                        sessionList.map {
                            if (it.id == intent.sessionId) {
                                it.copy(isSelected = true)
                            } else {
                                it.copy(isSelected = false)
                            }
                        }
                    reduce {
                        state.copy(sessionList = newSessionList)
                    }
                }
            }
        }
    }

    private fun handleStartRunningSessionClick(intent: SessionListIntent.StartRunningSessionClick) {
        intent {
            postSideEffect(SessionListSideEffect.NavigateToSession(sessionId = intent.sessionId))
        }
    }
}

package com.dpm.sixpack.presentation.routes.home

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.domain.usecase.GetHomeUseCase
import com.dpm.sixpack.domain.util.DoRunResult
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.home.contract.HomeIntent
import com.dpm.sixpack.presentation.routes.home.contract.HomeScreenState
import com.dpm.sixpack.presentation.routes.home.contract.HomeSessionComponentState
import com.dpm.sixpack.presentation.routes.home.contract.HomeSideEffect
import com.dpm.sixpack.presentation.routes.home.contract.HomeTotalGoalComponentState
import com.dpm.sixpack.presentation.routes.home.contract.asHomeSessionComponentState
import com.dpm.sixpack.presentation.routes.home.contract.asHomeTotalGoalComponentState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getHomeUseCase: GetHomeUseCase,
) : BaseViewModel<HomeScreenState, HomeIntent, HomeSideEffect>() {
    override val initialState: HomeScreenState = HomeScreenState()

    override val container: Container<HomeScreenState, HomeSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    private var cachedGoalId: Long? = null
    private var cachedNextSessionId: Long? = null
    private var cachedPreviousSessionId: Long? = null

    init {
        initializeState()
    }

    private fun initializeState() {
        intent {
            val home = getHomeUseCase()
            val data = (home as? DoRunResult.Success)?.data
            val totalGoal =
                data?.runningTotalGoal?.asHomeTotalGoalComponentState()
                    ?: HomeTotalGoalComponentState()
            val sessionGoal =
                data?.sessionGoal?.asHomeSessionComponentState(totalGoal.safeTotalSessionCount)
                    ?: HomeSessionComponentState()

            cachedGoalId = data?.runningTotalGoal?.id
            cachedNextSessionId = data?.sessionGoal?.id
            cachedPreviousSessionId = data?.sessionGoal?.previousSessionId

            reduce {
                state.copy(
                    loading = false,
                    totalGoalComponentState = totalGoal,
                    sessionComponentState = sessionGoal,
                    totalGoalCompleted = totalGoal.safeTotalSessionCount == totalGoal.safeCurrentSessionCount,
                )
            }
        }
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.GoalList -> handleGoalListIntent()
            is HomeIntent.NextSession -> handleNextSessionIntent()
            is HomeIntent.PreviousSession -> handlePreviousSessionIntent()
            is HomeIntent.GoalEdit -> handleGoalEditIntent()
        }
    }

    private fun handleGoalListIntent() {
        cachedGoalId?.let { id ->
            intent {
                postSideEffect(HomeSideEffect.NavigateToGoalList(id))
            }
        }
    }

    private fun handleNextSessionIntent() {
        cachedNextSessionId?.let { id ->
            intent {
                postSideEffect(HomeSideEffect.NavigateToSession(id))
            }
        }
    }

    private fun handlePreviousSessionIntent() {
        intent {
            cachedPreviousSessionId?.let { id ->
                postSideEffect(HomeSideEffect.NavigateToSession(id))
            }
        }
    }

    private fun handleGoalEditIntent() {
        intent {
            if (state.totalGoalCompleted) {
                postSideEffect(HomeSideEffect.NavigateToGoalEdit)
            }
        }
    }
}

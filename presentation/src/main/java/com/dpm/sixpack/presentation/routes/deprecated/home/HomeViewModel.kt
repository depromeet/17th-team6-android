package com.dpm.sixpack.presentation.routes.deprecated.home

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.deprecated.home.contract.HomeIntent
import com.dpm.sixpack.presentation.routes.deprecated.home.contract.HomeScreenState
import com.dpm.sixpack.presentation.routes.deprecated.home.contract.HomeSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
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
                postSideEffect(HomeSideEffect.NavigateToSessionList(id))
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

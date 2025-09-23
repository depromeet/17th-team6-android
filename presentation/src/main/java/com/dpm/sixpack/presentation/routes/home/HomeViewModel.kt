package com.dpm.sixpack.presentation.routes.home

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.home.contract.HomeIntent
import com.dpm.sixpack.presentation.routes.home.contract.HomeSideEffect
import com.dpm.sixpack.presentation.routes.home.contract.HomeScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<HomeScreenState, HomeIntent, HomeSideEffect>() {
    override val initialState: HomeScreenState = HomeScreenState()

    override val container: Container<HomeScreenState, HomeSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    init {
        intent {
            reduce {
                state.copy(
                    totalGoalComponentState = state.totalGoalComponentState.copy(
                        loading = false,
                        imageRes = R.drawable.ill_marathon_10km,
                        title = "이번 달 목표",
                        distance = "10km",
                        duration = "1시간 30분",
                        pace = "8'30\""
                    ),
                    sessionComponentState = state.sessionComponentState.copy(
                        loading = false,
                        sessionCount = 3,
                        cheerUpStringRes = R.string.home_goal_cheer_up_1_25,
                        distance = "5km",
                        duration = "45분",
                        pace = "9'00\""
                    )
                )
            }
        }
    }
    override fun onIntent(intent: HomeIntent) {

    }
}

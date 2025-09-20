package com.dpm.sixpack.presentation.routes.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.domain.model.RunningSessionGoal
import com.dpm.sixpack.domain.usecase.FinishRunningSessionUseCase
import com.dpm.sixpack.domain.usecase.GetRealtimeRunningDataUseCase
import com.dpm.sixpack.domain.usecase.StartRunningUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.util.calculatePace
import com.dpm.sixpack.presentation.common.util.formatSecondsToTime
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionIntent
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionSideEffect
import com.dpm.sixpack.presentation.routes.session.contract.uistate.MapUiState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RecordUiState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionState.Companion.INITIAL_COUNTDOWN
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionUiState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.toUiState
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextULong

@HiltViewModel
class RunningSessionViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val startRunningUseCase: StartRunningUseCase,
    private val getRealtimeRunningDataUseCase: GetRealtimeRunningDataUseCase,
    private val finishRunningSessionUseCase: FinishRunningSessionUseCase,
) : BaseViewModel<RunningSessionUiState, RunningSessionIntent, RunningSessionSideEffect>() {
    // TODO replace with real data
    val todayRunData = savedStateHandle.get<RunningSessionGoal>("key_name") ?: RunningSessionGoal(
        id = 0L,
        sessionNumber = 12,
        warmUpDuration = 300,
        mainRunningDistance = 15_000,
        mainRunningDuration = 3_600,
        mainRunningPace = 321,
        coolDownDuration = 300
    )

    override val initialState: RunningSessionUiState = RunningSessionUiState()

    override val container: Container<RunningSessionUiState, RunningSessionSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    init {
        initializeUiState()
    }

    override fun onIntent(intent: RunningSessionIntent) {
        when (intent) {
            is RunningSessionIntent.TabChange -> handleTabChange(intent)
            is RunningSessionIntent.SessionStart -> handleSessionStart()
            is RunningSessionIntent.ToggleFollowingMode -> handleToggleFollowingMode()
            is RunningSessionIntent.WarmUpSkip -> handleWarmUpSkip()
            is RunningSessionIntent.WarmUpSkipConfirm -> handleWarmUpSkipConfirm()
            is RunningSessionIntent.WarmUpSkipCancel -> handleWarmUpSkipCancel()
            is RunningSessionIntent.WarmUpFinish -> handleWarmUpFinish()
            is RunningSessionIntent.WarmUpContinue -> handleWarmUpContinue()
            is RunningSessionIntent.MainRunningPause -> handleMainRunningPause()
            is RunningSessionIntent.MainRunningResume -> handleMainRunningResume()
            is RunningSessionIntent.MainRunningCancelFinish -> handleMainRunningCancelFinish()
            is RunningSessionIntent.MainRunningFinish -> handleMainRunningFinish()
            is RunningSessionIntent.MainRunningConfirmFinish -> handleMainRunningConfirmFinish()
            is RunningSessionIntent.CoolDownPause -> handleCoolDownPause()
            is RunningSessionIntent.CoolDownResume -> handleCoolDownResume()
            is RunningSessionIntent.CoolDownFinish -> handleCoolDownFinish()
            is RunningSessionIntent.CoolDownCancelFinish -> handleCoolDownCancelFinish()
            is RunningSessionIntent.CoolDownConfirmFinish -> handleCoolDownConfirmFinish()
        }
    }

    private fun initializeUiState() = intent {
        reduce {
            state.copy(sessionState = RunningSessionState.Initial(todayRunData.toUiState()))
        }
    }

    private fun handleTabChange(intent: RunningSessionIntent.TabChange) = intent {
        postSideEffect(RunningSessionSideEffect.ChangeTab(intent.tab))
    }

    private fun handleSessionStart() {
        viewModelScope.launch {
            startRunningUseCase(goalPlanId = todayRunData.id)

            intent {
                var timer = 0
                repeat(INITIAL_COUNTDOWN) {
                    reduce {
                        state.copy(
                            sessionState = RunningSessionState.Main.Ready(
                                countdown = INITIAL_COUNTDOWN - timer,
                            )
                        )
                    }
                    timer++
                    delay(1000L)
                }

                reduce {
                    state.copy(
                        sessionState =
                            RunningSessionState.Main.Running(
                                mapUiState = MapUiState(),
                                recordUiState = RecordUiState(),
                            ),
                    )
                }

                getRealtimeRunningDataUseCase()
                    .catch { }
                    .collect { result ->
                        val currentState = state.sessionState
                        val realtimeData = result.getOrNull()
                        if (currentState is RunningSessionState.Main.Running && realtimeData != null) {
                            val currentPaceColors = currentState.mapUiState.paceColors.lastOrNull() ?: listOf()
                            // TODO FIXME 페이스 컬러실제 데이터로 변경
                            val newPaceColors = currentPaceColors + listOf(Random.nextULong(0uL..0xFFFFFFFFuL))
                            val currentPath = currentState.mapUiState.path.lastOrNull() ?: listOf()
                            val newPath = currentPath + LatLng(
                                realtimeData.latitude,
                                realtimeData.longitude,
                            )
                            reduce {
                                state.copy(
                                    sessionState = currentState.copy(
                                        mapUiState = currentState.mapUiState.copy(
                                            paceColors =
                                                currentState.mapUiState.paceColors
                                                    .dropLast(1)
                                                    .toMutableList()
                                                    .apply {
                                                        add(newPaceColors)
                                                    },
                                            path =
                                                currentState.mapUiState.path
                                                    .dropLast(1)
                                                    .toMutableList()
                                                    .apply {
                                                        add(newPath)
                                                    },
                                        ),
                                        recordUiState = currentState.recordUiState.copy(
                                            currentDistance = "${realtimeData.totalDistanceMeter}m",
                                            currentDuration = formatSecondsToTime(realtimeData.duration),
                                            avgPace =
                                                calculatePace(
                                                    totalTimeInSeconds = realtimeData.duration,
                                                    distanceInKm = realtimeData.totalDistanceMeter / 1000.0,
                                                ),
                                            cadence = "${realtimeData.cadence}",
                                        ),
                                    ),
                                )
                            }
                        }
                    }
            }
        }
    }

    fun handleToggleFollowingMode() {
        intent {
            reduce {
                state.copy(
                    isFollowingModeEnabled = !state.isFollowingModeEnabled,
                )
            }
        }
    }

    private fun handleWarmUpSkip() {}
    private fun handleWarmUpSkipConfirm() {}
    private fun handleWarmUpSkipCancel() {}
    private fun handleWarmUpFinish() {}
    private fun handleWarmUpContinue() {}
    private fun handleMainRunningPause() {
        intent {
            val currentState = state.sessionState
            if (currentState is RunningSessionState.Main.Running) {
                reduce {
                    state.copy(
                        sessionState =
                            RunningSessionState.Main.Pause(
                                mapUiState =
                                    currentState.mapUiState.copy(
                                        paceColors = currentState.mapUiState.paceColors + listOf(),
                                        path = currentState.mapUiState.path + listOf(),
                                    ),
                                recordUiState = currentState.recordUiState,
                                showFinishSessionConfirmUi = false,
                            ),
                    )
                }
            }
        }
    }

    private fun handleMainRunningResume() {
        intent {
            val currentState = state.sessionState
            if (currentState is RunningSessionState.Main.Pause) {
                reduce {
                    state.copy(
                        sessionState =
                            RunningSessionState.Main.Running(
                                mapUiState = currentState.mapUiState,
                                recordUiState = currentState.recordUiState,
                            ),
                    )
                }
            }
        }
    }

    private fun handleMainRunningCancelFinish() {
        intent {
            val currentState = state.sessionState
            if (currentState is RunningSessionState.Main.Pause) {
                reduce {
                    state.copy(
                        sessionState =
                            currentState.copy(
                                showFinishSessionConfirmUi = false,
                            ),
                    )
                }
            }
        }
    }

    private fun handleMainRunningFinish() {
        intent {
            val currentState = state.sessionState
            if (currentState is RunningSessionState.Main.Pause) {
                reduce {
                    state.copy(
                        sessionState =
                            currentState.copy(
                                showFinishSessionConfirmUi = true,
                            ),
                    )
                }
            }
        }
    }

    private fun handleMainRunningConfirmFinish() {

        viewModelScope.launch {
            finishRunningSessionUseCase()
            intent {
                val currentState = state.sessionState
                if (currentState is RunningSessionState.Main.Pause) {
                    var timer = 0
                    repeat(INITIAL_COUNTDOWN) {
                        reduce {
                            state.copy(
                                sessionState =
                                    RunningSessionState.WarmUp.Ready(
//                                            mapUiState = currentState.mapUiState,
                                        countdown = INITIAL_COUNTDOWN - timer,
                                    ),
                            )
                        }
                        timer++
                    }
                }
            }
        }
    }

    private fun handleCoolDownPause() {}
    private fun handleCoolDownResume() {}
    private fun handleCoolDownFinish() {}
    private fun handleCoolDownCancelFinish() {}
    private fun handleCoolDownConfirmFinish() {}
}

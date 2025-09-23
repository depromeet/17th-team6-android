package com.dpm.sixpack.presentation.routes.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.domain.model.RunningGoal
import com.dpm.sixpack.domain.usecase.FinishRunningSessionUseCase
import com.dpm.sixpack.domain.usecase.GetRealtimeRunningDataUseCase
import com.dpm.sixpack.domain.usecase.StartRunningUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionIntent
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionSideEffect
import com.dpm.sixpack.presentation.routes.session.contract.uistate.MapUiState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RecordUiState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionState.Companion.INITIAL_COUNTDOWN
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionUiState
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.nextULong

@HiltViewModel
class RunningSessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val startRunningUseCase: StartRunningUseCase,
    private val getRealtimeRunningDataUseCase: GetRealtimeRunningDataUseCase,
    private val finishRunningSessionUseCase: FinishRunningSessionUseCase,
) : BaseViewModel<RunningSessionUiState, RunningSessionIntent, RunningSessionSideEffect>() {
    // TODO replace with real data
    val todayRunData =
        savedStateHandle.get<RunningGoal>("key_name") ?: RunningGoal(
            id = 0L,
            title = "3주차 2회차 러닝",
            distance = 15_000,
            duration = 3_600,
            pace = 300,
        )

    override val initialState: RunningSessionUiState = RunningSessionUiState()

    override val container: Container<RunningSessionUiState, RunningSessionSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: RunningSessionIntent) {
        when (intent) {
            RunningSessionIntent.SessionStart -> handleStart()
            RunningSessionIntent.MainRunningPause -> handlePause()
            RunningSessionIntent.MainRunningResume -> handleResume()
            RunningSessionIntent.MainRunningCancelFinish -> handleCancelFinish()
            RunningSessionIntent.MainRunningFinish -> handleFinish()
            RunningSessionIntent.MainRunningConfirmFinish -> handleConfirmFinish()
            RunningSessionIntent.ToggleFollowingMode -> handleToggleFollowingMode()
            else -> {
                // No-Op
            }
        }
    }

    private fun handleStart() {
        viewModelScope.launch {
            startRunningUseCase(goalPlanId = todayRunData.id)

            intent {
                var timer = 0
                repeat(INITIAL_COUNTDOWN) {
                    reduce {
                        Timber.d("countdown: ${INITIAL_COUNTDOWN - timer}")
                        state.copy(
                            sessionState =
                                RunningSessionState.WarmUp.Ready(
                                    countdown = INITIAL_COUNTDOWN - timer,
                                ),
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
                            val currentPaceColors =
                                currentState.mapUiState.paceColors.lastOrNull() ?: listOf()
                            val newPaceColors =
                                currentPaceColors + listOf(Random.nextULong(0uL..0xFFFFFFFFuL))
                            val currentPath =
                                currentState.mapUiState.path.lastOrNull() ?: listOf()
                            val newPath =
                                currentPath +
                                    LatLng(
                                        realtimeData.latitude,
                                        realtimeData.longitude,
                                    )
                            reduce {
                                state.copy(
                                    sessionState =
                                        currentState.copy(
                                            mapUiState =
                                                currentState.mapUiState.copy(
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
                                            recordUiState =
                                                currentState.recordUiState.copy(
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

    private fun handlePause() {
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

    private fun handleResume() {
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

    private fun handleCancelFinish() {
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

    private fun handleFinish() {
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

    private fun handleConfirmFinish() {
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

    fun handleToggleFollowingMode() {
        intent {
            reduce {
                state.copy(
                    isFollowingModeEnabled = !state.isFollowingModeEnabled,
                )
            }
        }
    }

    private fun formatSecondsToTime(totalSeconds: Int): String {
        val seconds = abs(totalSeconds)

        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
    }

    private fun calculatePace(
        totalTimeInSeconds: Int,
        distanceInKm: Double,
    ): String {
        if (distanceInKm <= 0) return "00:00"

        val paceInSeconds = (totalTimeInSeconds / distanceInKm).toInt()
        val minutes = paceInSeconds / 60
        val seconds = paceInSeconds % 60

        return String.format("%02d'%02d", minutes, seconds)
    }
}

package com.dpm.sixpack.presentation.routes.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.model.RunningSessionGoal
import com.dpm.sixpack.domain.usecase.FinishRunningSessionUseCase
import com.dpm.sixpack.domain.usecase.GetRealtimeRunningDataUseCase
import com.dpm.sixpack.domain.usecase.StartRunningUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.util.calculatePace
import com.dpm.sixpack.presentation.common.util.formatDistanceToKm
import com.dpm.sixpack.presentation.common.util.formatSecondsToTime
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionIntent
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionSideEffect
import com.dpm.sixpack.presentation.routes.session.contract.uistate.INITIAL_RECORD_STATE
import com.dpm.sixpack.presentation.routes.session.contract.uistate.MapUiState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RecordUiState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningScreenTabItem
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionState.Companion.INITIAL_COUNTDOWN
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionUiState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.toUiState
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextULong

private typealias RunningSessionSyntax = Syntax<RunningSessionUiState, RunningSessionSideEffect>

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
        val todayRunData =
            savedStateHandle.get<RunningSessionGoal>("key_name") ?: RunningSessionGoal(
                id = 0L,
                sessionNumber = 12,
                warmUpDuration = 300,
                mainRunningDistance = 15_000,
                mainRunningDuration = 3_600,
                mainRunningPace = 321,
                coolDownDuration = 300,
            )

        override val initialState: RunningSessionUiState =
            RunningSessionUiState(
                sessionState = RunningSessionState.Initial(todayRunData.toUiState()),
            )

        override val container: Container<RunningSessionUiState, RunningSessionSideEffect> =
            container(initialState = initialState, savedStateHandle = savedStateHandle)

        private var observeJob: Job? = null

        private fun startObservingRealtimeData() {
            observeJob?.cancel()
            observeJob = intent { observeRealTimeRunningData() }
        }

        private fun pauseObservingRealtimeData() {
            observeJob?.cancel()
        }

        override fun onIntent(intent: RunningSessionIntent) {
            when (intent) {
                is RunningSessionIntent.TabChange -> handleTabChange(intent.tab)
                is RunningSessionIntent.SessionStart -> handleSessionStart()
                is RunningSessionIntent.ToggleFollowingMode -> handleToggleFollowingMode()
                is RunningSessionIntent.WarmUpSkip -> {
                    handleWarmUpPause(true)
                    pauseObservingRealtimeData()
                }

                is RunningSessionIntent.WarmUpSkipCancel -> handleWarmUpPause()
                is RunningSessionIntent.WarmUpSkipConfirm -> handleWarmUpSkipConfirm()
                is RunningSessionIntent.ResumeIntent -> {
                    when (intent) {
                        RunningSessionIntent.WarmUpResume -> handleWarmUpResume()
                        RunningSessionIntent.MainRunningResume -> handleMainRunningResume()
                        RunningSessionIntent.CoolDownResume -> handleCoolDownResume()
                    }
                    startObservingRealtimeData()
                }

                is RunningSessionIntent.PauseIntent -> {
                    when (intent) {
                        RunningSessionIntent.WarmUpPause -> handleWarmUpPause()
                        RunningSessionIntent.MainRunningPause -> handleMainRunningPause()
                        RunningSessionIntent.CoolDownPause -> handleCoolDownPause()
                    }
                    pauseObservingRealtimeData()
                }

                is RunningSessionIntent.StopCancelIntent -> handleStopDialog(false)
                is RunningSessionIntent.StopIntent -> handleStopDialog(true)

                is RunningSessionIntent.StopConfirmIntent -> {
                    pauseObservingRealtimeData()
                    when (intent) {
                        RunningSessionIntent.WarmUpStopConfirm -> handleWarmUpStopConfirm()
                        RunningSessionIntent.MainRunningStopConfirm -> handleMainRunningStopConfirm()
                        RunningSessionIntent.CoolDownStopConfirm -> handleCoolDownStopConfirm()
                    }

                    // TODO: 공동 로직있으면 처리
                }
            }
        }

        private fun handleTabChange(tab: RunningScreenTabItem) =
            intent {
                postSideEffect(RunningSessionSideEffect.ChangeTab(tab))
            }

        // region session start

        // Initial 상태에서 러닝 시작
        private fun handleSessionStart() {
            viewModelScope.launch {
                startRunningUseCase(goalPlanId = todayRunData.id)
                    // TODO: handle error
                    .onSuccess { }
                    .onError { }

                intent {
                    handleReadyState(RunningSessionState.WarmUp.Ready())

                    reduce {
                        state.copy(
                            sessionState =
                                RunningSessionState.WarmUp.Running(
                                    recordUiState = INITIAL_RECORD_STATE,
                                ),
                        )
                    }

                    startObservingRealtimeData()
                }
            }
        }

        // Ready 상태에서 보여지는 카운트 업데이트
        private suspend fun RunningSessionSyntax.handleReadyState(readyState: RunningSessionState.ReadyState) {
            repeat(INITIAL_COUNTDOWN - 1) { index ->
                val countdown = INITIAL_COUNTDOWN - (index + 1)
                reduce {
                    val newSessionState = readyState.withNewCountdown(countdown)
                    state.copy(sessionState = newSessionState)
                }
                delay(1000L)
            }
        }

        // 실시간 러닝 데이터 받아오는 함수
        private suspend fun RunningSessionSyntax.observeRealTimeRunningData() {
            getRealtimeRunningDataUseCase()
                .catch { }
                .collect { result ->
                    val currentSessionState = state.sessionState
                    val realtimeData = result.getOrNull()

                    if (currentSessionState is RunningSessionState.RunningState && realtimeData != null) {
                        val newSessionState = getNewRunningState(currentSessionState, realtimeData)

                        reduce {
                            state.copy(sessionState = newSessionState)
                        }
                    }
                }
        }

        // 러닝 진행 중 RunningState 업데이트
        private fun getNewRunningState(
            sessionState: RunningSessionState.RunningState,
            realtimeRunningData: RealtimeRunningData,
        ): RunningSessionState.RunningState {
            val newRecordState = getNewRecordUiState(realtimeRunningData)

            return when (sessionState) {
                is RunningSessionState.Main.Running -> {
                    val newMapUiState =
                        getNewMapUiState(
                            sessionState,
                            LatLng(realtimeRunningData.latitude, realtimeRunningData.longitude),
                        )
                    // Main.Running일 때는 mapUiState와 recordUiState를 모두 업데이트
                    sessionState.copy(
                        mapUiState = newMapUiState,
                        recordUiState = newRecordState,
                    )
                }

                else -> {
                    // 그 외에는 recordUiState만 업데이트
                    sessionState.withNewRecordUiState(newRecordState)
                }
            }
        }

        // 새로운 좌표가 추가된 갱신된 MapUiState 생성 -> 오직 본러닝 중에만
        private fun getNewMapUiState(
            sessionState: RunningSessionState.Main.Running,
            newPoint: LatLng,
        ): MapUiState {
            // 페이스 색상 업데이트
            val currentTotalPaceColors = sessionState.mapUiState.paceColors
            val currentPaceColors = currentTotalPaceColors.lastOrNull() ?: listOf()
            // TODO FIXME 페이스 컬러 실제 데이터로 변경
            val newPaceColors = currentPaceColors + listOf(Random.nextULong(0uL..0xFFFFFFFFuL))
            val newTotalPaceColors = currentTotalPaceColors.toMutableList()

            if (newTotalPaceColors.isNotEmpty()) {
                newTotalPaceColors[newTotalPaceColors.lastIndex] = newPaceColors
            }

            // 경로 업데이트
            val currentTotalPaths = sessionState.mapUiState.path
            val currentPath = currentTotalPaths.lastOrNull() ?: listOf()
            val newPath = currentPath + newPoint
            val newTotalPathList = currentTotalPaths.toMutableList()

            if (newTotalPathList.isNotEmpty()) {
                newTotalPathList[newTotalPathList.lastIndex] = newPath
            }

            return MapUiState(
                paceColors = newTotalPaceColors,
                path = newTotalPathList,
            )
        }

        private fun getNewRecordUiState(realtimeRunningData: RealtimeRunningData): RecordUiState {
            val newRecord =
                RecordUiState(
                    currentDistance = "${realtimeRunningData.totalDistanceMeter}m",
                    currentDuration = formatSecondsToTime(realtimeRunningData.duration),
                    avgPace =
                        calculatePace(
                            totalTimeInSeconds = realtimeRunningData.duration,
                            distanceInKm = realtimeRunningData.totalDistanceMeter / 1000.0,
                        ),
                    cadence = "${realtimeRunningData.cadence}",
                )
            return newRecord
        }

        // endregion

        fun handleToggleFollowingMode() =
            intent {
                reduce {
                    state.copy(
                        isFollowingModeEnabled = !state.isFollowingModeEnabled,
                    )
                }
            }

        // 일시정지 상태에서 종료 다이얼로그 show 상태 관리
        private fun handleStopDialog(showStopConfirmDialog: Boolean) =
            intent {
                val currentState = state.sessionState
                if (currentState is RunningSessionState.PausedState) {
                    reduce {
                        state.copy(
                            sessionState = currentState.withNewShowStopConfirmDialog(showStopConfirmDialog),
                        )
                    }
                }
            }

        // region warm up

        private fun handleWarmUpPause(showSkipConfirmDialog: Boolean = false) =
            intent {
                val currentState = state.sessionState
                if (currentState is RunningSessionState.HasRecord) {
                    reduce {
                        state.copy(
                            sessionState =
                                RunningSessionState.WarmUp.Pause(
                                    recordUiState = currentState.recordUiState,
                                    showSkipConfirmDialog = showSkipConfirmDialog,
                                ),
                        )
                    }
                }
            }

        private fun handleWarmUpSkipConfirm() =
            intent {
                handleReadyState(RunningSessionState.Main.Ready())
                reduce {
                    state.copy(
                        sessionState =
                            RunningSessionState.Main.Running(
                                goalDistance = formatDistanceToKm(todayRunData.mainRunningDistance),
                                recordUiState = INITIAL_RECORD_STATE,
                            ),
                    )
                }
                startObservingRealtimeData()
            }

    /*
    웜업 중단 -> 홈화면
    FIXME SK: 임시 조치로 Initial 상태로 돌아가게 함
     */
        private fun handleWarmUpStopConfirm() =
            intent {
                reduce {
                    state.copy(RunningSessionState.Initial())
                }
            }

        private fun handleWarmUpResume() =
            intent {
                val currentState = state.sessionState
                if (currentState is RunningSessionState.WarmUp.Pause) {
                    reduce {
                        state.copy(
                            sessionState =
                                RunningSessionState.WarmUp.Running(
                                    recordUiState = currentState.recordUiState,
                                ),
                        )
                    }
                }
            }

        // endregion

        // region Main

        private fun handleMainRunningPause() =
            intent {
                val currentState = state.sessionState
                if (currentState is RunningSessionState.Main.Running) {
                    reduce {
                        state.copy(
                            sessionState =
                                RunningSessionState.Main.Pause(
                                    goalDistance = currentState.goalDistance,
                                    mapUiState =
                                        currentState.mapUiState.copy(
                                            paceColors = currentState.mapUiState.paceColors + listOf(),
                                            path = currentState.mapUiState.path + listOf(),
                                        ),
                                    recordUiState = currentState.recordUiState,
                                ),
                        )
                    }
                }
            }

        private fun handleMainRunningResume() =
            intent {
                val currentState = state.sessionState
                if (currentState is RunningSessionState.Main.Pause) {
                    reduce {
                        state.copy(
                            sessionState =
                                RunningSessionState.Main.Running(
                                    goalDistance = currentState.goalDistance,
                                    mapUiState = currentState.mapUiState,
                                    recordUiState = currentState.recordUiState,
                                ),
                        )
                    }
                }
            }

        // 메인러닝 중단 -> 홈화면

        // FIXME SK: 임시 조치로 Initial 상태로 돌아가게 함
        private fun handleMainRunningStopConfirm() =
            intent {
                finishRunningSessionUseCase()
//                val currentState = state.sessionState
//                if (currentState is RunningSessionState.Main.Pause) {
//                    var timer = 0
//                    repeat(INITIAL_COUNTDOWN) {
//                        reduce {
//                            state.copy(
//                                sessionState =
//                                    RunningSessionState.WarmUp.Ready(
//                                        countdown = INITIAL_COUNTDOWN - timer,
//                                    ),
//                            )
//                        }
//                        timer++
//                    }
//                }
                reduce {
                    state.copy(RunningSessionState.Initial())
                }
            }

        // endregion

        private fun handleCoolDownPause() =
            intent {
                val currentState = state.sessionState
                if (currentState is RunningSessionState.CoolDown.Running) {
                    reduce {
                        state.copy(
                            sessionState =
                                RunningSessionState.CoolDown.Running(
                                    mapUiState = currentState.mapUiState,
                                    recordUiState = currentState.recordUiState,
                                ),
                        )
                    }
                }
            }

        private fun handleCoolDownResume() =
            intent {
                val currentState = state.sessionState
                if (currentState is RunningSessionState.CoolDown.Pause) {
                    reduce {
                        state.copy(
                            sessionState =
                                RunningSessionState.CoolDown.Running(
                                    mapUiState = currentState.mapUiState,
                                    recordUiState = currentState.recordUiState,
                                ),
                        )
                    }
                }
            }

        // FIXME SK: 임시 조치로 Initial 상태로 돌아가게 함
        private fun handleCoolDownStopConfirm() =
            intent {
                reduce {
                    state.copy(RunningSessionState.Initial())
                }
            }
    }

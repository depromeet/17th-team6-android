package com.dpm.sixpack.presentation.routes.session

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.model.session.RunningSessionGoal
import com.dpm.sixpack.domain.usecase.FinishRunningSessionUseCase
import com.dpm.sixpack.domain.usecase.GetRealtimeRunningDataUseCase
import com.dpm.sixpack.domain.usecase.StartRunningUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.util.MockLocationClient
import com.dpm.sixpack.presentation.common.util.Sungsoo
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
import com.dpm.sixpack.runningservice.RunningActions
import com.dpm.sixpack.runningservice.RunningService
import com.google.android.gms.location.FusedLocationProviderClient
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

private typealias RunningSessionSyntax = Syntax<RunningSessionUiState, RunningSessionSideEffect>

@HiltViewModel
class RunningSessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val startRunningUseCase: StartRunningUseCase,
    private val getRealtimeRunningDataUseCase: GetRealtimeRunningDataUseCase,
    private val finishRunningSessionUseCase: FinishRunningSessionUseCase,
    fusedLocationProviderClient: FusedLocationProviderClient,
) : BaseViewModel<RunningSessionUiState, RunningSessionIntent, RunningSessionSideEffect>() {
    // FIXME: 프리런칭 시뮬레이션용
    val mockLocationClient = MockLocationClient(fusedLocationProviderClient, viewModelScope)

    // TODO replace with real data
    val todayRunData =
        savedStateHandle.get<RunningSessionGoal>("key_name") ?: RunningSessionGoal(
            id = 0L,
            pace = 360,
            distance = 10000,
            duration = 60,
            roundCount = 5,
            previousSessionId = 3L,
            goalId = 2L,
            createdAt = "TODO()",
            updatedAt = "TODO()",
            clearedAt = null,
            totalRoundCount = 20,
        )

    override val initialState: RunningSessionUiState =
        RunningSessionUiState(
            sessionState = RunningSessionState.Initial(todayRunData.toUiState()),
        )

    override val container: Container<RunningSessionUiState, RunningSessionSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    @SuppressLint("StaticFieldLeak")
    private var runningService: RunningService? = null

    private val serviceConnection =
        object : ServiceConnection {
            override fun onServiceConnected(
                name: ComponentName?,
                service: IBinder?,
            ) {
                val binder = service as? RunningService.RunningBinder ?: return
                runningService = binder.getService()
                observeServiceState()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                runningService = null
            }
        }

    init {
        bindToService()
    }

    override fun onIntent(intent: RunningSessionIntent) {
        when (intent) {
            is RunningSessionIntent.ClickBackIcon ->
                intent {
                    postSideEffect(RunningSessionSideEffect.NavigateBackToHome)
                }

            is RunningSessionIntent.TabChange -> handleTabChange(intent.tab)
            is RunningSessionIntent.SessionStart -> handleSessionStart()
            is RunningSessionIntent.ToggleFollowingMode -> handleToggleFollowingMode()
            is RunningSessionIntent.WarmUpSkip -> {
                handleWarmUpPause(true)
                pauseObservingRealtimeData()
            }

            is RunningSessionIntent.WarmUpSkipCancel -> handleWarmUpPause()
            is RunningSessionIntent.WarmUpSkipConfirm -> {
                mockLocationClient.stop()
                sendCommandToService(context, RunningActions.STOP)
                handleWarmUpSkipConfirm()
            }

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
                mockLocationClient.stop()
                sendCommandToService(context, RunningActions.STOP)
                when (intent) {
                    RunningSessionIntent.WarmUpStopConfirm -> handleWarmUpStopConfirm()
                    RunningSessionIntent.MainRunningStopConfirm -> handleMainRunningStopConfirm()
                    RunningSessionIntent.CoolDownStopConfirm -> handleCoolDownStopConfirm()
                }
            }
        }
    }

    private fun observeServiceState() {
        runningService
            ?.runningDataState
            ?.onEach { realtimeData ->
                Timber.d("observeServiceState: $realtimeData")
                intent {
                    val currentSessionState = state.sessionState
                    if (currentSessionState is RunningSessionState.RunningState && realtimeData != null) {
                        val newSessionState = getNewRunningState(currentSessionState, realtimeData)
                        intent {
                            reduce {
                                state.copy(sessionState = newSessionState)
                            }
                        }
                    }
                }
            }?.launchIn(viewModelScope)
    }

    @SuppressLint("MissingPermission")
    private fun startObservingRealtimeData() {
        // 테스트
        if (mockLocationClient.isRunning) {
            mockLocationClient.resume()
        } else {
            mockLocationClient.startWithLatLng(Sungsoo)
        }

        sendCommandToService(context, RunningActions.START_OR_RESUME)
    }

    private fun pauseObservingRealtimeData() {
        mockLocationClient.pause()
        sendCommandToService(context, RunningActions.PAUSE)
    }

    private fun handleTabChange(tab: RunningScreenTabItem) =
        intent {
            postSideEffect(RunningSessionSideEffect.ChangeTab(tab))
        }

    // region session start

    // Initial 상태에서 러닝 시작
    private fun handleSessionStart() {
        intent {
            viewModelScope.launch {
                startRunningUseCase(goalPlanId = todayRunData.id)
                    .onSuccess { }
                    .onError { }
            }

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
                        realtimeRunningData,
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
        realtimeRunningData: RealtimeRunningData,
    ): MapUiState {
        val currentPaths = sessionState.mapUiState.path
        val currentColors = sessionState.mapUiState.paceColors

        val newColorInt = PaceColorCalculator(realtimeRunningData.pace)

        // 세션을 처음 시작하는 경우
        if (currentPaths.isEmpty()) {
            return MapUiState(
                path = listOf(listOf(newPoint)),
                paceColors = listOf(listOf(newColorInt)),
            )
        }

        // 기존 경로에 이어서 추가하는 경우
        val previousPaths = currentPaths.dropLast(1)
        val previousColors = currentColors.dropLast(1)

        val newLastPath = currentPaths.last() + newPoint
        val newLastColors = currentColors.last() + newColorInt

        return MapUiState(
            path = previousPaths + listOf(newLastPath),
            paceColors = previousColors + listOf(newLastColors),
        )
    }

    private fun getNewRecordUiState(realtimeRunningData: RealtimeRunningData): RecordUiState {
        val newRecord =
            RecordUiState(
                currentDistance = realtimeRunningData.totalDistanceMeter,
                currentDuration = realtimeRunningData.duration,
                avgPace = realtimeRunningData.pace,
                cadence = realtimeRunningData.cadence,
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
                    when (currentState) {
                        is RunningSessionState.Main.Pause -> {
                            state.copy(
                                sessionState =
                                    currentState.copy(
                                        // TODO 진짜 남은 거리 계산
                                        remainingDistanceMeter =
                                            currentState.goalDistanceMeter - currentState.recordUiState.currentDistance,
                                        showStopSessionConfirmDialog = showStopConfirmDialog,
                                    ),
                            )
                        }

                        else -> {
                            state.copy(
                                sessionState = currentState.withNewShowStopConfirmDialog(showStopConfirmDialog),
                            )
                        }
                    }
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
                            goalDistanceMeter = todayRunData.distance,
                            recordUiState = INITIAL_RECORD_STATE,
                        ),
                )
            }
            startObservingRealtimeData()
        }

    /*
     * 웜업 중단 -> 홈화면
     * FIXME SK: 임시 조치로 Initial 상태로 돌아가게 함
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
                                goalDistanceMeter = currentState.goalDistanceMeter,
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
                                goalDistanceMeter = currentState.goalDistanceMeter,
                                mapUiState = currentState.mapUiState,
                                recordUiState = currentState.recordUiState,
                            ),
                    )
                }
            }
        }

    // 메인러닝 중단 -> 홈화면

    // FIXME SK: 임시 조치로 Cooldown 으로 넘어가게함
    private fun handleMainRunningStopConfirm() =
        intent {
            finishRunningSessionUseCase()

            handleReadyState(RunningSessionState.CoolDown.Ready())
            reduce {
                val sessionState = state.sessionState

                state.copy(
                    sessionState =
                        RunningSessionState.CoolDown.Running(
                            mapUiState =
                                if (sessionState is RunningSessionState.Main.Pause) {
                                    sessionState.mapUiState
                                } else {
                                    MapUiState()
                                },
                            recordUiState = INITIAL_RECORD_STATE,
                        ),
                )
            }
            startObservingRealtimeData()
        }

    // endregion

    private fun handleCoolDownPause() =
        intent {
            val currentState = state.sessionState
            if (currentState is RunningSessionState.CoolDown.Running) {
                reduce {
                    state.copy(
                        sessionState =
                            RunningSessionState.CoolDown.Pause(
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

    // region Service
    private fun sendCommandToService(
        context: Context,
        action: String,
    ) {
        val intent =
            Intent(context, RunningService::class.java).apply {
                this.action = action
            }
        context.startService(intent)
    }

    private fun bindToService() {
        Intent(context, RunningService::class.java).also { intent ->
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    // endregion

    override fun onCleared() {
        mockLocationClient.stop()
        context.unbindService(serviceConnection)
        super.onCleared()
    }
}

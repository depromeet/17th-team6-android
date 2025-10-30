package com.dpm.sixpack.presentation.routes.running.session

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.usecase.FinishRunningSessionUseCase
import com.dpm.sixpack.domain.usecase.StartRunningUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.util.MockLocationClient
import com.dpm.sixpack.presentation.common.util.Sungsoo
import com.dpm.sixpack.presentation.routes.running.session.contract.RunningSessionIntent
import com.dpm.sixpack.presentation.routes.running.session.contract.RunningSessionSideEffect
import com.dpm.sixpack.presentation.routes.running.session.contract.RunningSessionUiState
import com.dpm.sixpack.presentation.routes.running.session.contract.RunningSessionUiState.Companion.INITIAL_COUNTDOWN
import com.dpm.sixpack.presentation.routes.running.session.contract.state.PathState
import com.dpm.sixpack.presentation.routes.running.session.contract.state.RecordState
import com.dpm.sixpack.runningservice.RunningActions
import com.dpm.sixpack.runningservice.RunningService
import com.google.android.gms.location.FusedLocationProviderClient
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
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
    private val finishRunningSessionUseCase: FinishRunningSessionUseCase,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) : BaseViewModel<RunningSessionUiState, RunningSessionIntent, RunningSessionSideEffect>() {
    // FIXME: 프리런칭 시뮬레이션용
    val mockLocationClient = MockLocationClient(fusedLocationProviderClient, viewModelScope)

    override val initialState: RunningSessionUiState = RunningSessionUiState.Initial

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

    @SuppressLint("MissingPermission")
    override fun onIntent(intent: RunningSessionIntent) {
        when (intent) {
            is RunningSessionIntent.SessionStart -> handleSessionStart()

            is RunningSessionIntent.RunningResume -> {
                handleMainRunningResume()
                startObservingRealtimeData()
            }

            is RunningSessionIntent.RunningPause -> {
                handleMainRunningPause()
                pauseObservingRealtimeData()
            }

            is RunningSessionIntent.RunningStopConfirm -> handleMainRunningStopConfirm()

            RunningSessionIntent.RunningStop -> handleStopDialog(true)
            RunningSessionIntent.RunningStopCancel -> handleStopDialog(false)
        }
    }

    // 러닝 중 1초마다 수집되는 러닝기록 데이터 처리
    private fun observeServiceState() {
        val runningDate = runningService?.runningDataState
        runningDate
            ?.onEach { realtimeData ->
                Timber.d("observeServiceState: $realtimeData")
                intent {
                    val currentState = state
                    if (currentState is RunningSessionUiState.Running && realtimeData != null) {
                        val newPathState =
                            getNewPathState(
                                currentState,
                                LatLng(realtimeData.latitude, realtimeData.longitude),
                                realtimeData.avgPace,
                            )
                        val newRecordState = getNewRecordUiState(realtimeData)

                        reduce {
                            currentState.copy(
                                pathState = newPathState,
                                recordState = newRecordState,
                            )
                        }

                        postSideEffect(
                            RunningSessionSideEffect.UpdateRunningPath(newPathState),
                        )
                    }
                }
            }?.launchIn(viewModelScope)
    }

    @SuppressLint("MissingPermission")
    private fun startObservingRealtimeData() {
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

    private fun handleSessionStart() {
        bindToService()
        intent {
            viewModelScope.launch {
                startRunningUseCase()
                    .onSuccess { sessionId ->
                        Timber.d("session start success: sessionId = $sessionId")
                    }.onError {
                        Timber.d("session start failed: ${it.message}")
                    }

                handleReadyState(RunningSessionUiState.Ready())

                reduce {
                    RunningSessionUiState.Running()
                }

                startObservingRealtimeData()
            }
        }
    }

    private suspend fun RunningSessionSyntax.handleReadyState(readyState: RunningSessionUiState.Ready) {
        repeat(INITIAL_COUNTDOWN - 1) { index ->
            val countdown = INITIAL_COUNTDOWN - (index + 1)
            reduce {
                readyState.withNewCountdown(countdown)
            }
            delay(1000L)
        }
    }

    private fun getNewPathState(
        sessionState: RunningSessionUiState.Running,
        newPoint: LatLng,
        newAvgPace: Int,
    ): PathState {
        val currentPathState = sessionState.pathState
        val newPathState = currentPathState.addPoint(newPoint, newAvgPace)

        return newPathState
    }

    private fun getNewRecordUiState(realtimeRunningData: RealtimeRunningData): RecordState {
        val newRecord =
            RecordState(
                currentDistance = realtimeRunningData.distanceInMeter,
                currentDuration = realtimeRunningData.durationInSec,
                avgPace = realtimeRunningData.avgPace,
                cadence = realtimeRunningData.avgCadence,
            )
        return newRecord
    }

    private fun handleStopDialog(showStopConfirmDialog: Boolean) =
        intent {
            val currentState = state
            if (currentState is RunningSessionUiState.Pause) {
                reduce {
                    currentState.copy(
                        showStopSessionConfirmDialog = showStopConfirmDialog,
                    )
                }
            }
        }

    private fun handleMainRunningPause() =
        intent {
            val currentState = state
            if (currentState is RunningSessionUiState.Running) {
                reduce {
                    RunningSessionUiState.Pause(
                        pathState =
                            currentState.pathState.copy(
                                paces = currentState.pathState.paces + listOf(),
                                paths = currentState.pathState.paths + listOf(),
                            ),
                        recordState = currentState.recordState,
                    )
                }
            }
        }

    private fun handleMainRunningResume() =
        intent {
            val currentState = state
            if (currentState is RunningSessionUiState.Pause) {
                reduce {
                    RunningSessionUiState.Running(
                        pathState = currentState.pathState,
                        recordState = currentState.recordState,
                    )
                }
            }
        }

    private fun handleMainRunningStopConfirm() =
        intent {
            if (state is RunningSessionUiState.Pause) {
                // TODO: Add capture image logic to confirm stop session
                finishRunningSessionUseCase(mapImageUrl = "")
                    .onSuccess {
                        Timber.d("session finish success")
                    }.onError {
                        Timber.d("session finish failed: ${it.message}")
                    }

                // TOOD SK: 종료 실패 시 다이얼로그 등
                reduce {
                    RunningSessionUiState.Initial
                }

                postSideEffect(RunningSessionSideEffect.SessionFinish)

                mockLocationClient.stop()
                sendCommandToService(context, RunningActions.STOP)
            }
        }

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

    override fun onCleared() {
        mockLocationClient.stop()
        context.unbindService(serviceConnection)
        super.onCleared()
    }
}

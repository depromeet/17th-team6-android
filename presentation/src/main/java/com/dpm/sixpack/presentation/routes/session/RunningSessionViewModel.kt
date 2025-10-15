package com.dpm.sixpack.presentation.routes.session

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.annotation.RequiresPermission
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.usecase.FinishRunningSessionUseCase
import com.dpm.sixpack.domain.usecase.StartRunningUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.util.MockLocationClient
import com.dpm.sixpack.presentation.common.util.Sungsoo
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionIntent
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionSideEffect
import com.dpm.sixpack.presentation.routes.session.contract.uistate.INITIAL_RECORD_STATE
import com.dpm.sixpack.presentation.routes.session.contract.uistate.MapUiState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RecordUiState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionState.Companion.INITIAL_COUNTDOWN
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionUiState
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

    override val initialState: RunningSessionUiState =
        RunningSessionUiState(
            sessionState = RunningSessionState.Initial,
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

    @SuppressLint("MissingPermission")
    override fun onIntent(intent: RunningSessionIntent) {
        when (intent) {
            // TODO SK: 권한설정 바뀌면 처리
            is RunningSessionIntent.UpdatePermission -> handlePermissionUpdate(intent.isGranted)
            is RunningSessionIntent.ClickBackIcon -> handleBackIconClick()
            is RunningSessionIntent.SessionStart -> handleSessionStart()
            is RunningSessionIntent.ToggleFollowingMode -> handleToggleFollowingMode()

            is RunningSessionIntent.RunningResume -> {
                handleMainRunningResume()
                startObservingRealtimeData()
            }

            is RunningSessionIntent.RunningPause -> {
                handleMainRunningPause()
                pauseObservingRealtimeData()
            }

            is RunningSessionIntent.RunningStopConfirm -> {
                mockLocationClient.stop()
                sendCommandToService(context, RunningActions.STOP)
                handleMainRunningStopConfirm()
            }

            RunningSessionIntent.RunningStop -> handleStopDialog(true)
            RunningSessionIntent.RunningStopCancel -> handleStopDialog(false)
        }
    }

    private fun observeServiceState() {
        runningService
            ?.runningDataState
            ?.onEach { realtimeData ->
                Timber.d("observeServiceState: $realtimeData")
                intent {
                    val currentSessionState = state.sessionState
                    if (currentSessionState is RunningSessionState.Running && realtimeData != null) {
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

    private fun handleBackIconClick() =
        intent {
            postSideEffect(RunningSessionSideEffect.NavigateBackToHome)
        }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun handlePermissionUpdate(isGranted: Boolean) {
        if (isGranted) {
            loadLocationFromClient(
                onSuccess = {
                    intent {
                        postSideEffect(RunningSessionSideEffect.SetLocation(it))
                    }
                },
            )
        } else {
            intent {
                postSideEffect(RunningSessionSideEffect.SetLocation(MapConstants.DEFAULT_CAMERA_POSITION.target))
            }
        }
    }

    // region session start

    // Initial 상태에서 러닝 시작
    private fun handleSessionStart() {
        intent {
            viewModelScope.launch {
                startRunningUseCase(goalPlanId = 123214214L) // TODO: 세션아이디
                    .onSuccess { }
                    .onError { }
            }

            handleReadyState(RunningSessionState.Ready())

            reduce {
                state.copy(
                    sessionState =
                        RunningSessionState.Running(
                            recordUiState = INITIAL_RECORD_STATE,
                        ),
                )
            }

            startObservingRealtimeData()
        }
    }

    // Ready 상태에서 보여지는 카운트 업데이트
    private suspend fun RunningSessionSyntax.handleReadyState(readyState: RunningSessionState.Ready) {
        repeat(INITIAL_COUNTDOWN - 1) { index ->
            val countdown = INITIAL_COUNTDOWN - (index + 1)
            reduce {
                val newSessionState = readyState.withNewCountdown(countdown)
                state.copy(sessionState = newSessionState)
            }
            delay(1000L)
        }
    }

    // 러닝 진행 중 RunningState 업데이트
    private fun getNewRunningState(
        sessionState: RunningSessionState.Running,
        realtimeRunningData: RealtimeRunningData,
    ): RunningSessionState.Running {
        val newRecordState = getNewRecordUiState(realtimeRunningData)

        val newMapUiState =
            getNewMapUiState(
                sessionState,
                LatLng(realtimeRunningData.latitude, realtimeRunningData.longitude),
                realtimeRunningData,
            )
        // Main.Running일 때는 mapUiState와 recordUiState를 모두 업데이트
        return sessionState.copy(
            mapUiState = newMapUiState,
            recordUiState = newRecordState,
        )
    }

    // 새로운 좌표가 추가된 갱신된 MapUiState 생성 -> 오직 본러닝 중에만
    private fun getNewMapUiState(
        sessionState: RunningSessionState.Running,
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

    @SuppressLint("MissingPermission")
    fun handleToggleFollowingMode() =
        intent {
            val currentFollowMode = state.isFollowingModeEnabled
            if (!currentFollowMode) {
                loadLocationFromClient(
                    onSuccess = { latLng ->
                        viewModelScope.launch {
                            postSideEffect(RunningSessionSideEffect.SetLocation(latLng))
                        }
                    },
                )
            }
            reduce {
                state.copy(
                    isFollowingModeEnabled = !currentFollowMode,
                )
            }
        }

    // 일시정지 상태에서 종료 다이얼로그 show 상태 관리
    private fun handleStopDialog(showStopConfirmDialog: Boolean) =
        intent {
            val currentState = state.sessionState
            if (currentState is RunningSessionState.Pause) {
                reduce {
                    state.copy(
                        sessionState =
                            currentState.copy(
                                showStopSessionConfirmDialog = showStopConfirmDialog,
                            ),
                    )
                }
            }
        }

    private fun handleMainRunningPause() =
        intent {
            val currentState = state.sessionState
            if (currentState is RunningSessionState.Running) {
                reduce {
                    state.copy(
                        sessionState =
                            RunningSessionState.Pause(
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
            if (currentState is RunningSessionState.Pause) {
                reduce {
                    state.copy(
                        sessionState =
                            RunningSessionState.Running(
                                mapUiState = currentState.mapUiState,
                                recordUiState = currentState.recordUiState,
                            ),
                    )
                }
            }
        }

    // 메인러닝 중단 -> 결과화면
    private fun handleMainRunningStopConfirm() =
        intent {
            finishRunningSessionUseCase()

            // FIXME: 임시로 Initial
            reduce {
                state.copy(
                    sessionState = RunningSessionState.Initial,
                )
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

    // TODO SK: 이 함수 호출부 모두 Client 직접 사용하는 방식 말고 유스케이스 거치는 방식으로 변경하기
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun loadLocationFromClient(onSuccess: (LatLng) -> Unit) {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    onSuccess(userLatLng)
                } else {
                    Timber.e("Last Location is Null")
                }
            }.addOnFailureListener {
                Timber.e("Load Location From Client failed: $it")
            }
    }
}

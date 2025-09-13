package com.dpm.sixpack.presentation.routes.session

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionIntent
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionSideEffect
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionState
import com.dpm.sixpack.presentation.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class RunningSessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<RunningSessionState, RunningSessionIntent, RunningSessionSideEffect>() {
    override val initialState: RunningSessionState = RunningSessionState.Initial

    override val container: Container<RunningSessionState, RunningSessionSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: RunningSessionIntent) {
        when (intent) {
            RunningSessionIntent.Start ->
                intent {
                }

            RunningSessionIntent.Pause ->
                intent {
                }

            RunningSessionIntent.Resume ->
                intent {
                }

            RunningSessionIntent.CancelFinish ->
                intent {
                }

            RunningSessionIntent.Finish ->
                intent {
                }

            RunningSessionIntent.ConfirmFinish ->
                intent {
                }
        }
    }
}

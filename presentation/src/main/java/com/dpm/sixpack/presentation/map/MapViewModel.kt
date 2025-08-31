package com.dpm.sixpack.presentation.map

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.base.BaseViewModel
import com.dpm.sixpack.presentation.example.contract.ExampleIntent
import com.dpm.sixpack.presentation.example.contract.ExampleSideEffect
import com.dpm.sixpack.presentation.example.contract.ExampleState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<ExampleState, ExampleIntent, ExampleSideEffect>() {

    override val initialState: ExampleState = ExampleState()
    override val container: Container<ExampleState, ExampleSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onEvent(intent: ExampleIntent): Job {
        TODO("Not yet implemented")
    }
}

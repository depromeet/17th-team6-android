package com.dpm.sixpack.presentation.example

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.domain.example.usecase.ChangeCountUseCase
import com.dpm.sixpack.domain.example.usecase.FetchCountUseCase
import com.dpm.sixpack.presentation.base.BaseViewModel
import com.dpm.sixpack.presentation.example.contract.ExampleIntent
import com.dpm.sixpack.presentation.example.contract.ExampleSideEffect
import com.dpm.sixpack.presentation.example.contract.ExampleState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class ExampleViewModel
    @Inject
    constructor(
        private val fetchCountUseCase: FetchCountUseCase,
        private val changeCountUseCase: ChangeCountUseCase,
        savedStateHandle: SavedStateHandle,
    ) : BaseViewModel<ExampleState, ExampleIntent, ExampleSideEffect>(savedStateHandle) {
        override val initialState: ExampleState = ExampleState()

        init {
            observeCount()
        }

        override fun onIntent(intent: ExampleIntent) {
            when (intent) {
                is ExampleIntent.Increment ->
                    intent {
                        changeCount(state, intent.count)
                        postSideEffect(ExampleSideEffect.ShowToast("Increment value"))
                    }

                is ExampleIntent.Decrement ->
                    intent {
                        changeCount(state, intent.count * -1)
                        postSideEffect(ExampleSideEffect.ShowToast("Decrement value"))
                    }

                is ExampleIntent.ClickNext ->
                    intent {
                        postSideEffect(ExampleSideEffect.NavigateNext)
                    }

                is ExampleIntent.ClickBack ->
                    intent {
                        postSideEffect(ExampleSideEffect.NavigateBack)
                    }
            }
        }

        private fun observeCount() =
            intent {
                fetchCountUseCase().collect { newCount ->
                    reduce {
                        state.copy(count = newCount)
                    }
                }
            }

        private fun changeCount(
            state: ExampleState,
            amount: Int,
        ) = intent {
            changeCountUseCase(amount)
        }

        override suspend fun Syntax<ExampleState, ExampleSideEffect>.handleEvent(intent: ExampleIntent) {
            when (intent) {
                is ExampleIntent.Increment -> {
                    changeCount(state, intent.count)
                    postSideEffect(ExampleSideEffect.ShowToast("Increment value"))
                }

                is ExampleIntent.Decrement -> {
                    changeCount(state, intent.count * -1)
                    postSideEffect(ExampleSideEffect.ShowToast("Decrement value"))
                }

                is ExampleIntent.ClickNext -> {
                    postSideEffect(ExampleSideEffect.NavigateNext)
                    postSideEffect(ExampleSideEffect.ShowToast("Navigate to Next Page"))
                }

                is ExampleIntent.ClickBack -> {
                    postSideEffect(ExampleSideEffect.NavigateBack)
                    postSideEffect(ExampleSideEffect.ShowToast("Navigate to Back Page"))
                }
            }
        }

        private suspend fun Syntax<ExampleState, ExampleSideEffect>.changeCount(
            state: ExampleState,
            amount: Int,
        ) {
            changeCountUseCase(amount)
        }
    }

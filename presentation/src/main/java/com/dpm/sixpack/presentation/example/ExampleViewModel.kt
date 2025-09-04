package com.dpm.sixpack.presentation.example

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.domain.example.usecase.ChangeCountUseCase
import com.dpm.sixpack.domain.example.usecase.FetchCountUseCase
import com.dpm.sixpack.presentation.base.BaseViewModel
import com.dpm.sixpack.presentation.example.contract.ExampleIntent
import com.dpm.sixpack.presentation.example.contract.ExampleSideEffect
import com.dpm.sixpack.presentation.example.contract.ExampleState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ExampleViewModel
    @Inject
    constructor(
        private val fetchCountUseCase: FetchCountUseCase,
        private val changeCountUseCase: ChangeCountUseCase,
        savedStateHandle: SavedStateHandle,
    ) : BaseViewModel<ExampleState, ExampleIntent, ExampleSideEffect>() {
        override val initialState: ExampleState = ExampleState()
        override val container: Container<ExampleState, ExampleSideEffect> =
            container(initialState = initialState, savedStateHandle = savedStateHandle)

        init {
            observeCount()
        }

        override fun onIntent(intent: ExampleIntent) {
            intent {
                when (intent) {
                    is ExampleIntent.Increment -> {
                        changeCount(intent.count)
                    }

                    is ExampleIntent.Decrement -> {
                        changeCount(intent.count * -1)
                    }

                    is ExampleIntent.ClickNext -> {
                        postSideEffect(ExampleSideEffect.NavigateNext)
                        postSideEffect(ExampleSideEffect.ShowToast("Next"))
                    }

                    is ExampleIntent.ClickBack -> {
                        postSideEffect(ExampleSideEffect.NavigateBack)
                        postSideEffect(ExampleSideEffect.ShowToast("Back"))
                    }
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

        private suspend fun changeCount(amount: Int) = changeCountUseCase(amount)
    }

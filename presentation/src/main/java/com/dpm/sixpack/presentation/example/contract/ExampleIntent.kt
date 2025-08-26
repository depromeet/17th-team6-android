package com.dpm.sixpack.presentation.example.contract

import com.dpm.sixpack.presentation.base.UiIntent

sealed interface ExampleIntent : UiIntent {
    data class Increment(val count: Int) : ExampleIntent
    data class Decrement(val count: Int) : ExampleIntent
    data object clickNext : ExampleIntent
    data object clickBack : ExampleIntent
}

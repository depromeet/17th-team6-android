package com.dpm.sixpack.presentation.destinations

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Main : Route
}

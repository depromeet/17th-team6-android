package com.dpm.sixpack.presentation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    // FIXME: Remove this when Map Screen is deleted
    @Serializable
    data object Map : Route

    @Serializable
    data object Main : Route
}

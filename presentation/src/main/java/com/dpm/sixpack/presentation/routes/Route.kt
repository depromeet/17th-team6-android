package com.dpm.sixpack.presentation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Example : Route

    @Serializable
    data object Map : Route
}

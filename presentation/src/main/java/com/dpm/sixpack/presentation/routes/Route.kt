package com.dpm.sixpack.presentation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Map : Route

    @Serializable
    data object Running : Route
}

@Serializable
sealed interface MapRoute {
    @Serializable
    data object Record : MapRoute

    @Serializable
    data object Setting : MapRoute
}

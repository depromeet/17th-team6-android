package com.dpm.sixpack.presentation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface MainRoute : Route {
    @Serializable
    data object Home : MainRoute

    @Serializable
    data object Session : MainRoute

    @Serializable
    data object Record : MainRoute

    @Serializable
    data object MyPage : MainRoute
}

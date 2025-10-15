package com.dpm.sixpack.presentation.destinations

import kotlinx.serialization.Serializable

@Serializable
sealed interface MainRoute : Route {
    @Serializable
    data object Home : MainRoute // TODO: 삭제, 홈화면은 Running임

    @Serializable
    data object Running : MainRoute

    @Serializable
    data object Feed : MainRoute

    @Serializable
    data object MyPage : MainRoute
}

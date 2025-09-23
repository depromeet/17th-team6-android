package com.dpm.sixpack.presentation.destinations

import kotlinx.serialization.Serializable

@Serializable
sealed interface RunningRoute : Route {
    // 홈 화면
    @Serializable
    data object Home : RunningRoute

    // 러닝 세션 화면
    @Serializable
    data object Session : RunningRoute

    // 러닝 종료 후 요약 화면
    @Serializable
    data object Summarization : RunningRoute
}

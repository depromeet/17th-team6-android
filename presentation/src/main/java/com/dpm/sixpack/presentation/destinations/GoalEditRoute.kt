package com.dpm.sixpack.presentation.destinations

import kotlinx.serialization.Serializable

@Serializable
sealed interface GoalEditRoute : Route {
    // 질문 화면
    @Serializable
    data object Question : GoalEditRoute

    // 추천 결과 화면
    @Serializable
    data object Result : GoalEditRoute
}

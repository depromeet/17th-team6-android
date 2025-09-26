package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.model.Goal
import com.dpm.sixpack.domain.model.RecommendedGoal
import javax.inject.Inject

class GetRecommendedGoalsUseCase @Inject constructor() {
    operator fun invoke(goalType: String): List<RecommendedGoal> =
        when (goalType) {
            "MARATHON" -> createMarathonRecommendations()
            "STAMINA" -> createStaminaRecommendations()
            "ZONE_2" -> createEnduranceRecommendations()
            else -> emptyList()
        }

    private fun createEnduranceRecommendations(): List<RecommendedGoal> =
        listOf(
            RecommendedGoal(
                title = "Zone2 러닝",
                subTitle = "지구력을 키워주는 저강도 유산소 러닝",
                type = "ZONE_2",
                goal = Goal(pace = 480, distance = 15000, duration = 7200, roundCount = 32),
            ),
        )

    private fun createStaminaRecommendations(): List<RecommendedGoal> =
        listOf(
            RecommendedGoal(
                title = "30분 달리기",
                subTitle = "러닝의 첫 걸음, 30분 달리기",
                type = "STAMINA",
                goal = Goal(pace = 420, distance = 4285, duration = 1800, roundCount = 32),
            ),
        )

    private fun createMarathonRecommendations(): List<RecommendedGoal> =
        listOf(
            RecommendedGoal(
                title = "10km 마라톤 완주",
                subTitle = "초보 러너도 안정적으로 완주할 수 있어요!",
                type = "MARATHON",
                goal = Goal(pace = 420, distance = 10000, duration = 3600, roundCount = 32),
            ),
            RecommendedGoal(
                title = "21km 마라톤 완주",
                subTitle = "한계를 넘어서는 도전, 함께 성장해봐요!",
                type = "MARATHON",
                goal = Goal(pace = 420, distance = 21000, duration = 8400, roundCount = 32),
            ),
            RecommendedGoal(
                title = "42km 마라톤 완주",
                subTitle = "러너라면 한 번쯤 꿈꾸는 목표에 도전해보세요!",
                type = "MARATHON",
                goal = Goal(pace = 420, distance = 42195, duration = 16800, roundCount = 32),
            ),
        )
}

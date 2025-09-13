package com.dpm.sixpack.domain.model

/**
 * 오늘의 러닝 목표 데이터 Entity
 */
data class RunningGoal(
    val id: Long, // 플랜 ID
    val title: String, // 목표 제목 == 현재 플랜 (N주차 M회차)
    val distance: Int, // 목표 거리
    val duration: Int, // 목표 시간
    val pace: Int, // 목표 페이스
)

package com.dpm.sixpack.domain.model

@Deprecated("Use RunningTotalGoal instead", ReplaceWith("RunningTotalGoal"))
data class RunningSessionGoal(
    val id: Long, // 플랜 ID
    val sessionNumber: Int, // 세션 번호 N회차
    val warmUpDuration: Int, // 준비운동 시간
    val mainRunningDuration: Int, // 목표 시간
    val mainRunningDistance: Int, // 목표 거리
    val mainRunningPace: Int, // 목표 페이스
    val coolDownDuration: Int, // 쿨다운 시간
)

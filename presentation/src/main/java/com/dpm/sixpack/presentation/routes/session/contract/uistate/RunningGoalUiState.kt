package com.dpm.sixpack.presentation.routes.session.contract.uistate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RunningGoalUiState(
    val sessionNumber: Int? = null,
    val title: String = "", // 목표 제목 == 현재 플랜 (N주차 M회차)
    val distance: String = "", // 목표 거리
    val duration: String = "", // 목표 시간
    val pace: String = "", // 목표 페이스
    val warmUpDuration: String = "", // 준비운동 시간
    val coolDownDuration: String = "", // 정리운동 시간
) : Parcelable

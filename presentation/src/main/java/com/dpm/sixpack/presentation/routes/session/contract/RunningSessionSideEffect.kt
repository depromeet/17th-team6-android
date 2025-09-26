package com.dpm.sixpack.presentation.routes.session.contract

import com.dpm.sixpack.presentation.common.base.SideEffect
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningScreenTabItem
import com.naver.maps.geometry.LatLng

sealed interface RunningSessionSideEffect : SideEffect {
    data class ChangeTab(
        val tab: RunningScreenTabItem,
    ) : RunningSessionSideEffect

    // 리포트가 유효하지 않을 때 종료하면 홈화면으로 돌아간다.
    data object NavigateBackToHome : RunningSessionSideEffect

    // 리포트가 유효할 때 종료하면 화면으로 이동.
    data class NavigateToReport(
        val sessionId: Long,
    ) : RunningSessionSideEffect

    data class SetLocation(
        val latLng: LatLng,
    ) : RunningSessionSideEffect
}

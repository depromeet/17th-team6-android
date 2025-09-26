package com.dpm.sixpack.presentation.routes.session.contract

import com.dpm.sixpack.presentation.common.base.UiIntent
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningScreenTabItem

sealed interface RunningSessionIntent : UiIntent {
    // 일시정지 이벤트
    sealed interface PauseIntent : RunningSessionIntent

    // 일시정지 상태에서 재개 이벤트
    sealed interface ResumeIntent : RunningSessionIntent

    // 일시정지 상태에서 종료하기 누른 이벤트
    sealed interface StopIntent : RunningSessionIntent

    // 종료 다이얼로그에서 취소 이벤트
    // StopCancelIntent 에 대한 뷰모델의 처리가 하나의 제너럴한 함수에서 이루어지기 때문에 각 object 삭제해도 무방함
    sealed interface StopCancelIntent : RunningSessionIntent

    // 종료 다이얼로그에서 완전 종료 이벤트
    sealed interface StopConfirmIntent : RunningSessionIntent

    //region Common

    data class TabChange(
        val tab: RunningScreenTabItem,
    ) : RunningSessionIntent

    data object SessionStart : RunningSessionIntent

    data object ToggleFollowingMode : RunningSessionIntent

    //endregion

    //region WarmUp

    data object WarmUpPause : PauseIntent

    data object WarmUpResume : ResumeIntent

    data object WarmUpSkip : RunningSessionIntent

    data object WarmUpSkipCancel : RunningSessionIntent

    data object WarmUpSkipConfirm : RunningSessionIntent

    data object WarmUpStop : StopIntent

    data object WarmUpStopCancel : StopCancelIntent

    data object WarmUpStopConfirm : StopConfirmIntent

    //endregion

    //region MainRunning

    data object MainRunningPause : PauseIntent

    data object MainRunningResume : ResumeIntent

    data object MainRunningStop : StopIntent

    data object MainRunningStopCancel : StopCancelIntent

    data object MainRunningStopConfirm : StopConfirmIntent

    //endregion

    //region CoolDown

    data object CoolDownPause : PauseIntent

    data object CoolDownResume : ResumeIntent

    data object CoolDownStop : StopIntent

    data object CoolDownStopCancel : StopCancelIntent

    data object CoolDownStopConfirm : StopConfirmIntent

    //endregion

    data object ClickBackIcon : RunningSessionIntent

    data class UpdatePermission(
        val isGranted: Boolean,
    ) : RunningSessionIntent
}

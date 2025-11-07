package com.dpm.sixpack.presentation.routes.mypage.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface MyPageIntent : UiIntent {
    data class OnTabClick(
        val tab: MyPageTab,
    ) : MyPageIntent

    data object OnSettingClick : MyPageIntent
}

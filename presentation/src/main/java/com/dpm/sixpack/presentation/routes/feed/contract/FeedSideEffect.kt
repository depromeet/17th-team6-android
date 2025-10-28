package com.dpm.sixpack.presentation.routes.feed.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface FeedSideEffect : SideEffect {
    data class ShowMenuBalloon(val feedId: Int) : FeedSideEffect
    data class ShowToast(val message: String) : FeedSideEffect
}

package com.dpm.sixpack.presentation.routes.feed.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface FeedIntent : UiIntent {
    data object OnRefresh : FeedIntent
}

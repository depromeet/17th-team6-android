package com.dpm.sixpack.presentation.routes.feed.contract.uistate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface FeedDateUiState : Parcelable {
    @Parcelize
    data object NoPostsAndExpired : FeedDateUiState

    @Parcelize
    data object NoPostsAndCertifiable : FeedDateUiState

    @Parcelize
    data object PostsAvailable : FeedDateUiState
}

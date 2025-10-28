package com.dpm.sixpack.presentation.routes.feed.contract.uistate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedBottomSheetState(
    val emojiSelection: Boolean = false,
    val reactionUsers: Boolean = false
) : Parcelable

package com.dpm.sixpack.presentation.routes.feed.contract.uistate

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.components.post.PostDropDownActionType
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedDialogState(
    val actionType: PostDropDownActionType? = PostDropDownActionType.IDLE,
    val deleteFeedId: Long? = null,
    val reportFeedId: Long? = null,
) : Parcelable

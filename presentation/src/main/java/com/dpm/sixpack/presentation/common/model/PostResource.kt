package com.dpm.sixpack.presentation.common.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.dpm.sixpack.domain.repository.FeedListItem
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class PostResource(
    val feedId: Long,
    val user: PostingUserInfo,
    val postImageUrl: String,
    val runningInfo: RunningSummary,
    val reactions: List<PostReaction>,
) : Parcelable

@Parcelize
@Immutable
data class PostingUserInfo(
    val user: UserInfo,
    val postingTime: String = "",
) : Parcelable

fun FeedListItem.PostItem.toPostResource(): PostResource {
    return PostResource(
        feedId = feed.feedId,
        user = PostingUserInfo(
            user = feed.user.toUserInfo(),
            postingTime = feed.selfieTime
        ),
        postImageUrl = feed.imageUrl,
        runningInfo = feed.runningSessionResult.toRunningSummary(feed.selfieTime),
        reactions = feed.reactions.map { it.toPostReaction() }
    )
}



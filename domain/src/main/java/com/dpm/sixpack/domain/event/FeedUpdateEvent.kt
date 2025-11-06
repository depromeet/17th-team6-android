package com.dpm.sixpack.domain.event

/**
 * Feed 데이터 변경 이벤트
 * Repository에서 발생하여 UI Layer에서 구독
 */
sealed interface FeedUpdateEvent {
    val timestamp: Long

    /**
     * 피드가 수정되었을 때
     * FeedScreen에서 Paging refresh 수행
     */
    data class Updated(
        val feedId: Long,
        override val timestamp: Long = System.currentTimeMillis(),
    ) : FeedUpdateEvent

    /**
     * 새로운 피드가 업로드되었을 때
     * FeedScreen에서 Paging invalidate 수행
     */
    data class Uploaded(
        val feedId: Long,
        val date: String,
        override val timestamp: Long = System.currentTimeMillis(),
    ) : FeedUpdateEvent
}

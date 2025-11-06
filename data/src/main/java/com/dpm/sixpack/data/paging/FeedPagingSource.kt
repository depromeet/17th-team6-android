package com.dpm.sixpack.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dpm.sixpack.data.source.remote.datasoruce.FeedDataSource
import com.dpm.sixpack.domain.repository.FeedListItem
import com.dpm.sixpack.domain.repository.FeedType
import java.io.IOException

private const val FEED_STARTING_PAGE_INDEX = 0

class FeedPagingSource(
    private val feedDataSource: FeedDataSource,
    private val type: FeedType,
    private val currentDate: String?,
    private val userId: Long?,
) : PagingSource<Int, FeedListItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedListItem> {
        val pageNum = params.key ?: FEED_STARTING_PAGE_INDEX

        return try {
            val response =
                feedDataSource.getFeeds(
                    currentDate = currentDate,
                    userId = userId,
                    page = pageNum,
                    size = params.loadSize,
                )

            val feedPage =
                response.data?.toDomain()
                    ?: return LoadResult.Error(Exception("데이터 변환에 실패했습니다"))

            val feedContent = feedPage.contents
            val meta = feedPage.meta

            val postItems = feedContent.feeds.map { FeedListItem.PostItem(it) }
            val finalItemList = mutableListOf<FeedListItem>()

            if (pageNum == 0 && type == FeedType.USER_PAGE_FEED && feedContent.userSummary != null) {
                finalItemList.add(FeedListItem.UserSummaryItem(feedContent.userSummary!!))
            }

            finalItemList.addAll(postItems)

            val nextKey = if (meta.hasNext) pageNum + 1 else null
            val prevKey = if (pageNum == 0) null else pageNum - 1

            LoadResult.Page(
                data = finalItemList,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, FeedListItem>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
}

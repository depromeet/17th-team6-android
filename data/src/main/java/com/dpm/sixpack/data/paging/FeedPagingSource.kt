package com.dpm.sixpack.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dpm.sixpack.data.source.remote.datasoruce.FeedDataSource
import com.dpm.sixpack.domain.model.FeedContent
import java.io.IOException

private const val FEED_STARTING_PAGE_INDEX = 0

class FeedPagingSource(
    private val feedDataSource: FeedDataSource,
    private val currentDate: String?,
    private val userId: Long?
) : PagingSource<Int, FeedContent>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedContent> {
        val pageNum = params.key ?: FEED_STARTING_PAGE_INDEX

        return try {
            val response = feedDataSource.getFeeds(
                currentDate = currentDate,
                userId = userId,
                page = pageNum,
                size = params.loadSize
            )

            val feedPage = response.data?.toDomain()
                ?: return LoadResult.Error(Exception("데이터 변환에 실패했습니다"))

            val feeds = feedPage.contents

            val nextKey = if (feedPage.meta.hasNext) {
                pageNum + 1
            } else {
                null
            }

            val prevKey = if (pageNum == FEED_STARTING_PAGE_INDEX) {
                null
            } else {
                pageNum - 1
            }

            LoadResult.Page(
                data = feeds,
                prevKey = prevKey,
                nextKey = nextKey
            )

        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, FeedContent>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

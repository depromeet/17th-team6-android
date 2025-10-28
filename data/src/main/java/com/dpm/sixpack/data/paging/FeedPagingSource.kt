package com.dpm.sixpack.data.paging

import android.graphics.pdf.LoadParams
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dpm.sixpack.domain.model.Feed
import com.dpm.sixpack.domain.usecase.GetFeedsByDateUseCase
import com.dpm.sixpack.domain.util.DoRunResult
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FeedPagingSource(
    private val getFeedsByDateUseCase: GetFeedsByDateUseCase,
    private val date: LocalDate
) : PagingSource<Int, Feed>() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Feed> {
        val page = params.key ?: 0
        return try {
            when (
                val result = getFeedsByDateUseCase(
                    currentDate = date.format(dateFormatter),
                    page = page,
                    size = params.loadSize
                )
            ) {
                is DoRunResult.Success -> {
                    LoadResult.Page(
                        data = result.data.contents.flatMap { it.feeds },
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (result.data.meta.hasNext) page + 1 else null
                    )
                }
                is DoRunResult.Failure -> {
                    LoadResult.Error(result.error)
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Feed>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

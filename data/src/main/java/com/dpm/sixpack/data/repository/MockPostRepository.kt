package com.dpm.sixpack.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dpm.sixpack.domain.model.Post
import com.dpm.sixpack.domain.repository.PostRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Mock implementation of PostRepository for development and testing.
 *
 * Provides paginated mock post data using PagingSource.
 */
class MockPostRepository
    @Inject
    constructor() : PostRepository {
        override fun getPosts(): Flow<PagingData<Post>> =
            Pager(
                config =
                    PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false,
                    ),
                pagingSourceFactory = { MockPostPagingSource() },
            ).flow
    }

/**
 * PagingSource for mock post data.
 *
 * Generates mock posts with sequential IDs and timestamps.
 */
private class MockPostPagingSource : PagingSource<Int, Post>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> =
        try {
            val page = params.key ?: 0
            val pageSize = params.loadSize

            // Simulate network delay
            delay(500)

            // Generate mock posts
            val posts = generateMockPosts(page, pageSize)

            LoadResult.Page(
                data = posts,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (posts.isEmpty()) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }

    private fun generateMockPosts(
        page: Int,
        pageSize: Int,
    ): List<Post> {
        val startId = page * pageSize
        val startYear = 2025
        val startMonth = 10
        val startDay = 14

        return (0 until pageSize).map { index ->
            val id = startId + index
            val daysAgo = index + (page * pageSize)

            // Calculate date (simple mock - doesn't handle month boundaries correctly)
            val day = (startDay - (daysAgo % 30)).coerceAtLeast(1)
            val month = (startMonth - (daysAgo / 30)).coerceAtLeast(1)

            Post(
                id = id.toLong(),
                imageUrl = null, // TODO: Add mock image URLs if needed
                createdAt = "$startYear-${month.toString().padStart(
                    2,
                    '0',
                )}-${day.toString().padStart(2, '0')}T10:30:00Z",
            )
        }
    }
}

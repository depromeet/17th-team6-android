package com.dpm.sixpack.domain.repository

import androidx.paging.PagingData
import com.dpm.sixpack.domain.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing Post data.
 *
 * This repository provides paginated access to Post data.
 */
interface PostRepository {
    /**
     * Observes a paginated stream of posts.
     *
     * @return Flow emitting PagingData containing Post items
     */
    fun getPosts(): Flow<PagingData<Post>>
}

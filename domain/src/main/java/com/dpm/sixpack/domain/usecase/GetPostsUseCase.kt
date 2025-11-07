package com.dpm.sixpack.domain.usecase

import androidx.paging.PagingData
import com.dpm.sixpack.domain.model.Post
import com.dpm.sixpack.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving paginated posts.
 *
 * This use case provides a stream of paginated post data from the repository.
 *
 * @property postRepository Repository for accessing post data
 */
class GetPostsUseCase
    @Inject
    constructor(
        private val postRepository: PostRepository,
    ) {
        /**
         * Retrieves a paginated flow of posts.
         *
         * @return Flow emitting PagingData containing Post items
         */
        operator fun invoke(): Flow<PagingData<Post>> = postRepository.getPosts()
    }

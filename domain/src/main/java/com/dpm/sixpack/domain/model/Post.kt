package com.dpm.sixpack.domain.model

/**
 * Post domain model representing a certification post.
 *
 * @property id Unique identifier for the post
 * @property imageUrl URL of the post image (nullable)
 * @property createdAt ISO 8601 formatted timestamp (e.g., "2025-10-14T10:30:00Z")
 */
data class Post(
    val id: Long,
    val imageUrl: String?,
    val createdAt: String,
)

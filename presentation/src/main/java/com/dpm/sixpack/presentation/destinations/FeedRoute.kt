package com.dpm.sixpack.presentation.destinations

import kotlinx.serialization.Serializable

sealed interface MainFeedRoute : Route

@Serializable
data class CertifiedUsers(
    val date: String,
) : MainFeedRoute

@Serializable
data object CertifiableRecord : MainFeedRoute

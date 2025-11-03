package com.dpm.sixpack.presentation.destinations

import kotlinx.serialization.Serializable

@Serializable
data class ProfileCreationRoute(
    val phoneNumber: String,
) : Route

package com.dpm.sixpack.data.source.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsentDto(
    @SerialName("marketingConsentAt") val marketingConsentAt: String?,
    @SerialName("locationConsentAt") val locationConsentAt: String?,
    @SerialName("personalConsentAt") val personalConsentAt: String,
)

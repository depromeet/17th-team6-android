package com.dpm.sixpack.data.source.remote.dto.response

import com.dpm.sixpack.domain.model.Uploadable
import com.dpm.sixpack.domain.model.UploadableReason
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadableResponseDto(
    @SerialName("isUploadable")
    val isUploadable: Boolean,
    @SerialName("reason")
    val reason: String?,
) {
    fun toDomain() =
        Uploadable(
            isUploadable = isUploadable,
            reason = reason?.let { UploadableReason.fromString(it) },
        )
}

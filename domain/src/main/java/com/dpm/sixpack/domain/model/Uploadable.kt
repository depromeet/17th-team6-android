package com.dpm.sixpack.domain.model

data class Uploadable(
    val isUploadable: Boolean,
    val reason: UploadableReason?,
)

sealed interface UploadableReason {
    data object RunNotToday : UploadableReason

    data object AlreadyUploadedToday : UploadableReason

    data object Unknown : UploadableReason

    companion object {
        fun fromString(reason: String): UploadableReason =
            when (reason) {
                "RUN_NOT_TODAY" -> RunNotToday
                "ALREADY_UPLOADED_TODAY" -> AlreadyUploadedToday
                else -> {
                    Unknown
                }
            }
    }
}

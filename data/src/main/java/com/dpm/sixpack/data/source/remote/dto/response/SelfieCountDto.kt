package com.dpm.sixpack.data.source.remote.dto.response

import com.dpm.sixpack.domain.model.SelfieCount
import com.dpm.sixpack.domain.model.SelfieCounts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//TODO SB swagger 명세 바뀌면 네이밍 수정
@Serializable
data class SelfieCountsDto(
    @SerialName("counts")
    val counts: List<SelfieCountDto>
) {
    fun toDomain(): SelfieCounts =
        SelfieCounts(
            counts = counts.map { it.toDomain() }
        )
}

@Serializable
data class SelfieCountDto(
    @SerialName("date")
    val date: String,
    @SerialName("selfieCount")
    val selfieCount: Int
) {
    fun toDomain(): SelfieCount =
        SelfieCount(
            date = date,
            selfieCount = selfieCount
        )
}

package com.dpm.sixpack.domain.model

data class SelfieCounts(
    val counts: List<SelfieCount>,
)

data class SelfieCount(
    val date: String,
    val selfieCount: Int,
)

package com.dpm.sixpack.domain.model

data class Course(
    val id: Long,
    val name: String,
    val distanceMeter: Double,
    val path: List<SimpleLocation>,
)

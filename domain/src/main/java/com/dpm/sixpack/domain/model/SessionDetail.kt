package com.dpm.sixpack.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SessionDetail(
    val id: Long,
    val createdAt: String,
    val updatedAt: String,
    val finishedAt: String,
    val distanceTotal: Int,
    val durationTotal: Int,
    val paceAvg: Int,
    val paceMax: Int,
    val paceMaxLatitude: Double,
    val paceMaxLongitude: Double,
    val cadenceAvg: Int,
    val cadenceMax: Int,
    val mapImage: String,
    val feed: SessionDetailFeed? = null,
    val segments: List<List<Segment>> = emptyList(),
) : Parcelable

@Parcelize
data class SessionDetailFeed(
    val id: Long,
    val mapImage: String,
    val selfieImage: String,
    val content: String,
    val createdAt: String,
) : Parcelable

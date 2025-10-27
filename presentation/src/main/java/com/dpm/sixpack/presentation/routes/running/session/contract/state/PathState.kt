package com.dpm.sixpack.presentation.routes.running.session.contract.state

import android.os.Parcelable
import com.naver.maps.geometry.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class PathState(
    val paths: List<List<LatLng>> = listOf(),
    val paces: List<List<Int>> = listOf(),
) : Parcelable {
    /**
     * 현재 PathState에 새로운 좌표와 페이스를 추가한
     * *새로운* PathState 객체를 반환합니다.
     *
     * @param newPoint 새로 추가할 좌표
     * @param newAvgPace 새로 추가할 시점의 평균 페이스
     */
    fun addPoint(
        newPoint: LatLng,
        newAvgPace: Int,
    ): PathState {
        val currentPaths = this.paths
        val currentPaces = this.paces

        // 세션을 처음 시작하는 경우 (paths가 비어있는 경우)
        if (currentPaths.isEmpty()) {
            return PathState(
                paths = listOf(listOf(newPoint)),
                paces = listOf(listOf(newAvgPace)),
            )
        }

        // 기존 경로에 이어서 추가하는 경우
        val previousPaths = currentPaths.dropLast(1)
        val previousPaces = currentPaces.dropLast(1)

        val newLastPath = currentPaths.last() + newPoint
        val newLastPace = currentPaces.last() + newAvgPace

        return PathState(
            paths = previousPaths + listOf(newLastPath),
            paces = previousPaces + listOf(newLastPace),
        )
    }
}

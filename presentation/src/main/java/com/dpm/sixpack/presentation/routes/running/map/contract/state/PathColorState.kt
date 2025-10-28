package com.dpm.sixpack.presentation.routes.running.map.contract.state

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.dpm.sixpack.presentation.routes.running.map.PaceColorCalculator
import com.dpm.sixpack.presentation.routes.running.session.contract.state.PathState
import com.naver.maps.geometry.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class PathColorState(
    val paths: List<List<LatLng>> = listOf(),
    val paceColors: List<List<Int>> = listOf(),
) : Parcelable {
    /**
     * 새로운 PathState를 기반으로, 가장 마지막에 추가된 경로의 페이스 색상을 계산하여 추가한
     * 새로운 [PathColorState] 객체를 반환합니다.
     * newPathState의 가장 마지막 페이스 값만 계산하여 기존 색상 리스트에 추가합니다.
     *
     * @param newPathState 새로운 좌표와 페이스가 추가된 최신 [PathState].
     * @return 색상이 추가된 새로운 [PathColorState].
     */
    fun updatedWith(newPathState: PathState): PathColorState {
        // 현재(이전) 상태의 색상 리스트
        val currentPaceColors = this.paceColors
        // 새로 추가된 가장 마지막 페이스 값 하나만 가져와 색상 계산
        val latestPaceColor = PaceColorCalculator(newPathState.paces.last().last())

        // 경로가 아예 없는 초기 상태
        if (currentPaceColors.isEmpty()) {
            return PathColorState(
                paths = newPathState.paths,
                paceColors = listOf(listOf(latestPaceColor)),
            )
        } else {
            // 마지막 리스트를 제외한 기존 색상 리스트들
            val unchangedColorLists = currentPaceColors.dropLast(1)
            // 마지막 리스트에 새로 계산한 색상만 추가
            val updatedLastColorList = currentPaceColors.last() + latestPaceColor

            return PathColorState(
                paths = newPathState.paths,
                paceColors = unchangedColorLists + listOf(updatedLastColorList),
            )
        }
    }
}

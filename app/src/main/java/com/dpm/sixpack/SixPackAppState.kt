package com.dpm.sixpack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.dpm.sixpack.core.util.NetworkMonitor
import com.dpm.sixpack.core.util.TimeZoneMonitor
import com.dpm.sixpack.main.navigation.MainNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone

@Composable
fun rememberSixPackAppState(
    navigator: MainNavigator,
    networkMonitor: NetworkMonitor,
    timeZoneMonitor: TimeZoneMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): SixPackAppState =
    remember(
        navigator,
        coroutineScope,
        networkMonitor,
        timeZoneMonitor,
    ) {
        SixPackAppState(
            navigator = navigator,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor,
            timeZoneMonitor = timeZoneMonitor,
        )
    }

@Stable
class SixPackAppState(
    val navigator: MainNavigator,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    timeZoneMonitor: TimeZoneMonitor,
) {
    val isOffline =
        networkMonitor.isOnline
            .map(Boolean::not)
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false,
            )

    val currentTimeZone =
        timeZoneMonitor.currentTimeZone
            .stateIn(
                coroutineScope,
                SharingStarted.WhileSubscribed(5_000),
                TimeZone.currentSystemDefault(),
            )
}

val LocalTimeZone = compositionLocalOf { TimeZone.currentSystemDefault() }

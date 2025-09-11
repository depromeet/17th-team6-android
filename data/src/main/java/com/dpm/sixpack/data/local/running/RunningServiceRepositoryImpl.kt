package com.dpm.sixpack.data.local.running

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.dpm.sixpack.core.network.di.ApplicationScope
import com.dpm.sixpack.domain.model.RunningState
import com.dpm.sixpack.domain.running.RunningServiceRepository
import com.dpm.sixpack.runningservice.RunningService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class RunningServiceRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
) : RunningServiceRepository {

    @Inject
    @ApplicationScope
    lateinit var scope: CoroutineScope

    private val _runningDataState = MutableStateFlow(RunningState())
    override val runningDataState = _runningDataState.asStateFlow()

    private var runningService: RunningService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            runningService = (service as RunningService.RunningBinder).getService()
            observeServiceData()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            runningService = null
        }
    }

    init {
        Intent(context, RunningService::class.java).also { intent ->
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun observeServiceData() {
        runningService?.runningDataState?.onEach { data ->
            _runningDataState.value = data
        }?.launchIn(scope)
    }
}

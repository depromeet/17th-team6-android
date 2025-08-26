package com.dpm.sixpack.domain.example

import kotlinx.coroutines.flow.Flow

interface ExampleRepository {
    fun getCount(): Flow<Int>

    suspend fun changeCount(amount: Int)
}

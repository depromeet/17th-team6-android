package com.dpm.sixpack.data.example

import com.dpm.sixpack.domain.example.ExampleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExampleRepositoryImpl @Inject constructor(
    private val localDataSource: ExampleLocalDataSource,
) : ExampleRepository {

    override fun getCount(): Flow<Int> = localDataSource.getCount()

    override suspend fun changeCount(amount: Int) {
        localDataSource.changeCount(amount)
    }
}

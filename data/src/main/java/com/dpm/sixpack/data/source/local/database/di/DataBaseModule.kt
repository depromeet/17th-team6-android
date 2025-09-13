package com.dpm.sixpack.data.source.local.database.di

import android.content.Context
import androidx.room.Room
import com.dpm.sixpack.data.source.local.database.RunningDataBase
import com.dpm.sixpack.data.source.local.database.dao.RunningDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): RunningDataBase =
        Room
            .databaseBuilder(
                context,
                RunningDataBase::class.java,
                "my-app-database",
            ).build()

    @Provides
    @Singleton
    fun providePostDao(db: RunningDataBase): RunningDao = db.runningDao()
}

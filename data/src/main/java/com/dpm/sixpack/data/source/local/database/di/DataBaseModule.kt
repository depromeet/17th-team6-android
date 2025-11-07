package com.dpm.sixpack.data.source.local.database.di

import android.content.Context
import androidx.room.Room
import com.dpm.sixpack.data.source.local.database.RunningDatabase
import com.dpm.sixpack.data.source.local.database.dao.RunningSessionDao
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
    ): RunningDatabase =
        Room
            .databaseBuilder(
                context,
                RunningDatabase::class.java,
                "my-app-database",
            ).build()

    @Provides
    @Singleton
    fun providePostDao(db: RunningDatabase): RunningSessionDao = db.runningSessionDao()
}

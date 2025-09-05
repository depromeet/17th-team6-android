package com.dpm.sixpack.data.di

import android.content.Context
import androidx.room.Room
import com.dpm.sixpack.data.local.running.RunningDatabase
import com.dpm.sixpack.data.local.running.dao.RunningDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): RunningDatabase {
        return Room.databaseBuilder(
            context,
            RunningDatabase::class.java,
            "my-app-database"
        ).build()
    }
    
    @Provides
    @Singleton
    fun providePostDao(db: RunningDatabase): RunningDao {
        return db.runningDao()
    }
}

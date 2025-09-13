package com.dpm.sixpack.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dpm.sixpack.data.source.local.database.dao.RunningDao
import com.dpm.sixpack.data.source.local.database.entity.CourseEntity
import com.dpm.sixpack.data.source.local.database.entity.LocationPointEntity
import com.dpm.sixpack.data.source.local.database.entity.RunSessionEntity

@Database(
    entities = [
        RunSessionEntity::class,
        CourseEntity::class,
        LocationPointEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class RunningDataBase : RoomDatabase() {
    abstract fun runningDao(): RunningDao
}

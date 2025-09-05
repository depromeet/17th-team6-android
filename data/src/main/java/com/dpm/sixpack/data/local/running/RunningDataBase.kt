package com.dpm.sixpack.data.local.running

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dpm.sixpack.data.local.running.dao.RunningDao
import com.dpm.sixpack.data.local.running.entity.CourseEntity
import com.dpm.sixpack.data.local.running.entity.LocationPointEntity
import com.dpm.sixpack.data.local.running.entity.RunSessionEntity

@Database(
    entities = [
        RunSessionEntity::class,
        CourseEntity::class,
        LocationPointEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RunningDatabase : RoomDatabase() {
    abstract fun runningDao(): RunningDao
}

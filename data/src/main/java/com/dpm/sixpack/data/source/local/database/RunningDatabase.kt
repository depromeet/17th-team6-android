package com.dpm.sixpack.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dpm.sixpack.data.source.local.database.dao.RunningSessionDao
import com.dpm.sixpack.data.source.local.database.entity.RunningTrackPointEntity

@Database(entities = [RunningTrackPointEntity::class], version = 1, exportSchema = false)
abstract class RunningDatabase : RoomDatabase() {
    abstract fun runningSessionDao(): RunningSessionDao
}

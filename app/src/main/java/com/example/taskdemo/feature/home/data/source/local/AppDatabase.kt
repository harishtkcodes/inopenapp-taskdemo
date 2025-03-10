package com.example.taskdemo.feature.home.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.taskdemo.feature.home.data.source.local.dao.DashboardDataDao
import com.example.taskdemo.feature.home.data.source.local.dao.RefreshMetadataDao
import com.example.taskdemo.feature.home.data.source.local.entity.DashboardDataEntity
import com.example.taskdemo.feature.home.data.source.local.entity.RefreshMetadataEntity

@Database(
    entities = [DashboardDataEntity::class, RefreshMetadataEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dashboardDataDao(): DashboardDataDao
    abstract fun refreshMetadataDao(): RefreshMetadataDao

    class Factory {
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration().build()
        }
    }

    companion object {
        private const val DATABASE_NAME = "app_database"

        const val TABLE_DASHBOARD_DATA = "dashboard_data"
        const val TABLE_REFRESH_METADATA = "refresh_metadata"
    }
}
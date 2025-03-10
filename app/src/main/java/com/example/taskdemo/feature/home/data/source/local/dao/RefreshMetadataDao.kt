package com.example.taskdemo.feature.home.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskdemo.feature.home.data.source.local.AppDatabase.Companion.TABLE_REFRESH_METADATA
import com.example.taskdemo.feature.home.data.source.local.entity.RefreshMetadataEntity

@Dao
interface RefreshMetadataDao {
    @Query("SELECT * FROM $TABLE_REFRESH_METADATA WHERE refreshKey = :key")
    suspend fun getRefreshMetadata(key: String): RefreshMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRefreshMetadata(refreshMetadata: RefreshMetadataEntity)
}
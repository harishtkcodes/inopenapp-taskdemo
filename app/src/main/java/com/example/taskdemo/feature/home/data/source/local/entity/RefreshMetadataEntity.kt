package com.example.taskdemo.feature.home.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskdemo.feature.home.data.source.local.AppDatabase
import com.example.taskdemo.feature.home.domain.model.RefreshMetadata

@Entity(tableName = AppDatabase.TABLE_REFRESH_METADATA)
data class RefreshMetadataEntity(
    @PrimaryKey val refreshKey: String,
    val lastRefreshedTime: Long
)

fun RefreshMetadataEntity.toRefreshMetadata(): RefreshMetadata {
    return RefreshMetadata(
        refreshKey = this.refreshKey,
        lastRefreshedTime = this.lastRefreshedTime
    )
}

fun RefreshMetadata.toEntity(): RefreshMetadataEntity {
    return RefreshMetadataEntity(
        refreshKey = this.refreshKey,
        lastRefreshedTime = this.lastRefreshedTime
    )
}
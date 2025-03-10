package com.example.taskdemo.feature.home.data.source.local

import com.example.taskdemo.feature.home.data.source.local.dao.RefreshMetadataDao
import com.example.taskdemo.feature.home.data.source.local.entity.RefreshMetadataEntity
import javax.inject.Inject

class RoomRefreshMetadataDataSource @Inject constructor(
    private val refreshMetadataDao: RefreshMetadataDao
) : RefreshMetadataDataSource {

    override suspend fun getRefreshMetadata(key: String): RefreshMetadataEntity? {
        return refreshMetadataDao.getRefreshMetadata(key)
    }

    override suspend fun insertRefreshMetadata(refreshMetadata: RefreshMetadataEntity) {
        refreshMetadataDao.insertRefreshMetadata(refreshMetadata)
    }
}
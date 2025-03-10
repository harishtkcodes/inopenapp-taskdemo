package com.example.taskdemo.feature.home.data.source.local

import com.example.taskdemo.feature.home.data.source.local.entity.RefreshMetadataEntity

interface RefreshMetadataDataSource {
    suspend fun getRefreshMetadata(key: String): RefreshMetadataEntity?
    suspend fun insertRefreshMetadata(refreshMetadata: RefreshMetadataEntity)
}
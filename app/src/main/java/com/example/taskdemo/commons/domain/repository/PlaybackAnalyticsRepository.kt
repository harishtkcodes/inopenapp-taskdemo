package com.example.taskdemo.commons.domain.repository

import kotlinx.coroutines.flow.Flow

interface PlaybackAnalyticsRepository {
    fun addFirstFrameRenderDelay(key: String, delayMs: Int)
    fun getAveragePlaybackTime(): Flow<Int>
}
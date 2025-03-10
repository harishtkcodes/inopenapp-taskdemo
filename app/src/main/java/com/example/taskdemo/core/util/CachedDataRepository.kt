package com.example.taskdemo.core.util

import com.example.taskdemo.commons.util.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Note: This class is written by CoPilot AI and is not part of the original app code.
 */
class CachedDataRepository<T>(
    private val dataFetcher: suspend () -> Flow<T>,
    private val cacheReader: suspend () -> T?,
    private val cacheWriter: suspend (T) -> Unit,
    private val refreshIntervalMinutes: Long = 5,
    private val refreshThreshold: Int = 3
) {
    private var lastFetchTime: Long = 0
    private var refreshCount: Int = 0
    private val mutex = Mutex()

    val data: Flow<Result<T>> = flow {
        emit(Result.Loading)
        val cachedData = cacheReader()
        if (cachedData != null) {
            emit(
                Result.Success(
                    cachedData,
//                    isFromCache = true,
//                    lastCacheTime = Date(lastFetchTime)
                )
            )
        }

        if (shouldRefreshData()) {
            try {
                executeRefresh()
            } catch (e: Exception) {
                emit(Result.Error(e))
                if (cachedData != null) {
                    emit(
                        Result.Success(
                            cachedData,
//                            isFromCache = true,
//                            lastCacheTime = Date(lastFetchTime)
                        )
                    )
                }
            }
        }
    }

    private suspend fun shouldRefreshData(): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastFetch = currentTime - lastFetchTime
        val intervalMillis = TimeUnit.MINUTES.toMillis(refreshIntervalMinutes)

        return timeSinceLastFetch >= intervalMillis || refreshCount > refreshThreshold
    }

    private suspend fun executeRefresh() {
        try {
            mutex.withLock {
                if (refreshCount > refreshThreshold) {
                    refreshCount = 0 // Reset count on forced refresh
                }
                dataFetcher().collect { fetchedData ->
                    cacheWriter(fetchedData)
                    lastFetchTime = System.currentTimeMillis()
                }
            }
        } catch (e: Exception) {
            // Log the error for debugging purposes
            println("Error refreshing data: ${e.message}")
            throw e // Re-throw the exception to be caught by the calling function
        }
    }

    suspend fun refreshData() {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastFetch = currentTime - lastFetchTime
        val intervalMillis = TimeUnit.MINUTES.toMillis(refreshIntervalMinutes)

        if (timeSinceLastFetch < intervalMillis && refreshCount <= refreshThreshold) {
            refreshCount++
            return // Ignore refresh
        }

        try {
            executeRefresh()
        } catch (e: Exception) {
            // Handle error, possibly log it
            println("Error during manual refresh: ${e.message}")
        }
    }
}
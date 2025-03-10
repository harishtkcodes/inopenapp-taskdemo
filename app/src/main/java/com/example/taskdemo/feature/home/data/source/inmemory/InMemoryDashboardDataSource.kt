package com.example.taskdemo.feature.home.data.source.inmemory

import com.example.taskdemo.feature.home.domain.model.DashboardData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * A concrete class to hold tasks data in-memory
 */
class InMemoryDashboardDataSource {

    /**
     * A backing hot-flow for list of [DashboardData] for caching
     */
    val dashboardDataFlow: MutableSharedFlow<DashboardData?> =
        MutableSharedFlow(
            replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    init {
        setDashboardData(null)
    }

    fun setDashboardData(dashboardData: DashboardData?) {
        dashboardDataFlow.tryEmit(dashboardData)
    }

}
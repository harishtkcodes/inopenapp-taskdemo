package com.example.taskdemo.feature.home.data.source.local

import com.example.taskdemo.feature.home.data.source.local.entity.DashboardDataEntity
import kotlinx.coroutines.flow.Flow

interface DashboardLocalDataSource {

    fun dashboardStream(): Flow<DashboardDataEntity?>

    suspend fun insertDashboardData(dashboardData: DashboardDataEntity)
}
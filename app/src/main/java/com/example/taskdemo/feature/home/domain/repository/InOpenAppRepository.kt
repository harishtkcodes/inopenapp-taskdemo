package com.example.taskdemo.feature.home.domain.repository

import com.example.taskdemo.commons.util.Result
import com.example.taskdemo.feature.home.domain.model.DashboardData
import kotlinx.coroutines.flow.Flow

interface InOpenAppRepository {

    fun dashboardStream(): Flow<DashboardData>

    suspend fun refreshDashboard(): Result<DashboardData>

}
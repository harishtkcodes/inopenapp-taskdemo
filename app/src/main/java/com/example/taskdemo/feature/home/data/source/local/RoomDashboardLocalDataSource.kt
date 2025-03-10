package com.example.taskdemo.feature.home.data.source.local

import com.example.taskdemo.feature.home.data.source.local.dao.DashboardDataDao
import com.example.taskdemo.feature.home.data.source.local.entity.DashboardDataEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomDashboardLocalDataSource @Inject constructor(
    private val dashboardDataDao: DashboardDataDao,
) : DashboardLocalDataSource {

    override fun dashboardStream(): Flow<DashboardDataEntity?> {
        return dashboardDataDao.getDashboardData()
    }

    override suspend fun insertDashboardData(dashboardData: DashboardDataEntity) {
        dashboardDataDao.insertDashboardData(dashboardData)
    }

}
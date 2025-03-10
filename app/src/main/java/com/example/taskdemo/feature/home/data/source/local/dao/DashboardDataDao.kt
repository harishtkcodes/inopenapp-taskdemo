package com.example.taskdemo.feature.home.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskdemo.feature.home.data.source.local.entity.DashboardDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DashboardDataDao {

    @Query("SELECT * FROM dashboard_data WHERE id = 0")
    fun getDashboardData(): Flow<DashboardDataEntity?>

    @Query("SELECT * FROM dashboard_data WHERE id = 0")
    suspend fun getDashboardDataSync(): DashboardDataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDashboardData(dashboardData: DashboardDataEntity)
}
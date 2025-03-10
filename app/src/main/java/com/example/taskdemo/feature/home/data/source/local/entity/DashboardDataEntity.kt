package com.example.taskdemo.feature.home.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskdemo.feature.home.data.source.local.AppDatabase
import com.example.taskdemo.feature.home.domain.model.ChartKVPair
import com.example.taskdemo.feature.home.domain.model.DashboardData
import com.example.taskdemo.feature.home.domain.model.OpenAppLink

@Entity(tableName = AppDatabase.TABLE_DASHBOARD_DATA)
data class DashboardDataEntity(
    @PrimaryKey val id: Int = 0,
    val supportWhatsappNumber: String?,
    val extraIncome: Double,
    val totalLinks: Int,
    val totalClicks: Int,
    val topSource: String?,
    val topLocation: String?,
    val startTime: String,
    val linksCreatedToday: Int,
    val appliedCampaign: Int,
    val recentLinks: List<OpenAppLink>,
    val topLinks: List<OpenAppLink>,
    val favouriteLinks: List<OpenAppLink>,
    val overallUrlChart: List<ChartKVPair>?
)

fun DashboardData.toDashboardDataEntity(): DashboardDataEntity {
    return DashboardDataEntity(
        supportWhatsappNumber = this.supportWhatsappNumber,
        extraIncome = this.extraIncome,
        totalLinks = this.totalLinks,
        totalClicks = this.totalClicks,
        topSource = this.topSource,
        topLocation = this.topLocation,
        startTime = this.startTime,
        linksCreatedToday = this.linksCreatedToday,
        appliedCampaign = this.appliedCampaign,
        recentLinks = this.recentLinks,
        topLinks = this.topLinks,
        favouriteLinks = this.favouriteLinks,
        overallUrlChart = this.overallUrlChart,
    )
}

fun DashboardDataEntity.toDashboardData(): DashboardData {
    return DashboardData(
        supportWhatsappNumber = this.supportWhatsappNumber,
        extraIncome = this.extraIncome,
        totalLinks = this.totalLinks,
        totalClicks = this.totalClicks,
        topSource = this.topSource,
        topLocation = this.topLocation,
        startTime = this.startTime,
        linksCreatedToday = this.linksCreatedToday,
        appliedCampaign = this.appliedCampaign,
        recentLinks = this.recentLinks,
        topLinks = this.topLinks,
        favouriteLinks = this.favouriteLinks,
        overallUrlChart = this.overallUrlChart
    )
}
package com.example.taskdemo.feature.home.domain.model

import com.example.taskdemo.feature.home.data.source.remote.dto.OpenAppLinkDto
import kotlinx.serialization.json.JsonObject

data class DashboardData(
    val supportWhatsappNumber: String?,
    val extraIncome: Double,
    val totalLinks: Int,
    val totalClicks: Int,
    val topSource: String,
    val topLocation: String,
    val startTime: String,
    val linksCreatedToday: Int,
    val appliedCampaign: Int,
    val recentLinks: List<OpenAppLink>?,
    val topLinks: List<OpenAppLink>?,
    val favouriteLinks: List<OpenAppLink>?,
    val overallUrlChart: JsonObject?
) {
    companion object {
        val EMPTY = DashboardData(
            supportWhatsappNumber = "",
            extraIncome = 0.0,
            totalLinks = 0,
            totalClicks = 0,
            topSource = "",
            topLocation = "",
            startTime = "",
            linksCreatedToday = 0,
            appliedCampaign = 0,
            recentLinks = null,
            topLinks = null,
            favouriteLinks = null,
            overallUrlChart = null
        )
    }
}
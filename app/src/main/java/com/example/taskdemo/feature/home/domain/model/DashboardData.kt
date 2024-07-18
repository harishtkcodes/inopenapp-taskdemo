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
)
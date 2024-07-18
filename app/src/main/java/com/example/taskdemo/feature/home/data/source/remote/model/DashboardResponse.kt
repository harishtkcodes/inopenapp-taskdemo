package com.example.taskdemo.feature.home.data.source.remote.model

import com.example.taskdemo.feature.home.data.source.remote.dto.OpenAppLinkDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class DashboardResponse(
    val status: Boolean,
    val statusCode: Int,
    val message: String,
    @SerialName("support_whatsapp_number")
    val supportWhatsappNumber: String?,
    @SerialName("extra_income")
    val extraIncome: Double,
    @SerialName("total_links")
    val totalLinks: Int,
    @SerialName("total_clicks")
    val totalClicks: Int,
    @SerialName("top_source")
    val topSource: String,
    @SerialName("top_location")
    val topLocation: String,
    val startTime: String,
    @SerialName("links_created_today")
    val linksCreatedToday: Int,
    @SerialName("applied_campaign")
    val appliedCampaign: Int,
    val data: Data?
) {
    @Serializable
    data class Data(
        @SerialName("recent_links")
        val recentLinks: List<OpenAppLinkDto>?,
        @SerialName("top_links")
        val topLinks: List<OpenAppLinkDto>?,
        @SerialName("favourite_links")
        val favouriteLinks: List<OpenAppLinkDto>?,
        @SerialName("overall_url_chart")
        val overallUrlChart: JsonObject?
    )
}


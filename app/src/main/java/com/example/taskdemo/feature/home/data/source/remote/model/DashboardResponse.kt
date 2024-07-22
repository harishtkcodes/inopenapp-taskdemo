package com.example.taskdemo.feature.home.data.source.remote.model

import com.example.taskdemo.feature.home.data.source.remote.dto.OpenAppLinkDto
import com.example.taskdemo.feature.home.data.source.remote.model.dto.ChartKVPairDto
import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    val status: Boolean,
    val statusCode: Int,
    val message: String,
    @SerializedName("support_whatsapp_number")
    val supportWhatsappNumber: String?,
    @SerializedName("extra_income")
    val extraIncome: Double,
    @SerializedName("total_links")
    val totalLinks: Int,
    @SerializedName("total_clicks")
    val totalClicks: Int,
    @SerializedName("top_source")
    val topSource: String?,
    @SerializedName("top_location")
    val topLocation: String?,
    val startTime: String,
    @SerializedName("links_created_today")
    val linksCreatedToday: Int,
    @SerializedName("applied_campaign")
    val appliedCampaign: Int,
    val data: Data?
) {

    data class Data(
        @SerializedName("recent_links")
        val recentLinks: List<OpenAppLinkDto>?,
        @SerializedName("top_links")
        val topLinks: List<OpenAppLinkDto>?,
        @SerializedName("favourite_links")
        val favouriteLinks: List<OpenAppLinkDto>?,
        @SerializedName("overall_url_chart")
        val overallUrlChart: List<ChartKVPairDto>?
    )
}


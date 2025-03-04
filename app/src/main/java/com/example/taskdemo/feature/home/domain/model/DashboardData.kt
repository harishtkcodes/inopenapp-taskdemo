package com.example.taskdemo.feature.home.domain.model

data class DashboardData(
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
            recentLinks = emptyList(),
            topLinks = emptyList(),
            favouriteLinks = emptyList(),
            overallUrlChart = emptyList(),
        )

        fun create(
            supportWhatsappNumber: String? = null,
            extraIncome: Double = 0.0,
            totalLinks: Int = 0,
            totalClicks: Int = 0,
            topSource: String? = null,
            topLocation: String? = null,
            startTime: String = "",
            linksCreatedToday: Int = 0,
            appliedCampaign: Int = 0,
            recentLinks: List<OpenAppLink> = emptyList(),
            topLinks: List<OpenAppLink> = emptyList(),
            favouriteLinks: List<OpenAppLink> = emptyList(),
            overallUrlChart: List<ChartKVPair>? = emptyList()
        ) = DashboardData(
            supportWhatsappNumber,
            extraIncome,
            totalLinks,
            totalClicks,
            topSource,
            topLocation,
            startTime,
            linksCreatedToday,
            appliedCampaign,
            recentLinks,
            topLinks,
            favouriteLinks,
            overallUrlChart
        )
    }
}
package com.example.taskdemo.feature.home.domain.model

data class OpenAppLink(
    val urlId: Long,
    val webLink: String,
    val smartLink: String,
    val title: String,
    val totalClicks: Int,
    val originalImage: String?,
    val thumbnail: String?,
    val timesAgo: String?,
    val createdAt: String?,
    val domainId: String,
    val urlPrefix: String?,
    val urlSuffix: String?,
    val app: String,
    val isFavourite: Boolean
)
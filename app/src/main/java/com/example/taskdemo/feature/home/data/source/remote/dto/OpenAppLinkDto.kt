package com.example.taskdemo.feature.home.data.source.remote.dto

import com.example.taskdemo.feature.home.domain.model.OpenAppLink
import com.google.gson.annotations.SerializedName

data class OpenAppLinkDto(
    @SerializedName("url_id")
    val urlId: Long,
    @SerializedName("web_link")
    val webLink: String?,
    @SerializedName("smart_link")
    val smartLink: String?,
    @SerializedName("title")
    val title: String,
    @SerializedName("total_clicks")
    val totalClicks: Int,
    @SerializedName("original_image")
    val originalImage: String?,
    val thumbnail: String?,
    @SerializedName("times_ago")
    val timesAgo: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("domain_id")
    val domainId: String?,
    @SerializedName("url_prefix")
    val urlPrefix: String?,
    @SerializedName("url_suffix")
    val urlSuffix: String?,
    @SerializedName("app")
    val app: String,
    @SerializedName("is_favourite")
    val isFavourite: Boolean
)

fun OpenAppLinkDto.toOpenAppLink(): OpenAppLink {
    return OpenAppLink(
        urlId = urlId,
        webLink = webLink ?: "",
        smartLink = smartLink ?: "",
        title = title,
        totalClicks = totalClicks,
        originalImage = originalImage,
        thumbnail = thumbnail,
        timesAgo = timesAgo,
        createdAt = createdAt,
        domainId = domainId ?: "",
        urlPrefix = urlPrefix,
        urlSuffix = urlSuffix,
        app = app,
        isFavourite = isFavourite
    )
}
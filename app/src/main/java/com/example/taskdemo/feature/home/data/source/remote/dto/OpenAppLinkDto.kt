package com.example.taskdemo.feature.home.data.source.remote.dto

import com.example.taskdemo.feature.home.domain.model.OpenAppLink
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAppLinkDto(
    @SerialName("url_id")
    val urlId: Long,
    @SerialName("web_link")
    val webLink: String,
    @SerialName("smart_link")
    val smartLink: String,
    @SerialName("title")
    val title: String,
    @SerialName("total_clicks")
    val totalClicks: Int,
    @SerialName("original_image")
    val originalImage: String?,
    val thumbnail: String?,
    @SerialName("times_ago")
    val timesAgo: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("domain_id")
    val domainId: String,
    @SerialName("url_prefix")
    val urlPrefix: String?,
    @SerialName("url_suffix")
    val urlSuffix: String?,
    @SerialName("app")
    val app: String,
    @SerialName("is_favourite")
    val isFavourite: Boolean
)

fun OpenAppLinkDto.toOpenAppLink(): OpenAppLink {
    return OpenAppLink(
        urlId = urlId,
        webLink = webLink,
        smartLink = smartLink,
        title = title,
        totalClicks = totalClicks,
        originalImage = originalImage,
        thumbnail = thumbnail,
        timesAgo = timesAgo,
        createdAt = createdAt,
        domainId = domainId,
        urlPrefix = urlPrefix,
        urlSuffix = urlSuffix,
        app = app,
        isFavourite = isFavourite
    )
}
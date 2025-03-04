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
) {

    companion object {
        fun create(
            urlId: Long = 0L,
            webLink: String = "",
            smartLink: String = "",
            title: String = "",
            totalClicks: Int = 0,
            originalImage: String? = null,
            thumbnail: String? = null,
            timesAgo: String? = null,
            createdAt: String? = null,
            domainId: String = "",
            urlPrefix: String? = null,
            urlSuffix: String? = null,
            app: String = "",
            isFavourite: Boolean = false
        ) = OpenAppLink(
            urlId,
            webLink,
            smartLink,
            title,
            totalClicks,
            originalImage,
            thumbnail,
            timesAgo,
            createdAt,
            domainId,
            urlPrefix,
            urlSuffix,
            app,
            isFavourite
        )
    }
}
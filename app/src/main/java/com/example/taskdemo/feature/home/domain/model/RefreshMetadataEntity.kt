package com.example.taskdemo.feature.home.domain.model

data class RefreshMetadata(
    val refreshKey: String,
    val lastRefreshedTime: Long
)
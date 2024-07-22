package com.example.taskdemo.feature.home.data.source.remote.model.dto

import com.example.taskdemo.feature.home.domain.model.ChartKVPair
import com.google.gson.annotations.SerializedName

data class ChartKVPairDto(
    @SerializedName("key")
    val key: String,
    @SerializedName("value")
    val value: Double,
)

fun ChartKVPairDto.toChartKVPair(): ChartKVPair {
    return ChartKVPair(
        key = key,
        value = value
    )
}
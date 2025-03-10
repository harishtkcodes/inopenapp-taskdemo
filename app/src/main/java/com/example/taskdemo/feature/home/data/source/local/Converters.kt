package com.example.taskdemo.feature.home.data.source.local

import androidx.room.TypeConverter
import com.example.taskdemo.feature.home.domain.model.ChartKVPair
import com.example.taskdemo.feature.home.domain.model.OpenAppLink
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromOpenAppLinkList(value: String): List<OpenAppLink> {
        val listType = object : TypeToken<List<OpenAppLink>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromOpenAppLinkList(list: List<OpenAppLink>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromChartKVPairList(value: String): List<ChartKVPair> {
        val listType = object : TypeToken<List<ChartKVPair>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromChartKVPairList(list: List<ChartKVPair>): String {
        return Gson().toJson(list)
    }
}
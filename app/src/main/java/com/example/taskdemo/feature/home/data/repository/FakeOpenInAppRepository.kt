package com.example.taskdemo.feature.home.data.repository

import android.content.Context
import com.example.taskdemo.R
import com.example.taskdemo.commons.util.Result
import com.example.taskdemo.commons.util.parseJsonFromString
import com.example.taskdemo.feature.home.data.source.inmemory.InMemoryInOpenAppDataSource
import com.example.taskdemo.feature.home.data.source.remote.dto.OpenAppLinkDto
import com.example.taskdemo.feature.home.data.source.remote.dto.toOpenAppLink
import com.example.taskdemo.feature.home.data.source.remote.model.DashboardResponse
import com.example.taskdemo.feature.home.data.source.remote.model.dto.ChartKVPairDto
import com.example.taskdemo.feature.home.data.source.remote.model.dto.toChartKVPair
import com.example.taskdemo.feature.home.domain.model.DashboardData
import com.example.taskdemo.feature.home.domain.repository.InOpenAppRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeOpenInAppRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkJson: Json,
    private val gson: Gson,
    private val localDataSource: InMemoryInOpenAppDataSource,
) : InOpenAppRepository {

    override fun dashboardStream(): Flow<DashboardData> {
        return localDataSource.dashboardDataFlow
            .filterNotNull()
    }

    override suspend fun refreshDashboard(): Result<DashboardData> {
        return try {
            // Imitate network delay
            delay(2_000)
            val jsonString = parseJsonFromString(context, R.raw.dashboard_response)
            val data: DashboardData = gson.fromJson(jsonString, DashboardResponse::class.java).let { response ->
                DashboardData(
                    supportWhatsappNumber = response.supportWhatsappNumber,
                    extraIncome = response.extraIncome,
                    totalLinks = response.totalLinks,
                    totalClicks = response.totalClicks,
                    topSource = response.topSource,
                    topLocation = response.topLocation,
                    startTime = response.startTime,
                    linksCreatedToday = response.linksCreatedToday,
                    appliedCampaign = response.appliedCampaign,
                    recentLinks = response.data?.recentLinks?.map(OpenAppLinkDto::toOpenAppLink) ?: emptyList(),
                    topLinks = response.data?.topLinks?.map(OpenAppLinkDto::toOpenAppLink) ?: emptyList(),
                    favouriteLinks = response.data?.favouriteLinks?.map(OpenAppLinkDto::toOpenAppLink) ?: emptyList(),
                    overallUrlChart = response.data?.overallUrlChart?.map(ChartKVPairDto::toChartKVPair) ?: emptyList(),
                )
            }
            localDataSource.setDashboardData(data)
            Result.Success(data)
        } catch (e: Exception) {
            Timber.e(e)
            return Result.Error(e)
        }
    }

}
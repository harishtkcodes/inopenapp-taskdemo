package com.example.taskdemo.feature.home.data.repository

import android.content.Context
import com.example.taskdemo.R
import com.example.taskdemo.commons.util.Result
import com.example.taskdemo.commons.util.parseJsonFromString
import com.example.taskdemo.feature.home.data.source.inmemory.InMemoryDashboardDataSource
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

/**
 * A fake implementation of the InOpenAppRepository interface for testing purposes.
 * This repository simulates network operations and provides data from a local in-memory data source.
 *
 * @property context The application context.
 * @property networkJson The JSON parser for network responses.
 * @property gson The Gson instance for JSON parsing.
 * @property localDataSource The in-memory data source for dashboard data.
 */
@Singleton
class FakeOpenInAppRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkJson: Json,
    private val gson: Gson,
    private val localDataSource: InMemoryDashboardDataSource,
) : InOpenAppRepository {

    /**
     * Returns a flow of DashboardData from the local in-memory data source.
     *
     * @return A Flow emitting DashboardData.
     */
    override fun dashboardStream(): Flow<DashboardData> {
        return localDataSource.dashboardDataFlow
            .filterNotNull()
    }

    /**
     * Refreshes the dashboard data by simulating a network request and updating the local data source.
     *
     * @return A Result containing the refreshed DashboardData or an error.
     */
    override suspend fun refreshDashboard(): Result<DashboardData> {
        return try {
            // Imitate network delay
            delay(2_000)
            // Parse the JSON response from a raw resource file
            val jsonString = parseJsonFromString(context, R.raw.dashboard_response)
            // Convert the JSON string to a DashboardData object
            val data: DashboardData =
                gson.fromJson(jsonString, DashboardResponse::class.java).let { response ->
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
                        recentLinks = response.data?.recentLinks?.map(OpenAppLinkDto::toOpenAppLink)
                            ?: emptyList(),
                        topLinks = response.data?.topLinks?.map(OpenAppLinkDto::toOpenAppLink)
                            ?: emptyList(),
                        favouriteLinks = response.data?.favouriteLinks?.map(OpenAppLinkDto::toOpenAppLink)
                            ?: emptyList(),
                        overallUrlChart = response.data?.overallUrlChart?.map(ChartKVPairDto::toChartKVPair)
                            ?: emptyList(),
                    )
                }
            // Update the local data source with the new data
            localDataSource.setDashboardData(data)
            Result.Success(data)
        } catch (e: Exception) {
            Timber.e(e)
            return Result.Error(e)
        }
    }
}
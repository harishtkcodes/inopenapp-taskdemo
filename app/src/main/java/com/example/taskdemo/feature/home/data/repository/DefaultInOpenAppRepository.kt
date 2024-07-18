package com.example.taskdemo.feature.home.data.repository

import com.example.taskdemo.commons.util.NetworkResult
import com.example.taskdemo.commons.util.NetworkResultParser
import com.example.taskdemo.commons.util.Result
import com.example.taskdemo.core.di.Dispatcher
import com.example.taskdemo.core.di.TaskDemoDispatchers
import com.example.taskdemo.feature.home.data.source.inmemory.InMemoryInOpenAppDataSource
import com.example.taskdemo.feature.home.data.source.remote.InOpenAppRemoteDataSource
import com.example.taskdemo.feature.home.data.source.remote.dto.OpenAppLinkDto
import com.example.taskdemo.feature.home.data.source.remote.dto.toOpenAppLink
import com.example.taskdemo.feature.home.domain.model.DashboardData
import com.example.taskdemo.feature.home.domain.repository.InOpenAppRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultInOpenAppRepository @Inject constructor(
    private val localDataSource: InMemoryInOpenAppDataSource,
    private val remoteDataSource: InOpenAppRemoteDataSource,
    @Dispatcher(TaskDemoDispatchers.Io) val ioDispatcher: CoroutineDispatcher
) : InOpenAppRepository, NetworkResultParser {

    override fun dashboardStream(): Flow<DashboardData> {
        return localDataSource.dashboardDataFlow
            .filterNotNull()
    }

    override suspend fun refreshDashboard(): Result<DashboardData> {
        return when (val networkResult = remoteDataSource.dashboard()) {
            is NetworkResult.Success -> {
                if (networkResult.data?.statusCode == HttpURLConnection.HTTP_OK) {
                    val data = networkResult.data.let { response ->
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
                            overallUrlChart = response.data?.overallUrlChart
                        )
                    }
                    localDataSource.setDashboardData(data)
                    Result.Success(data)
                } else {
                    badResponse(networkResult)
                }
            }

            else -> parseErrorNetworkResult(networkResult)
        }
    }
}
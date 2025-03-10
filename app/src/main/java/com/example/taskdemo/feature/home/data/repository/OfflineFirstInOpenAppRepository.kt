package com.example.taskdemo.feature.home.data.repository

import com.example.taskdemo.commons.util.NetworkResult
import com.example.taskdemo.commons.util.NetworkResultParser
import com.example.taskdemo.commons.util.Result
import com.example.taskdemo.core.di.Dispatcher
import com.example.taskdemo.core.di.TaskDemoDispatchers
import com.example.taskdemo.feature.home.data.source.local.DashboardLocalDataSource
import com.example.taskdemo.feature.home.data.source.local.RefreshMetadataDataSource
import com.example.taskdemo.feature.home.data.source.local.entity.DashboardDataEntity
import com.example.taskdemo.feature.home.data.source.local.entity.toDashboardData
import com.example.taskdemo.feature.home.data.source.local.entity.toDashboardDataEntity
import com.example.taskdemo.feature.home.data.source.local.entity.toEntity
import com.example.taskdemo.feature.home.data.source.remote.DashboardRemoteDataSource
import com.example.taskdemo.feature.home.data.source.remote.dto.OpenAppLinkDto
import com.example.taskdemo.feature.home.data.source.remote.dto.toOpenAppLink
import com.example.taskdemo.feature.home.data.source.remote.model.dto.ChartKVPairDto
import com.example.taskdemo.feature.home.data.source.remote.model.dto.toChartKVPair
import com.example.taskdemo.feature.home.domain.model.DashboardData
import com.example.taskdemo.feature.home.domain.model.RefreshMetadata
import com.example.taskdemo.feature.home.domain.repository.InOpenAppRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineFirstInOpenAppRepository @Inject constructor(
    private val localDataSource: DashboardLocalDataSource,
    private val remoteDataSource: DashboardRemoteDataSource,
    private val refreshMetadataDataSource: RefreshMetadataDataSource,
    @Dispatcher(TaskDemoDispatchers.Io) val ioDispatcher: CoroutineDispatcher
) : InOpenAppRepository, NetworkResultParser {

    override fun dashboardStream(): Flow<DashboardData> {
        return localDataSource.dashboardStream()
            .filterNotNull()
            .map(DashboardDataEntity::toDashboardData)
    }

    override suspend fun refreshDashboard(): Result<DashboardData> {
        return withContext(ioDispatcher) {
            when (val networkResult = remoteDataSource.dashboard()) {
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
                                overallUrlChart = response.data?.overallUrlChart?.map(ChartKVPairDto::toChartKVPair) ?: emptyList(),
                            )
                        }
                        localDataSource.insertDashboardData(data.toDashboardDataEntity())
                        refreshMetadataDataSource.insertRefreshMetadata(
                            RefreshMetadata(
                                refreshKey = DASHBOARD_REFRESH_KEY,
                                lastRefreshedTime = System.currentTimeMillis()
                            ).toEntity()
                        )
                        Result.Success(data)
                    } else {
                        badResponse(networkResult)
                    }
                }

                else -> parseErrorNetworkResult(networkResult)
            }
        }
    }

    companion object {
        const val DASHBOARD_REFRESH_KEY = "refresh_dashboard"
    }

}
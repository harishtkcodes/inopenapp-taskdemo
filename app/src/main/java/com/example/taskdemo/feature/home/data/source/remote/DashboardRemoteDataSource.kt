package com.example.taskdemo.feature.home.data.source.remote

import com.example.taskdemo.commons.util.NetworkResult
import com.example.taskdemo.feature.home.data.source.remote.model.DashboardResponse

interface DashboardRemoteDataSource {

    suspend fun dashboard(): NetworkResult<DashboardResponse>

}
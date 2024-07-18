package com.example.taskdemo.feature.home.data.source.remote

import com.example.taskdemo.BuildConfig
import com.example.taskdemo.commons.util.NetworkResult
import com.example.taskdemo.core.data.source.remote.BaseRemoteDataSource
import com.example.taskdemo.core.util.NetworkMonitor
import com.example.taskdemo.feature.home.data.source.remote.model.DashboardResponse
import com.google.gson.Gson
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Lazy
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

private const val INOPEN_APP_BASE_URL = BuildConfig.API_URL

class NetworkInOpenAppRemoteDataSource @Inject constructor(
    gson: Gson,
    networkJson: Json,
    // We specifically request dagger.Lazy here, so that it's not instantiated from Dagger.
    okhttpCallFactory: Lazy<Call.Factory>,
    networkMonitor: NetworkMonitor,
) : InOpenAppRemoteDataSource, BaseRemoteDataSource(networkMonitor) {

    private val networkApi = Retrofit.Builder()
        .baseUrl(INOPEN_APP_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        // We use callFactory lambda here with dagger.Lazy<Call.Factory>
        // to prevent initializing OkHttp on the main thread.
        .callFactory { okhttpCallFactory.get().newCall(it) }
        .addConverterFactory(
            networkJson.asConverterFactory("application/json".toMediaType())
        )
        .build().create(InOpenAppApi::class.java)

    override suspend fun dashboard(): NetworkResult<DashboardResponse> {
        return safeApiCall { networkApi.dashboard() }
    }

}
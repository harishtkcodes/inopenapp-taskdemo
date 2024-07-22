package com.example.taskdemo.feature.home.data.source.remote

import com.example.taskdemo.feature.home.data.source.remote.model.DashboardResponse
import retrofit2.Response
import retrofit2.http.GET

interface InOpenAppApi {

    @GET("dashboardNew")
    suspend fun dashboard(): Response<DashboardResponse>
}

/*
token - eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjU5MjcsImlhdCI6MTY3NDU1MDQ1MH0.dCkW0ox8tbjJA2GgUx2UEwNlbTZ7Rr38PVFJevYcXFI
 */
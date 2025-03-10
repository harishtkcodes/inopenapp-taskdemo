package com.example.taskdemo.feature.home.di

import com.example.taskdemo.core.di.RepositorySource
import com.example.taskdemo.core.di.RepositorySources
import com.example.taskdemo.feature.home.data.repository.DefaultInOpenAppRepository
import com.example.taskdemo.feature.home.data.repository.FakeOpenInAppRepository
import com.example.taskdemo.feature.home.data.repository.OfflineFirstInOpenAppRepository
import com.example.taskdemo.feature.home.data.source.inmemory.InMemoryDashboardDataSource
import com.example.taskdemo.feature.home.data.source.local.DashboardLocalDataSource
import com.example.taskdemo.feature.home.data.source.local.RoomDashboardLocalDataSource
import com.example.taskdemo.feature.home.data.source.remote.DashboardRemoteDataSource
import com.example.taskdemo.feature.home.data.source.remote.NetworkDashboardRemoteDataSource
import com.example.taskdemo.feature.home.domain.repository.InOpenAppRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface InOpenAppRepositoryModule {

    @Binds
    @RepositorySource(RepositorySources.Default)
    fun bindsDefaultInOpenAppRepository(
        inOpenAppRepository: DefaultInOpenAppRepository
    ): InOpenAppRepository

    @Binds
    @RepositorySource(RepositorySources.Fake)
    fun bindsFakeInOpenAppRepository(
        inOpenAppRepository: FakeOpenInAppRepository
    ): InOpenAppRepository

    @Binds
    @RepositorySource(RepositorySources.OfflineFirst)
    fun bindsOfflineFirstInOpenAppRepository(
        inOpenAppRepository: OfflineFirstInOpenAppRepository
    ): InOpenAppRepository

    @Binds
    fun bindsInOpenAppRemoteDataSource(
        inOpenAppRemoteDataSource: NetworkDashboardRemoteDataSource
    ): DashboardRemoteDataSource

    @Binds
    fun bindsInOpenAppLocalDataSource(
        inOpenAppLocalDataSource: RoomDashboardLocalDataSource
    ): DashboardLocalDataSource

    companion object {
        @Provides
        @Singleton
        fun providesInMemoryDataSource(): InMemoryDashboardDataSource =
            InMemoryDashboardDataSource()
    }

}
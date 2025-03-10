package com.example.taskdemo.feature.home.di

import android.content.Context
import com.example.taskdemo.feature.home.data.source.local.AppDatabase
import com.example.taskdemo.feature.home.data.source.local.dao.DashboardDataDao
import com.example.taskdemo.feature.home.data.source.local.dao.RefreshMetadataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModule {

   companion object {
       @Singleton
       @Provides
       fun provideDashboardDataDao(appDatabase: AppDatabase): DashboardDataDao {
           return appDatabase.dashboardDataDao()
       }

       @Singleton
       @Provides
       fun provideRefreshMetadataDao(appDatabase: AppDatabase): RefreshMetadataDao {
           return appDatabase.refreshMetadataDao()
       }

       @Singleton
       @Provides
       fun provideDatabase(context: Context): AppDatabase {
           return AppDatabase.Factory().create(context)
       }
   }
}
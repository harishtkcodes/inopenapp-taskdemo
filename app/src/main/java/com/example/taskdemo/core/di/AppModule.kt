package com.example.taskdemo.core.di

import android.content.Context
import androidx.work.WorkManager
import com.example.taskdemo.BuildConfig
import com.example.taskdemo.commons.util.Util
import com.example.taskdemo.commons.util.gson.StringConverter
import com.example.taskdemo.commons.util.net.AndroidHeaderInterceptor
import com.example.taskdemo.commons.util.net.ForbiddenInterceptor
import com.example.taskdemo.commons.util.net.GuestUserInterceptor
import com.example.taskdemo.commons.util.net.JwtInterceptor
import com.example.taskdemo.commons.util.net.PlatformInterceptor
import com.example.taskdemo.commons.util.net.UserAgentInterceptor
import com.example.taskdemo.core.Env
import com.example.taskdemo.core.data.persistence.DefaultPersistentStore
import com.example.taskdemo.core.data.persistence.PersistentStore
import com.example.taskdemo.core.envForConfig
import com.example.taskdemo.core.util.JsonParser
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class RepositorySource(val repositorySource: RepositorySources)

enum class RepositorySources { Default, RemoteOnly, }

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /* Coroutine scope */
    @ApplicationCoroutineScope
    @Singleton
    @Provides
    fun provideApplicationScope(): CoroutineScope =
        Util.buildCoroutineScope(
            coroutineName = Util.APPLICATION_COROUTINE_NAME
        )
    /* END - Coroutine scope */

    @Provides
    @Singleton
    fun providePersistentStore(@ApplicationContext application: Context): PersistentStore
        = DefaultPersistentStore.getInstance(application)

    @Provides
    fun provideWorkManager(@ApplicationContext application: Context): WorkManager {
        return WorkManager.getInstance(application)
    }
}


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GsonParser

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationCoroutineScope

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WebService
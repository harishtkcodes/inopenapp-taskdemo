package com.workfast.ai.app.feature.tasks.di

import com.example.taskdemo.BuildConfig
import com.example.taskdemo.commons.util.gson.StringConverter
import com.example.taskdemo.commons.util.net.AndroidHeaderInterceptor
import com.example.taskdemo.commons.util.net.ForbiddenInterceptor
import com.example.taskdemo.commons.util.net.GuestUserInterceptor
import com.example.taskdemo.commons.util.net.JwtInterceptor
import com.example.taskdemo.commons.util.net.PlatformInterceptor
import com.example.taskdemo.commons.util.net.UserAgentInterceptor
import com.example.taskdemo.core.Env
import com.example.taskdemo.core.data.persistence.DefaultPersistentStore
import com.example.taskdemo.core.envForConfig
import com.example.taskdemo.eventbus.UnAuthorizedEvent
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkhttpCallFactory(
        persistentStore: DefaultPersistentStore,
    ): okhttp3.Call.Factory {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.MINUTES)

        okHttpClientBuilder.addInterceptor(UserAgentInterceptor())
        okHttpClientBuilder.addInterceptor(
            AndroidHeaderInterceptor(
                versionCode = BuildConfig.VERSION_CODE.toString(),
                versionName = BuildConfig.VERSION_NAME
            )
        )
        okHttpClientBuilder.addInterceptor(
            JwtInterceptor { persistentStore.deviceToken }
        )
        okHttpClientBuilder.addInterceptor(PlatformInterceptor())
        okHttpClientBuilder.addInterceptor(
            GuestUserInterceptor { persistentStore.fcmToken }
        )
        okHttpClientBuilder.addInterceptor(
            ForbiddenInterceptor { EventBus.getDefault().post(UnAuthorizedEvent(System.currentTimeMillis())) }
        )

        // Add delays to all api calls
        // ifDebug { okHttpClientBuilder.addInterceptor(DelayInterceptor(2_000, TimeUnit.MILLISECONDS)) }

        if (envForConfig(BuildConfig.ENV) == Env.DEV || BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttpClientBuilder.addInterceptor(httpLoggingInterceptor)
        }
        return okHttpClientBuilder.build()
    }

    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(String::class.java, StringConverter())
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }
}
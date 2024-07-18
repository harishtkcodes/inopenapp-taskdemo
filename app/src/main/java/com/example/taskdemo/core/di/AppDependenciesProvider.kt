package com.example.taskdemo.core.di

import android.app.Application
import com.example.taskdemo.commons.util.AppForegroundObserver
import com.example.taskdemo.core.data.persistence.DefaultPersistentStore
import com.example.taskdemo.core.data.persistence.PersistentStore

class AppDependenciesProvider(private val application: Application) : AppDependencies.Provider {
    override fun provideAppForegroundObserver(): AppForegroundObserver {
        return AppForegroundObserver()
    }

    override fun providePersistentStore(): PersistentStore {
        return DefaultPersistentStore.getInstance(application)
    }


}
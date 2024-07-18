package com.example.taskdemo.core.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val taskDemoDispatcher: TaskDemoDispatchers)

enum class TaskDemoDispatchers {
    Default, Io, Main
}
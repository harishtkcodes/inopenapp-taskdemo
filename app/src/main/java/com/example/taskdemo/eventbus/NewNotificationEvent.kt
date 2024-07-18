package com.example.taskdemo.eventbus

data class NewNotificationEvent(
    val hint: String,
    val timestamp: Long
)
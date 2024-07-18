package com.example.taskdemo.feature.home.presentation.util

import java.util.Calendar

enum class TimeOfDay {
    Morning, Afternoon, Evening, Night, Other;

    val greetingMessage: String
        get() = when (this) {
            Morning -> "Good Morning"
            Afternoon -> "Good Afternoon"
            Evening -> "Good Evening"
            Night -> "Good Night"
            Other -> "Hello"
        }

    val emoji: String
        get() = when (this) {
            Morning -> "🌤️"
            Afternoon -> "☀️"
            Evening -> "⛅️"
            Night -> "🌙"
            Other -> "🌕"
        }

    companion object {
        val current: TimeOfDay
            get() {
                val c = Calendar.getInstance()
                return when (c.get(Calendar.HOUR_OF_DAY)) {
                    in 6..11 -> Morning
                    in 12..15 -> Afternoon
                    in 16..20 -> Evening
                    in 21..23 -> Night
                    else -> Other
                }
            }
    }
}
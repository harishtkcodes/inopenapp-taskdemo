package com.example.taskdemo.feature.home.presentation.util

import kotlin.random.Random

private val randomEmojis: List<String> by lazy {
    listOf("⛱️", "🎯", "🎷", "🍰", "🍉", "🫧", "☄️", "🌈", "👟", "🧚🏻‍♀️", "🎈", "🎸", "🚀") /* size=13 */
}

private val randomDescriptions: List<String> by lazy {
    listOf(
        "Start your day with positivity and productivity.",
        "Keep up the good work and stay focused.",
        "Relax and unwind from the day’s work."
    )
}

fun getRandomEmoji(): String {
    val (min, max) = 0 to randomEmojis.size - 1
    val index = Random.Default.nextInt(min, max + 1)
    return randomEmojis[index]
}

fun getRandomDescription(): String {
    val (min, max) = 0 to randomDescriptions.size - 1
    val index = Random.Default.nextInt(min, max + 1)
    return randomDescriptions[index]
}
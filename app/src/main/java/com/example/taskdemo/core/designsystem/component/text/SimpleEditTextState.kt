package com.example.taskdemo.core.designsystem.component.text

import kotlinx.coroutines.flow.update

/**
 * We don't name as TextFieldState as it will clash with Jetpack Compose
 */
data class SimpleEditTextState(
    val initialValue: String,
    private val validator: (String) -> Boolean,
    private val errorFor: (String) -> String,
) : EditTextState(validator, errorFor) {
    init {
        text.update { initialValue }
    }
}
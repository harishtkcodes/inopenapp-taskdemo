package com.example.taskdemo.core.designsystem.component.text

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import timber.log.Timber

/**
 * A replica of Jetpack Compose's TextFieldState but with [MutableStateFlow].
 * !! We don't name as TextFieldState as it will clash with Jetpack Compose
 */
open class EditTextState(
    private val validator: (String) -> Boolean = { true },
    private val errorFor: (String) -> String = { "" },
) {
    val text = MutableStateFlow("")
    // was the TextField ever focused?
    val isFocusedDirty = MutableStateFlow(false)
    val isFocused = MutableStateFlow(false)
    private var displayErrors = MutableStateFlow(false)

    open val isValid: Boolean
        get() = validator(text.value)

    fun onFocusChange(focused: Boolean) {
        Timber.d("onFocusChange() called with: focused = [$focused]")
        isFocused.update { focused }
        if (focused) {
            isFocusedDirty.update { true }
        }
    }

    fun enableShowErrors(ignoreFocus: Boolean = false) {
        Timber.d("enableShowErrors() called with: ignoreFocus = [$ignoreFocus]")
        // only show errors if the text was at least once focused
        if (isFocusedDirty.value || ignoreFocus) {
            displayErrors.update { true }
        }
    }

    @Suppress("DEPRECATION")
    open fun getError(): Flow<String?> =
        showErrors().map {
            if (it) {
                errorFor(text.value)
            } else {
                null
            }
        }

    @Deprecated("use getError()")
    fun showErrors(): Flow<Boolean> =
        combine(
            displayErrors,
            isValid().map { it.not() },
            Boolean::and
        )
            .distinctUntilChanged()

    fun isValid(): Flow<Boolean> =
        text.map { validator(it) }

    fun reset() {
        text.value = ""
        isFocusedDirty.value = false
        isFocused.value = false
        displayErrors.value = false
    }

}
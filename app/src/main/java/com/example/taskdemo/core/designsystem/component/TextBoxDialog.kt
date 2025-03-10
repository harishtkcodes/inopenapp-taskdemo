package com.example.taskdemo.core.designsystem.component

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.taskdemo.R
import com.example.taskdemo.core.designsystem.component.text.EditTextState
import com.example.taskdemo.databinding.DialogTextBoxBinding
import com.example.taskdemo.fakeDisable
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class TextBoxDialog(
    private val context: Context,
    private val inputState: EditTextState,
    private val onSend: () -> Unit,
    private val hintText: String? = null,
    @DrawableRes private val endIconRes: Int = R.drawable.ic_send
) : BottomSheetDialog(context, R.style.ThemeOverlay_App_BottomSheetDialog_Cut) {

    private lateinit var binding: DialogTextBoxBinding

    private var initialValue: String = inputState.text.value

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogTextBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preInit()
        binding.init()
    }

    private fun DialogTextBoxBinding.init() {
        textInputEditText.hint = hintText ?: context.getString(R.string.type_something)
        textInputEditText.setText(initialValue)
        textInputEditText.setSelection(initialValue.length)
        textInputEditText.addTextChangedListener(
            afterTextChanged = { typed ->
                inputState.text.update { typed.toString().trim() }
                btnSend.fakeDisable(!inputState.isValid)

                /*val regex = Regex("@(?:[a-zA-Z0-9]*)?")
                val usernames = regex.toPattern().matcher(typed.toString()).let { matcher ->
                    val matches = mutableListOf<String>()
                    while (matcher.find()) { matches.add(matcher.group()) }
                    matches
                }*/
            }
        )

        textInputEditText.setOnFocusChangeListener { _, hasFocus ->
            inputState.onFocusChange(hasFocus)
        }

        inputState.getError().onEach { error ->
            textInputLayout.isErrorEnabled = !inputState.isValid
            textInputLayout.error = error
        }
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)

        btnSend.setIconResource(endIconRes)
        btnSend.fakeDisable(!inputState.isValid)
        btnSend.setOnClickListener {
            if (!inputState.isValid) {
                inputState.enableShowErrors(true)
            } else {
                onSend()
                dismiss()
            }
        }

        root.post {
            textInputEditText.showSoftInputOnFocus = true
            textInputEditText.requestFocus()
        }
    }

    private fun preInit() {
        // Disable cancel on touch outside
        val touchOutsideView =
            window?.decorView?.findViewById<View>(com.google.android.material.R.id.touch_outside)
        touchOutsideView?.setOnClickListener {
            val typedInput = inputState.text.value
            if (typedInput.isNotBlank() && typedInput != initialValue) {
                confirmBackPress { dismiss() }
            } else {
                dismiss()
            }
        }
    }

    private fun confirmBackPress(cont: () -> Unit) {
        val clickListener: DialogInterface.OnClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        cont()
                    }
                }
                dialog.dismiss()
            }
        MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialDialog)
            .setTitle(context.getString(R.string.hey_wait))
            .setMessage(context.getString(R.string.changes_will_be_lost))
            .setPositiveButton(context.getString(R.string.label_ok), clickListener)
            .setNegativeButton(context.getString(R.string.cancel), clickListener)
            .show()
    }
}
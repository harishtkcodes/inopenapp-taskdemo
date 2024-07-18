package com.example.taskdemo.feature.home.presentation.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.taskdemo.MainActivity
import com.example.taskdemo.R
import com.example.taskdemo.WindowSizeClass
import com.example.taskdemo.databinding.FragmentDashboardBinding
import com.example.taskdemo.doOnApplyWindowInsets
import com.example.taskdemo.feature.home.presentation.util.GreetingMessage
import com.example.taskdemo.feature.home.presentation.util.TimeOfDay
import com.example.taskdemo.feature.home.presentation.util.getRandomDescription
import com.example.taskdemo.feature.home.presentation.util.getRandomEmoji

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false).also {
            val topLevelDestinationPaddingPx = resources.getDimensionPixelSize(R.dimen.default_top_destination_padding)
            if (MainActivity.widthWindowSizeClass < WindowSizeClass.EXPANDED) {
                it.setPadding(0, 0, 0, topLevelDestinationPaddingPx)
            } else {
                it.setPadding(topLevelDestinationPaddingPx, 0, 0, 0)
            }
            it.doOnApplyWindowInsets { view, windowInsetsCompat, initialPadding ->
                val navbarInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars())
                val imeInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.ime())
                view.updatePadding(
                    bottom = if (imeInsets.bottom > 0) {
                        imeInsets.bottom.coerceAtLeast(navbarInsets.bottom)
                    } else {
                        imeInsets.bottom.coerceAtLeast(navbarInsets.bottom) + initialPadding.bottom
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDashboardBinding.bind(view)

        binding.bindState()
    }

    private fun FragmentDashboardBinding.bindState() {

        val timeOfDay = TimeOfDay.current
        val greetingMessage = GreetingMessage(
            title = timeOfDay.greetingMessage,
            description = getRandomDescription(requireContext()), /* "It's time to something awesome! 🤩" */
            leadingEmojiText = timeOfDay.emoji,
            trailingEmojiText = getRandomEmoji(requireContext()),
        )

        bindAppBar()
    }

    private fun FragmentDashboardBinding.bindAppBar() {
        appbarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val delta = (kotlin.math.abs(verticalOffset)) / appBarLayout.totalScrollRange.toFloat()
            // Timber.d("Offset: $verticalOffset total: ${appBarLayout.totalScrollRange} delta = $delta")

            (1.0F - delta).let { scale ->
                /*profileImageExpanded.scaleX = scale
                profileImageExpanded.scaleY = scale*/
            }

            if (delta > 0.91f) {
                // toolbarTitle.isVisible = true
                ivToolbarSettingsExpanded.visibility = View.GONE
                ivToolbarSettingsCollapsed.visibility = View.VISIBLE

                content.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.bg_solid,
                    requireContext().theme
                )
            } else {
                // toolbarTitle.isVisible = false
                ivToolbarSettingsExpanded.visibility = View.VISIBLE
                ivToolbarSettingsCollapsed.visibility = View.GONE

                content.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.bg_curved_top,
                    requireContext().theme
                )
            }
        }
    }
}
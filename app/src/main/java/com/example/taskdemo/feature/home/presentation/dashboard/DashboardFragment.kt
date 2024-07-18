package com.example.taskdemo.feature.home.presentation.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.taskdemo.MainActivity
import com.example.taskdemo.R
import com.example.taskdemo.WindowSizeClass
import com.example.taskdemo.databinding.FragmentDashboardBinding
import com.example.taskdemo.doOnApplyWindowInsets
import com.example.taskdemo.extensions.showToast
import com.example.taskdemo.feature.home.presentation.util.GreetingMessage
import com.example.taskdemo.feature.home.presentation.util.TimeOfDay
import com.example.taskdemo.feature.home.presentation.util.getRandomDescription
import com.example.taskdemo.feature.home.presentation.util.getRandomEmoji
import com.example.taskdemo.showSnack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()

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

        binding.bindState(
            uiModels = viewModel.uiModels,
            uiEvent = viewModel.uiEvent,
            uiAction = viewModel.accept
        )
    }

    private fun FragmentDashboardBinding.bindState(
        uiModels: StateFlow<List<DashboardUiModel>>,
        uiEvent: SharedFlow<DashboardUiEvent>,
        uiAction: (DashboardUiAction) -> Unit
    ) {
        uiEvent.onEach { event ->
            when (event) {
                is DashboardUiEvent.ShowSnack -> {
                    root.showSnack(event.message.asString(requireContext()), withBottomNavigation = true)
                }

                is DashboardUiEvent.ShowToast -> {
                    context?.showToast(event.message.asString(requireContext()))
                }

                // TODO: Handle other navigation related events
            }
        }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(viewLifecycleOwner.lifecycleScope)

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

    private fun initGlide(): RequestManager {
        return Glide.with(requireContext())
    }
}
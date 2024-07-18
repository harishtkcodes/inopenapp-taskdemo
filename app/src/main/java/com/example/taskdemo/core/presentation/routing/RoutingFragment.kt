package com.example.taskdemo.core.presentation.routing

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.taskdemo.R
import com.example.taskdemo.commons.util.loadstate.LoadState
import com.example.taskdemo.databinding.FragmentRoutingBinding
import com.example.taskdemo.defaultNavOptsBuilder
import com.example.taskdemo.extensions.ifDebug
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class RoutingFragment : Fragment(R.layout.fragment_routing) {

    private val viewModel: RoutingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        val binding = FragmentRoutingBinding.bind(view)

        binding.bindState(
            uiState = viewModel.uiState,
            onRetry = viewModel::retry
        )
    }

    private fun FragmentRoutingBinding.bindState(
        uiState: StateFlow<RoutingUiState>,
        onRetry: () -> Unit
    ) {
        uiState.map { it.loadState }
            .distinctUntilChangedBy { it.refresh }
            .onEach { loadState ->
                Timber.d("Routing: loading=$loadState")
                progressBar.isVisible = loadState.refresh is LoadState.Loading
                tvTitle.isVisible = loadState.refresh is LoadState.Error
                when (loadState.refresh) {
                    is LoadState.Error -> {
                        tvTitle.text = getString(R.string.something_went_wrong_try_later)
                    }
                    is LoadState.Loading -> { /* no-op */ }
                    is LoadState.NotLoading -> { /* no-op */ }
                }
            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        uiState.map { it.deepLink }
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { deepLinkUri ->
                Timber.d("Routing: deepLink=${deepLinkUri}")
                try {
                    findNavController().apply {
                        navigate(
                            deepLinkUri,
                            defaultNavOptsBuilder {
                                setPopUpTo(R.id.routing_page, inclusive = true, saveState = false)
                            }.build()
                        )
                    }
                    viewModel.reset()
                } catch (e: Exception) {
                    ifDebug { Timber.e(e) }
                    viewModel.setError(e)
                }
            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    /*private fun gotoStickyFooterWithBottomSheetFragment(
        view: View
    ) {
        resetTransitions()
        enterTransition = MaterialElevationScale(*//* growing= *//* false)
        exitTransition = MaterialElevationScale(*//* growing= *//* false)
        reenterTransition = MaterialElevationScale(*//* growing= *//* true)

        val args = bundleOf(
            "from" to "routing",
            "titleTransitionName" to view.transitionName
        )
        val extras = FragmentNavigatorExtras(
            view to view.transitionName
        )

        findNavController().navigateToStickyFooterBottomSheetScreen(
            args = args, null, extras = extras
        )
    }*/

    private fun resetTransitions() {
        enterTransition = null
        exitTransition = null
        reenterTransition = null
    }
}
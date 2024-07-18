package com.example.taskdemo.core.presentation.routing

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.example.taskdemo.R
import com.example.taskdemo.databinding.FragmentRoutingBinding

class RoutingFragment : Fragment(R.layout.fragment_routing) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        val binding = FragmentRoutingBinding.bind(view)

        binding.bindState()
    }

    private fun FragmentRoutingBinding.bindState() {
        // TODO: bind state
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
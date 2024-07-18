package com.example.taskdemo.feature.home.presentation.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.taskdemo.MainActivity
import com.example.taskdemo.R
import com.example.taskdemo.WindowSizeClass
import com.example.taskdemo.doOnApplyWindowInsets

class CampaignOverviewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_campaign_overview, container, false).also {
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
}
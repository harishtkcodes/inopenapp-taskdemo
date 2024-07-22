package com.example.taskdemo.feature.home.presentation.dashboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.taskdemo.MainActivity
import com.example.taskdemo.R
import com.example.taskdemo.WindowSizeClass
import com.example.taskdemo.commons.util.AnimationUtil.animationListener
import com.example.taskdemo.commons.util.loadstate.LoadState
import com.example.taskdemo.core.designsystem.component.TextBoxDialog
import com.example.taskdemo.core.designsystem.component.text.EditTextState
import com.example.taskdemo.databinding.FragmentDashboard2Binding
import com.example.taskdemo.databinding.FragmentDashboardBinding
import com.example.taskdemo.doOnApplyWindowInsets
import com.example.taskdemo.extensions.safeCall
import com.example.taskdemo.extensions.showToast
import com.example.taskdemo.feature.home.domain.model.DashboardData
import com.example.taskdemo.feature.home.domain.model.OpenAppLink
import com.example.taskdemo.setOnSingleClickListener
import com.example.taskdemo.showSnack
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.color.MaterialColors
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()

    private var currentTabPosition: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard2, container, false).also {
            val topLevelDestinationPaddingPx = resources.getDimensionPixelSize(R.dimen.default_top_destination_padding)
            if (MainActivity.widthWindowSizeClass < WindowSizeClass.EXPANDED) {
                it.setPadding(0, 0, 0, topLevelDestinationPaddingPx)
            } else {
                it.setPadding(topLevelDestinationPaddingPx, 0, 0, 0)
            }
            val scrollParent = it.findViewById<View>(R.id.scroll_parent)
            scrollParent.doOnApplyWindowInsets { view, windowInsetsCompat, initialPadding ->
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
        val binding = FragmentDashboard2Binding.bind(view)

        binding.bindState(
            uiState = viewModel.uiState,
            uiEvent = viewModel.uiEvent,
            uiAction = viewModel.accept
        )
    }

    private fun FragmentDashboardBinding.bindState(
        uiState: StateFlow<DashboardUiState>,
        uiEvent: SharedFlow<DashboardUiEvent>,
        uiAction: (DashboardUiAction) -> Unit
    ) {
        uiEvent.onEach { event ->
            when (event) {
                is DashboardUiEvent.ShowSnack -> {
                    root.showSnack(
                        event.message.asString(requireContext()),
                        withBottomNavigation = true
                    )
                }

                is DashboardUiEvent.ShowToast -> {
                    context?.showToast(event.message.asString(requireContext()))
                }

                // TODO: Handle other navigation related events
            }
        }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        uiState.map { it.loadState }
            .distinctUntilChangedBy { it.refresh }
            .onEach { refreshLoadState ->
                when (refreshLoadState.refresh) {
                    is LoadState.Error -> {
                        loadingShimmer.root.isVisible = false
                        errorContainer.isVisible = true
                        listView.isVisible = false

                        tvErrorTitle.text = getString(R.string.something_went_wrong_try_later)
                        btnRetry.setOnClickListener { uiAction(DashboardUiAction.Refresh) }
                    }

                    is LoadState.Loading -> {
                        loadingShimmer.root.isVisible = true
                        errorContainer.isVisible = false
                        listView.isVisible = false
                        loadingShimmer.root.startShimmer()
                    }

                    is LoadState.NotLoading -> {
                        loadingShimmer.root.isVisible = false
                        errorContainer.isVisible = false
                        listView.isVisible = true
                    }
                }
            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        val headerAdapter = DashboardHeaderAdapter(
            glide = initGlide(),
            onViewAnalyticsClick = {}
        )
        val tabsAdapter = DashboardTabsAdapter(
            glide = initGlide(),
            onItemClick = {},
            onCopyLink = {},
            onSearch = {},
            onTabSelected = {
                uiAction(DashboardUiAction.OnTabSelected(it))
            }
        )
        val footerAdapter = SupportFooterAdapter(
            onWhatsappSupportClick = {},
            onFaqClick = {}
        )

        uiState.map(DashboardUiState::toUiModels)
            .distinctUntilChanged()
            .onEach { models ->
                models.onEach { dashboardUiModel ->
                    when (dashboardUiModel) {
                        is DashboardUiModel.DashboardHeader -> {
                            headerAdapter.submitList(listOf(dashboardUiModel))
                        }

                        is DashboardUiModel.DashboardTabLayout -> {
                            tabsAdapter.submitList(listOf(dashboardUiModel))
                        }

                        is DashboardUiModel.DashboardSupportFooter -> {
                            footerAdapter.submitList(listOf(dashboardUiModel))
                        }

                        else -> {}
                    }
                }
            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        listView.adapter = ConcatAdapter(
            headerAdapter,
            tabsAdapter,
            footerAdapter
        )

        /*val linksAdapter = LinksAdapter(
            glide = initGlide(),
            onItemClick = {},
            onCopyLink = {},
        )
        listView.adapter = linksAdapter

        uiState.map { it.dashboardData }
            .map {
                mutableListOf<OpenAppLink>().apply {
                    it?.topLinks?.let(this::addAll)
                    it?.recentLinks?.let(this::addAll)
                    it?.favouriteLinks?.let(this::addAll)
                }
            }
            .distinctUntilChanged()
            .onEach {
                linksAdapter.submitList(it)
            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(viewLifecycleOwner.lifecycleScope)*/

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

        val settingsClickListener = View.OnClickListener {
            showAccessTokenDialog(
                inputState = viewModel.tokenInputState,
                onSubmit = {
                    viewModel.accept(DashboardUiAction.SubmitAccessToken)
                }
            )
        }
        ivToolbarSettingsCollapsed.setOnClickListener(settingsClickListener)
        ivToolbarSettingsExpanded.setOnClickListener(settingsClickListener)
    }

    private fun FragmentDashboard2Binding.bindState(
        uiState: StateFlow<DashboardUiState>,
        uiEvent: SharedFlow<DashboardUiEvent>,
        uiAction: (DashboardUiAction) -> Unit
    ) {
        uiEvent.onEach { event ->
            when (event) {
                is DashboardUiEvent.ShowSnack -> {
                    root.showSnack(
                        event.message.asString(requireContext()),
                        withBottomNavigation = true
                    )
                }

                is DashboardUiEvent.ShowToast -> {
                    context?.showToast(event.message.asString(requireContext()))
                }

                // TODO: Handle other navigation related events
            }
        }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        uiState.map { it.loadState }
            .distinctUntilChangedBy { it.refresh }
            .onEach { refreshLoadState ->
                when (refreshLoadState.refresh) {
                    is LoadState.Error -> {
                        loadingShimmer.root.isVisible = false
                        errorContainer.isVisible = true
                        dashboardContent.isVisible = false

                        tvErrorTitle.text = getString(R.string.something_went_wrong_try_later)
                        btnRetry.setOnClickListener { uiAction(DashboardUiAction.Refresh) }
                    }

                    is LoadState.Loading -> {
                        loadingShimmer.root.isVisible = true
                        errorContainer.isVisible = false
                        dashboardContent.isVisible = false
                        loadingShimmer.root.startShimmer()
                    }

                    is LoadState.NotLoading -> {
                        loadingShimmer.root.isVisible = false
                        errorContainer.isVisible = false
                        dashboardContent.isVisible = true
                    }
                }
            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        uiState.map(DashboardUiState::toUiModels)
            .distinctUntilChanged()
            .onEach { models ->
                models.onEach { dashboardUiModel ->
                    when (dashboardUiModel) {
                        is DashboardUiModel.DashboardHeader -> {
                            bindHeader(
                                dashboardUiModel,
                                glide = initGlide(),
                                onViewAnalyticsClick = {}
                            )
                        }

                        is DashboardUiModel.DashboardTabLayout -> {
                            bindTabsLayout(
                                dashboardUiModel,
                                glide = initGlide(),
                                onItemClick = {},
                                onCopyLink = {},
                                onSearch = {},
                                onTabSelected = {
                                    uiAction(DashboardUiAction.OnTabSelected(it))
                                }
                            )
                        }

                        is DashboardUiModel.DashboardSupportFooter -> {

                        }

                        else -> {}
                    }
                }
            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        /*val linksAdapter = LinksAdapter(
            glide = initGlide(),
            onItemClick = {},
            onCopyLink = {},
        )
        listView.adapter = linksAdapter

        uiState.map { it.dashboardData }
            .map {
                mutableListOf<OpenAppLink>().apply {
                    it?.topLinks?.let(this::addAll)
                    it?.recentLinks?.let(this::addAll)
                    it?.favouriteLinks?.let(this::addAll)
                }
            }
            .distinctUntilChanged()
            .onEach {
                linksAdapter.submitList(it)
            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(viewLifecycleOwner.lifecycleScope)*/

        bindAppBar()
    }

    private fun FragmentDashboard2Binding.bindHeader(
        data: DashboardUiModel.DashboardHeader,
        glide: RequestManager,
        onViewAnalyticsClick: () -> Unit
    ) {
        greetingTitle.text = data.greetingMessage.title
        greetingLeadingIcon.text = data.greetingMessage.leadingEmojiText

        val taskUser = data.userProfile
        tvTitle.text = taskUser.fullName
        tvSubtitle.isVisible = false

        val analyticsAdapter = SimpleAnalyticsAdapter {
            // TODO: handle clicks
        }.apply {
            submitList(data.dashboardData.asSimpleAnalyticsItems(root.context))
        }
        analyticsOverviewList.adapter = analyticsAdapter

        greetingLeadingIcon.clearAnimation()
        android.view.animation.AnimationUtils.loadAnimation(root.context, R.anim.pop_enter).apply {
            animationListener(
                onAnimationStart = {
                    greetingLeadingIcon.isVisible = true
                },
            )
        }.also(greetingLeadingIcon::startAnimation)

        btnViewAnalytics.setOnSingleClickListener { onViewAnalyticsClick() }

        // block:start:chart
        // TODO: draw the chart
        val values1 = arrayListOf<Entry>().apply {
            data.dashboardData.overallUrlChart?.onEachIndexed { index, chartKVPair ->
                add(Entry(index.toFloat(), chartKVPair.value.toFloat(), chartKVPair.key))
            }
        }


        // create a dataset and give it a type
        val set1 = LineDataSet(values1, "Clicks")

        val primaryColor = MaterialColors.getColor(
            root,
            com.google.android.material.R.attr.colorPrimary
        )

        set1.setAxisDependency(AxisDependency.RIGHT)
        set1.setColor(primaryColor)
        set1.setCircleColor(Color.WHITE)
        set1.setLineWidth(2f)
        set1.setCircleRadius(0f)
        set1.setFillAlpha(65)
        set1.setFillColor(primaryColor)
        set1.fillDrawable = ResourcesCompat.getDrawable(resources, R.drawable.bg_gradient_from_top, requireContext().theme)
        /*set1.setHighLightColor(
            MaterialColors.getColor(
                root,
                com.google.android.material.R.attr.colorError
            )
        )*/
        set1.setDrawCircleHole(false)
        set1.setDrawCircles(false)
        set1.setDrawFilled(true)
        set1.setDrawValues(false)
        lineChart.description = Description().apply { text = "" }
        lineChart.data = LineData(set1)
        // block:end:chart
    }

    private fun FragmentDashboard2Binding.bindTabsLayout(
        data: DashboardUiModel.DashboardTabLayout,
        glide: RequestManager,
        onItemClick: (OpenAppLink) -> Unit,
        onCopyLink: (String) -> Unit,
        onSearch: () -> Unit,
        onTabSelected: (DashboardTab) -> Unit
    ) {

        when (data.selectedTab) {
            DashboardTab.TopLinks -> {
                safeCall { tabLayout.getTabAt(TAB_TYPE_TOP_LINKS)?.select() }
            }
            DashboardTab.RecentLinks -> {
                safeCall { tabLayout.getTabAt(TAB_TYPE_RECENT_LINKS)?.select() }
            }
            DashboardTab.FavoriteLinks -> {
                safeCall { tabLayout.getTabAt(TAB_TYPE_FAV_LINKS)?.select() }
            }
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Noop.
                tab?.position?.let { tabPosition ->
                    currentTabPosition = tabPosition
                    when (tabPosition) {
                        TAB_TYPE_TOP_LINKS -> onTabSelected.invoke(
                            DashboardTab.TopLinks)
                        TAB_TYPE_RECENT_LINKS -> onTabSelected.invoke(
                            DashboardTab.RecentLinks)
                        TAB_TYPE_FAV_LINKS -> onTabSelected.invoke(
                            DashboardTab.FavoriteLinks)
                    }
                }
                /*if (defaultFriendsTab) {
                    defaultFriendsTab = false
                }*/
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Noop.
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Noop.
            }
        })

        val linksAdapter = LinksAdapter(
            glide = glide,
            onItemClick = onItemClick,
            onCopyLink = onCopyLink
        ).apply {
            submitList(data.getLinksForActiveTab())
        }
        linksListView.adapter = linksAdapter

        btnSearch.setOnSingleClickListener { onSearch() }
    }

    private fun FragmentDashboard2Binding.bindAppBar() {
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

        val settingsClickListener = View.OnClickListener {
            showAccessTokenDialog(
                inputState = viewModel.tokenInputState,
                onSubmit = {
                    viewModel.accept(DashboardUiAction.SubmitAccessToken)
                }
            )
        }
        ivToolbarSettingsCollapsed.setOnClickListener(settingsClickListener)
        ivToolbarSettingsExpanded.setOnClickListener(settingsClickListener)
    }

    private fun showAccessTokenDialog(
        inputState: EditTextState,
        onSubmit: () -> Unit,
    ) {
        TextBoxDialog(
            context = requireContext(),
            inputState = inputState,
            onSend = onSubmit,
            hintText = "Enter Access Token",
            endIconRes = R.drawable.baseline_done_24
        ).show()
    }

    private fun copyToClip(content: String) {
        val clipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as?
                ClipboardManager
        clipboardManager?.let { _ ->
            val clipData = ClipData.newPlainText("task-link", content)
            clipboardManager.setPrimaryClip(clipData)
            requireContext().showToast(getString(R.string.copied_to_clipboard), isShort = false)
        }
    }

    private fun initGlide(): RequestManager {
        return Glide.with(requireContext())
    }

    companion object {
        private const val TAB_TYPE_TOP_LINKS = 0
        private const val TAB_TYPE_RECENT_LINKS = 1
        private const val TAB_TYPE_FAV_LINKS = 2
    }
}

private fun DashboardData.asSimpleAnalyticsItems(context: Context): List<SimpleAnalyticsItem> {
    val resources = context.resources
    return listOf(
        SimpleAnalyticsItem(
            id = "total_clicks",
            title = totalClicks.toString(),
            subtitle = context.getString(R.string.total_clicks),
            icon = R.drawable.ic_clicks_outline,
            tint = ResourcesCompat.getColor(
                resources,
                R.color.purple_200,
                context.theme
            )
        ),
        SimpleAnalyticsItem(
            id = "top_location",
            title = topLocation ?: "N/A",
            subtitle = context.getString(R.string.top_location),
            icon = R.drawable.ic_location_pin_outline,
            tint = ResourcesCompat.getColor(
                resources,
                R.color.brand_blue,
                context.theme
            )
        ),
        SimpleAnalyticsItem(
            id = "top_source",
            title = topSource ?: "N/A",
            subtitle = context.getString(R.string.top_source),
            icon = R.drawable.ic_globe_outline,
            tint = ResourcesCompat.getColor(resources, R.color.red_200, context.theme)
        ),
    )
}
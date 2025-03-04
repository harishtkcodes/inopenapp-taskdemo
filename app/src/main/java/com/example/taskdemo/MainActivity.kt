package com.example.taskdemo

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.window.layout.WindowMetricsCalculator
import com.example.taskdemo.commons.util.AnimationUtil.animationListener
import com.example.taskdemo.core.designsystem.component.MyBottomAppBarState
import com.example.taskdemo.core.di.AppDependencies
import com.example.taskdemo.databinding.ActivityMainBinding
import com.example.taskdemo.eventbus.UnAuthorizedEvent
import com.example.taskdemo.extensions.Log.tag
import com.example.taskdemo.extensions.getDisplaySize
import com.example.taskdemo.extensions.launchWhenStarted
import com.example.taskdemo.extensions.showToast
import com.example.taskdemo.view.CircularEdgeCutout
import com.example.taskdemo.view.CustomBottomAppBarTopEdgeTreatment
import com.example.taskdemo.view.InvertedCradleBottomAppBarTopEdgeTreatment
import com.google.android.material.bottomappbar.BottomAppBarTopEdgeTreatment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.lang.ref.WeakReference

enum class WindowSizeClass { COMPACT, MEDIUM, EXPANDED }

private const val WINDOW_SOFT_INPUT_MODE_ADJUST_NOTHING = 0
private const val WINDOW_SOFT_INPUT_MODE_ADJUST_PAN = 1
private const val WINDOW_SOFT_INPUT_MODE_ADJUST_RESIZE = 2
private const val WINDOW_SOFT_INPUT_MODE_SYSTEM_BARS_ONLY = 3

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mainNavController: NavController
    val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.fragment_container)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    private val sharedViewModel: SharedViewModel by viewModels()

    private var offlineSnackBarWeakRef = WeakReference<Snackbar>(null)

    private var windowSoftInputMode: Int = WINDOW_SOFT_INPUT_MODE_ADJUST_RESIZE

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        /*lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { uiState ->
                    splashScreen.setKeepOnScreenCondition {
                        when (uiState) {
                            MainActivityUiState.Loading -> true
                            else -> false
                        }
                    }
                }
            }
        }*/

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        AppDependencies.displaySize = getDisplaySize()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWindowSizeClass(binding.root)
        setupObservers()
        setupLifecycleObservers()

        binding.apply {
            appBottomNavigationView.doOnApplyWindowInsets { view, windowInsetsCompat, initialPadding ->
                val navBarInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars())
                view.updatePadding(
                    bottom = navBarInsets.bottom + initialPadding.bottom
                )
            }

            val fabInsetPx = resources.getDimensionPixelSize(R.dimen.default_fab_inset)
            fabCreate.doOnApplyWindowInsets { view, windowInsetsCompat, _ ->
                val initialMarginBottom = fabCreate.marginBottom
                val navInset = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                /*view.updateMargins(
                    bottom = navInset + initialMarginBottom
                )*/
                // view.translationY = -(navInset.toFloat())
                // fabContainer.translationY = -(navInset.toFloat())
                binding.fabContainer.updateMargins(
                    bottom = -navInset
                )
            }
            customizeBottomNavigationViewShape(appBottomNavigationView)
        }


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            /*sdkAtLeastR {
                val imeHeight = windowInsets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                val navHeight = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                val statusBarHeight = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                Timber.d("Insets: imeHeight=$imeHeight navHeight=$navHeight statusBarHeight=$statusBarHeight")
                view.setPadding(
                    0,
                    statusBarHeight,
                    0,
                    imeHeight.coerceAtLeast(navHeight),
                )
                WindowInsetsCompat.CONSUMED
            } ?: windowInsets*/
            val imeHeight = windowInsets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val navHeight = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val statusBarHeight = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            Timber.d("Insets: imeHeight=$imeHeight navHeight=$navHeight statusBarHeight=$statusBarHeight")
            when (windowSoftInputMode) {
                WINDOW_SOFT_INPUT_MODE_ADJUST_RESIZE -> {
                    view.setPadding(
                        0,
                        statusBarHeight,
                        0,
                        imeHeight.coerceAtLeast(navHeight),
                    )
                }
                WINDOW_SOFT_INPUT_MODE_ADJUST_NOTHING -> {
                    view.setPadding(
                        0,
                        0,
                        0,
                        0,
                    )
                }
                else -> {
                    /* Default - WINDOW_SOFT_INPUT_MODE_SYSTEM_BARS_ONLY */
                    val isTopLevelDestination = sharedViewModel
                        .currentDestination.value in bottomBarDestinations
                    view.setPadding(
                        0,
                        statusBarHeight,
                        0,
                        if (isTopLevelDestination) 0 else navHeight,
                    )
                }
            }

            // Check if any child has consumed the windowInsets
            var consumed = false

            (view as ViewGroup).forEach { child ->
                // Dispatch the insets to the child
                val childResult = child.dispatchApplyWindowInsets(windowInsets.toWindowInsets())
                // If the child consumed the insets, record it.
                if (childResult.isConsumed) {
                    consumed = true
                }
            }

            Timber.tag("WindowInsets").d("consumed=$consumed")
            // If any of the children consumed the insets, return
            // an appropriate value
            if (consumed) WindowInsetsCompat.CONSUMED else windowInsets
        }

        setNavGraph(R.id.dashboard_graph)
    }

    private fun ActivityMainBinding.bindBottomBarState(
        bottomAppBarState: MyBottomAppBarState,
    ) {
        Timber.d("bindBottomBarState() called with: myBottomAppBarState = [$bottomAppBarState]")
        // val animDuration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        if (appBottomNavigationView.isVisible && bottomAppBarState.hidden) {
            // hide
            // appBottomNavigationView.isVisible = false
            AnimationUtils.loadAnimation(this@MainActivity, R.anim.slide_bottom).apply {
                duration = ENTER_ANIMATION_DURATION
                interpolator = LinearOutSlowInInterpolator()
                animationListener(onAnimationEnd = { appBottomNavigationView.isVisible = false })
            }.also(appBottomNavigationView::startAnimation)
        } else {
            // show
            appBottomNavigationView.isVisible = true

            AnimationUtils.loadAnimation(this@MainActivity, R.anim.slide_up).apply {
                duration = EXIT_ANIMATION_DURATION
                interpolator = FastOutLinearInInterpolator()/*this.setAnimationListener(
                    AnimationUtil.animationListener(
                        onAnimationEnd = {
                            bottomBarProfileImage.isVisible = true
                        }
                    )
                )*/
            }.also(appBottomNavigationView::startAnimation)
        }
    }

    private fun setNavGraph(
        @IdRes jumpToDestination: Int? = null,
        jumpToDestinationArgs: Bundle? = null,
    ) {
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
                    as NavHostFragment
        if (!this@MainActivity::mainNavController.isInitialized) {
            mainNavController = navHostFragment.navController
            mainNavController.addOnDestinationChangedListener(this@MainActivity)

            findViewById<BottomNavigationView>(R.id.app_bottom_navigation_view).apply {
                setupWithNavController(mainNavController)
                /*setOnItemSelectedListener {
                    NavigationUI.onNavDestinationSelected(it, mainNavController)
                    true
                }*/
                setOnItemReselectedListener { menuItem ->
                    Timber.d("Navbar: onItemReselected id=${menuItem.itemId}")
                    if (menuItem.itemId == R.id.dashboard_graph) {
                        Timber.d("Navbar: sending scroll signal..")
                        sharedViewModel.setHomeScrollToTop(true)
                    }

                    sharedViewModel.setBottomBarReselected(menuItem.itemId)
                }

            }
        }

        val inflater = navHostFragment.navController.navInflater
        val graph: NavGraph
        val startDestinationArgs = Bundle()

        if (jumpToDestination != null) {
            graph = inflater.inflate(R.navigation.nav_graph)
            graph.setStartDestination(jumpToDestination)
            jumpToDestinationArgs?.let(startDestinationArgs::putAll)
            mainNavController.setGraph(graph, startDestinationArgs)
        }
    }

    private fun setupObservers() {
        sharedViewModel.myBottomAppBarStateState.onEach { state ->
            binding.bindBottomBarState(state)
        }.launchWhenStarted(lifecycleOwner = this) // Kinda sus!

        sharedViewModel.isOffline.onEach { isOffline ->
            val msg = if (isOffline) {
                "Offline"
            } else {
                "Online"
            }
            Timber.d("Network: $msg")
            if (isOffline) {
                val bottomNavigationShown = !sharedViewModel.myBottomAppBarStateState.value.hidden
                offlineSnackBarWeakRef = WeakReference(
                    binding.root.showSnack(
                        message = getString(R.string.you_are_offline_showing_limited_content),
                        withBottomNavigation = bottomNavigationShown,
                        autoCancel = false,
                        showAction = true,
                        actionTitle = getString(R.string.label_ok),
                    )
                )
            } else {
                offlineSnackBarWeakRef.get()?.dismiss()
                offlineSnackBarWeakRef.clear()
            }
        }
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)

        /*tasksSharedViewModel.taskUpdatesCount
            .onEach { count ->
                if (count == 0) {
                    binding.oneappBottomNavigationView.removeBadge(R.id.tasks_graph)
                } else {
                    binding.oneappBottomNavigationView.getOrCreateBadge(R.id.tasks_graph).let { badgeDrawable ->
                        badgeDrawable.text = count.asNotificationBadge()
                    }
                }
            }
            .flowWithLifecycle(lifecycle).launchIn(lifecycleScope)*/
    }

    private fun setupLifecycleObservers() {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                EventBus.getDefault().register(this@MainActivity)

                // checkNotificationPermissions()
            }

            override fun onStop(owner: LifecycleOwner) {
                EventBus.getDefault().unregister(this@MainActivity)
            }
        })
    }

    private fun setupWindowSizeClass(root: ViewGroup) {
        root.addView(object : View(root.context) {
            override fun onConfigurationChanged(newConfig: Configuration?) {
                super.onConfigurationChanged(newConfig)
                calculateWindowSizeClasses()
            }
        })

        calculateWindowSizeClasses()
    }

    private fun calculateWindowSizeClasses() {
        val metrics = WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(this)

        val widthDp = metrics.bounds.width() /
                resources.displayMetrics.density
        val widthWindowSizeClass = when {
            widthDp < 600f -> WindowSizeClass.COMPACT
            widthDp < 840f -> WindowSizeClass.MEDIUM
            else -> WindowSizeClass.EXPANDED
        }

        val heightDp = metrics.bounds.height() /
                resources.displayMetrics.density
        val heightWindowSizeClass = when {
            heightDp < 480f -> WindowSizeClass.COMPACT
            heightDp < 900f -> WindowSizeClass.MEDIUM
            else -> WindowSizeClass.EXPANDED
        }

        Timber.d("Window: size width = $widthWindowSizeClass widthDp=$widthDp height = $heightWindowSizeClass heightDp=$heightDp")
        MainActivity.widthWindowSizeClass = widthWindowSizeClass
        MainActivity.heightWindowSizeClass = heightWindowSizeClass
    }

    private fun customizeBottomNavigationViewShape(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.post {
            val fabDiameter = resources.getDimension(R.dimen.fab_size)
            val fabMargin = resources.getDimension(R.dimen.fab_inset_large)
            val cradleVerticalOffset = resources.getDimension(R.dimen.cradle_vertical_offset)

            // val topEdgeTreatment = BottomAppBarTopEdgeTreatment(fabDiameter, fabMargin, cradleVerticalOffset)
            val topEdgeTreatment = InvertedCradleBottomAppBarTopEdgeTreatment(fabDiameter, 0f, cradleVerticalOffset)

            val shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setAllCorners(RoundedCornerTreatment())
                .setAllCornerSizes(resources.getDimension(R.dimen.bottom_nav_corner_radius))
                // .setTopEdge(CircularEdgeCutout(bottomNavigationView.width / 2f, resources.getDimension(R.dimen.fab_size), resources.getDimension(R.dimen.fab_corner_radius)))
                .setTopEdge(topEdgeTreatment)
                .build()

            val shapedDrawable = MaterialShapeDrawable(shapeAppearanceModel).apply {
                fillColor = ContextCompat.getColorStateList(this@MainActivity, R.color.bottom_nav_background_color)
                elevation = resources.getDimension(R.dimen.bottom_nav_elevation)
            }

            bottomNavigationView.background = shapedDrawable
            bottomNavigationView.clipToOutline = false
            bottomNavigationView.clipToPadding = false
            binding.fabContainer.isVisible = true
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?,
    ) {
        Timber.tag("Navigation").d(
            "onDestinationChanged: ${destination.id} ${
                listOf<String>()
            }"
        )
        sharedViewModel.setCurrentDestination(destinationId = destination.id)
        binding.fabCreate.isVisible = destination.id in bottomBarDestinations
        // Do something with [destination.id]
        /*when (destination.id) {
            *//* These have stciky footers so needs to be updated. *//*
            *//*R.id.search_page, R.id.search_area_page,  R.id.explore_page, R.id.profile_page*//* -> {
            windowSoftInputMode = WINDOW_SOFT_INPUT_MODE_ADJUST_NOTHING
            sdkBelowT {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            }
        }

            else -> {
                windowSoftInputMode = WINDOW_SOFT_INPUT_MODE_ADJUST_RESIZE
                sdkBelowT {
                    @Suppress("DEPRECATION")
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                }
            }
        }*/
        when (destination.id) {
            R.id.dashboard_overview_page, R.id.courses_overview_page, R.id.campaigns_overview_page, R.id.profile_overview_page -> {
                windowSoftInputMode = WINDOW_SOFT_INPUT_MODE_ADJUST_NOTHING
                sdkBelowT {
                    @Suppress("DEPRECATION") window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                }
            }

            else -> {
                windowSoftInputMode = WINDOW_SOFT_INPUT_MODE_ADJUST_RESIZE
                sdkBelowT {
                    @Suppress("DEPRECATION") window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUnAuthorizedEvent(e: UnAuthorizedEvent) {
        Timber.d("onUnAuthorizedEvent: $e")
        // TODO: gracefully logout the user.
        if (!AppDependencies.persistentStore?.deviceToken.isNullOrBlank()) {

            // userViewModel.logout()
            // googleSignInClient.signOut()

            showToast(getString(R.string.session_expired_login_again))/* Will be handled automatically. See [MainActivityViewModel]*//*if (this::mainNavController.isInitialized) {
                mainNavController.gotoOnboard()
            }*/
        }
    }

    /**
     * This will keep the splash for a pre-defined time.
     *
     * @param durationMillis - Time to keep
     */
    private fun keepSplash(durationMillis: Long = DEFAULT_SPLASH_DURATION) {
        val content = findViewById<View>(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                Thread.sleep(durationMillis)
                content.viewTreeObserver.removeOnPreDrawListener(this)
                return true
            }
        })
    }

    companion object {
        val Tag = tag(MainActivity::class.java)

        internal var widthWindowSizeClass: WindowSizeClass = WindowSizeClass.COMPACT
        internal var heightWindowSizeClass: WindowSizeClass = WindowSizeClass.COMPACT

        const val DEFAULT_SPLASH_DURATION: Long = 500

        const val THEME_MODE_AUTO = 0
        const val THEME_MODE_LIGHT = 1
        const val THEME_MODE_DARK = 2

        /** Milliseconds used for UI animations */
        const val ANIMATION_FAST_MILLIS = 50L
        const val ANIMATION_SLOW_MILLIS = 100L
        private const val IMMERSIVE_FLAG_TIMEOUT = 500L

        const val UI_RENDER_WAIT_TIME = 100L

        const val ENTER_ANIMATION_DURATION = 225L
        const val EXIT_ANIMATION_DURATION = 175L
    }
}
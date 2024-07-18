package com.example.taskdemo

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.window.layout.WindowMetricsCalculator
import com.example.taskdemo.core.di.AppDependencies
import com.example.taskdemo.databinding.ActivityMainBinding
import com.example.taskdemo.extensions.Log.tag
import com.example.taskdemo.extensions.getDisplaySize
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.lang.ref.WeakReference

enum class WindowSizeClass { COMPACT, MEDIUM, EXPANDED }

private const val WINDOW_SOFT_INPUT_MODE_ADJUST_NOTHING = 0
private const val WINDOW_SOFT_INPUT_MODE_ADJUST_PAN = 1
private const val WINDOW_SOFT_INPUT_MODE_ADJUST_RESIZE = 2

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

        setNavGraph(R.id.routing_page)
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
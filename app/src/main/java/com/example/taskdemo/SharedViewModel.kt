package com.example.taskdemo

import androidx.annotation.IdRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskdemo.core.designsystem.component.MyBottomAppBarState
import com.example.taskdemo.core.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _jumpToDestination = MutableSharedFlow<Int?>()
    val jumpToDestination = _jumpToDestination
        .filterNotNull()
        .shareIn(
            scope = viewModelScope,
            replay = 0,
            started = SharingStarted.WhileSubscribed(5_000)
        )

    fun jumpToPage(@IdRes destinationId: Int?) = viewModelScope.launch {
        if (currentDestination.value != destinationId) {
            _jumpToDestination.emit(destinationId)
        }
    }

    /* Navigation current destination */
    val currentDestination: MutableStateFlow<Int> = MutableStateFlow(-1)

    fun setCurrentDestination(destinationId: Int) {
        currentDestination.update { destinationId }
        setBottomAppBarHideRequest(false)
    }

    val _bottomAppBarHideRequest = MutableStateFlow(false)

    fun setBottomAppBarHideRequest(hide: Boolean) = viewModelScope.launch {
        _bottomAppBarHideRequest.update { hide }
    }

    val myBottomAppBarStateState = combine(
        currentDestination
            .map { destinationId ->
                destinationId !in bottomBarDestinations
            },
        _bottomAppBarHideRequest,
        Boolean::or
    )
        .distinctUntilChanged()
        .map { hidden ->
            MyBottomAppBarState(
                hidden = hidden
            )
        }
        /*.debounce {
            if (it.hidden) { 0 } else { 150 }
        }*/
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MyBottomAppBarState.Default
        )

    /* Top level destination navigation Icon click signal */
    private val _navigationIconClickSignal = MutableStateFlow(false)
    val navigationIconClickSignal = _navigationIconClickSignal.asStateFlow()

    fun setNavigationIconClickSignal(clicked: Boolean) {
        _navigationIconClickSignal.update { clicked }
    }

    /* Home feeds scroll to top signal */
    private val _scrollToTopSignal = MutableStateFlow(false)
    val scrollToTopSignal = _scrollToTopSignal.asStateFlow()

    fun setHomeScrollToTop(scrollToTop: Boolean) {
        _scrollToTopSignal.update { scrollToTop }
    }

    private val _bottomBarReSelectSignal = MutableSharedFlow<Int>(replay = 0)
    /**
     * Used to get a callback on bottom bar item reselect
     */
    val bottomBarReselectSignal = _bottomBarReSelectSignal.asSharedFlow()

    fun setBottomBarReselected(id: Int) {
        Timber.d("Reselect: sending $id..")
        viewModelScope.launch { _bottomBarReSelectSignal.emit(id) }
    }

    /* Connection Listener */
    val isOffline =
        combine(
            networkMonitor.isOnline
                .map(Boolean::not),
            currentDestination
                .map { destinationId ->
                    destinationId !in noOfflineAlertDestinations
                },
            Boolean::and
        )
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false
            )
}

private val noOfflineAlertDestinations: List<Int> =
    listOf(
        R.id.routing_page
    )

val bottomBarDestinations: List<Int> =
    listOf(
        R.id.dashboard_graph,
        R.id.dashboard_overview_page,
        R.id.courses_graph,
        R.id.courses_overview_page,
        R.id.campaigns_graph,
        R.id.campaigns_overview_page,
        R.id.profile_graph,
        R.id.profile_overview_page,
    )
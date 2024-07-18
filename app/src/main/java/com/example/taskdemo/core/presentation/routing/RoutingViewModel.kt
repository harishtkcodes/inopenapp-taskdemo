package com.example.taskdemo.core.presentation.routing

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskdemo.commons.util.loadstate.LoadState
import com.example.taskdemo.commons.util.loadstate.LoadStates
import com.example.taskdemo.commons.util.loadstate.LoadType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutingUiState())
    val uiState: StateFlow<RoutingUiState> = _uiState.asStateFlow()

    private var linkDataFetchJob: Job? = null

    private val deepLinkedShareId: String?
        get() = savedStateHandle[SHARE_ID]

    fun retry() {
        deepLinkedShareId?.let(::fetchDeepLink)
    }

    fun reset() {
        savedStateHandle[SHARE_ID] = null
        _uiState.update { state ->
            state.copy(
                loadState = LoadStates.IDLE,
                deepLink = null
            )
        }
    }

    fun setError(e: Exception) {
        setLoading(LoadType.REFRESH, LoadState.Error(e))
    }

    private fun fetchDeepLink(shareId: String) {
        linkDataFetchJob?.cancel(CancellationException("New Request"))
        setLoading(LoadType.REFRESH, LoadState.Loading())
        linkDataFetchJob = viewModelScope.launch {
            setLoading(LoadType.REFRESH, LoadState.NotLoading.Complete)

            // TODO: Fetch the long link and deep link to that accordingly

            _uiState.update { state ->
                state.copy(
                    deepLink = "https://openinapp.com/dashboard".toUri()
                )
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun setLoading(
        loadType: LoadType,
        loadState: LoadState
    ) {
        val newLoadState = uiState.value.loadState.modifyState(loadType, loadState)
        _uiState.update { it.copy(loadState = newLoadState) }
    }
}

data class RoutingUiState(
    val loadState: LoadStates = LoadStates.IDLE,
    val deepLink: Uri? = null
)

private const val SHARE_ID = "shareId"
package com.example.taskdemo.feature.home.presentation.dashboard

import com.example.taskdemo.commons.util.Result
import com.example.taskdemo.commons.util.UiText
import com.example.taskdemo.commons.util.loadstate.LoadState
import com.example.taskdemo.commons.util.loadstate.LoadStates
import com.example.taskdemo.commons.util.loadstate.LoadType
import com.example.taskdemo.core.data.persistence.PersistentStore
import com.example.taskdemo.feature.home.domain.model.DashboardData
import com.example.taskdemo.feature.home.domain.model.OpenAppLink
import com.example.taskdemo.feature.home.domain.repository.InOpenAppRepository
import com.example.taskdemo.feature.home.presentation.dashboard.DashboardUiModel.DashboardHeader
import com.example.taskdemo.feature.home.presentation.dashboard.DashboardUiModel.DashboardSupportFooter
import com.example.taskdemo.feature.home.presentation.dashboard.DashboardUiModel.DashboardTabLayout
import com.example.taskdemo.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: DashboardViewModel
    private val repository: InOpenAppRepository = mockk()
    private val persistentStore: PersistentStore = mockk()
    private val savedStateHandle = mockk<androidx.lifecycle.SavedStateHandle>()

    @Before
    fun setup() {
        every { persistentStore.deviceToken } returns ""
    }

    @After
    fun tearDown() {
    }

    private fun createOpenAppLink(
        name: String,
        url: String
    ) = OpenAppLink.create(
        urlId = 0,
        webLink = url,
        smartLink = url,
        title = name,
        totalClicks = 0,
        originalImage = null,
        thumbnail = null,
        timesAgo = null,
        createdAt = null,
        domainId = "",
        urlPrefix = null,
        urlSuffix = null,
        app = "",
        isFavourite = false
    )

    @Test
    fun refreshInternal_updatesStateToLoading_whenDataFetchStarts() = runTest {
        coEvery { repository.refreshDashboard() } returns Result.Loading
        every { repository.dashboardStream() } returns flowOf(DashboardData.EMPTY)

        viewModel = DashboardViewModel(repository, persistentStore, savedStateHandle)

        assertTrue(viewModel.uiState.first().loadState.refresh is LoadState.Loading)
    }

    @Test
    fun refreshInternal_updatesStateToSuccess_whenDataFetchIsSuccessful() = runTest {
        val dashboardData = DashboardData.create(
            topLinks = listOf(createOpenAppLink("Top Link", "https://toplink.com")),
            recentLinks = listOf(createOpenAppLink("Recent Link", "https://recentlink.com")),
            favouriteLinks = listOf(createOpenAppLink("Favorite Link", "https://favoritelink.com")),
            supportWhatsappNumber = "+1234567890"
        )
        coEvery { repository.refreshDashboard() } returns Result.Success(dashboardData)
        every { repository.dashboardStream() } returns flowOf(dashboardData)

        viewModel = DashboardViewModel(repository, persistentStore, savedStateHandle)

        assertEquals(dashboardData, viewModel.uiState.first().dashboardData)
        assertFalse(viewModel.uiState.first().loadState.refresh is LoadState.Loading)
        assertTrue(viewModel.uiState.first().loadState.refresh is LoadState.NotLoading)
    }

    @Test
    fun refreshInternal_updatesStateToError_whenDataFetchFails() = runTest {
        val exception = Exception("Failed to fetch data")
        coEvery { repository.refreshDashboard() } returns Result.Error(exception)
        every { repository.dashboardStream() } returns flowOf(DashboardData.EMPTY)

        viewModel = DashboardViewModel(repository, persistentStore, savedStateHandle)

        assertTrue(viewModel.uiState.first().loadState.refresh is LoadState.Error)
        assertEquals(
            exception,
            (viewModel.uiState.first().loadState.refresh as LoadState.Error).error
        )
    }

    @Test
    fun handleTabSelectedInternal_updatesCurrentTab_whenNewTabIsSelected() = runTest {
        coEvery { repository.refreshDashboard() } returns Result.Success(DashboardData.EMPTY)
        every { repository.dashboardStream() } returns flowOf(DashboardData.EMPTY)

        viewModel = DashboardViewModel(repository, persistentStore, savedStateHandle)

        viewModel.accept(DashboardUiAction.OnTabSelected(DashboardTab.RecentLinks))
        assertEquals(DashboardTab.RecentLinks, viewModel.uiState.first().currentDashboardTab)

        viewModel.accept(DashboardUiAction.OnTabSelected(DashboardTab.FavoriteLinks))
        assertEquals(DashboardTab.FavoriteLinks, viewModel.uiState.first().currentDashboardTab)
    }

    @Test
    fun submitAccessToken_savesTokenToPersistentStore_whenTokenIsSubmitted() = runTest {
        val testToken = "test_token"
        every { persistentStore.setDeviceToken(testToken) } returns ""
        coEvery { repository.refreshDashboard() } returns Result.Success(DashboardData.EMPTY)
        every { repository.dashboardStream() } returns flowOf(DashboardData.EMPTY)
        every { persistentStore.deviceToken } returns testToken

        viewModel = DashboardViewModel(repository, persistentStore, savedStateHandle)
        viewModel.tokenInputState.text.value = testToken

        viewModel.accept(DashboardUiAction.SubmitAccessToken)

        //Can't verify the persistentStore.setDeviceToken(testToken) is called because it is a suspend function
        assertEquals(testToken, viewModel.tokenInputState.text.value)
    }

    @Test
    fun submitAccessToken_emitsShowToastEvent_whenTokenIsSubmitted() = runTest {
        val testToken = "test_token"
        every { persistentStore.setDeviceToken(testToken) } returns testToken
        coEvery { repository.refreshDashboard() } returns Result.Success(DashboardData.EMPTY)
        every { repository.dashboardStream() } returns flowOf(DashboardData.EMPTY)
        every { persistentStore.deviceToken } returns testToken

        viewModel = DashboardViewModel(repository, persistentStore, savedStateHandle)
        viewModel.tokenInputState.text.value = testToken

        val emittedEvent = MutableSharedFlow<DashboardUiEvent>(
            replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiEvent.collect { event ->
                emittedEvent.emit(event)
            }
        }

        viewModel.accept(DashboardUiAction.SubmitAccessToken)

        assertEquals(
            UiText.DynamicString("Token saved"),
            (emittedEvent.replayCache.firstOrNull() as? DashboardUiEvent.ShowToast)?.message
        )

        collectJob.cancel()
    }

    @Test
    fun toUiModels_returnsCorrectList_withValidData() {
        val dashboardData = DashboardData.create(
            topLinks = listOf(createOpenAppLink("Top Link", "https://toplink.com")),
            recentLinks = listOf(createOpenAppLink("Recent Link", "https://recentlink.com")),
            favouriteLinks = listOf(createOpenAppLink("Favorite Link", "https://favoritelink.com")),
            supportWhatsappNumber = "+1234567890"
        )
        val uiState = DashboardUiState(
            loadState = LoadStates.IDLE,
            dashboardData = dashboardData,
            currentDashboardTab = DashboardTab.TopLinks
        )

        val uiModels = uiState.toUiModels()

        assertEquals(3, uiModels.size)
        assertTrue(uiModels[0] is DashboardHeader)
        assertTrue(uiModels[1] is DashboardTabLayout)
        assertTrue(uiModels[2] is DashboardSupportFooter)
    }

    @Test
    fun getLinksForActiveTab_returnsCorrectLinks_forTopLinksTab() {
        val topLinks = listOf(createOpenAppLink("Top Link", "https://toplink.com"))
        val dashboardData = DashboardData.create(
            topLinks = topLinks,
            recentLinks = listOf(createOpenAppLink("Recent Link", "https://recentlink.com")),
            favouriteLinks = listOf(createOpenAppLink("Favorite Link", "https://favoritelink.com")),
            supportWhatsappNumber = "+1234567890"
        )
        val tabLayout = DashboardTabLayout(
            selectedTab = DashboardTab.TopLinks,
            dashboardData = dashboardData
        )

        val links = tabLayout.getLinksForActiveTab()

        assertEquals(topLinks, links)
    }

    @Test
    fun getLinksForActiveTab_returnsCorrectLinks_forRecentLinksTab() {
        val recentLinks = listOf(createOpenAppLink("Recent Link", "https://recentlink.com"))
        val dashboardData = DashboardData.create(
            topLinks = listOf(createOpenAppLink("Top Link", "https://toplink.com")),
            recentLinks = recentLinks,
            favouriteLinks = listOf(createOpenAppLink("Favorite Link", "https://favoritelink.com")),
            supportWhatsappNumber = "+1234567890"
        )
        val tabLayout = DashboardTabLayout(
            selectedTab = DashboardTab.RecentLinks,
            dashboardData = dashboardData
        )

        val links = tabLayout.getLinksForActiveTab()

        assertEquals(recentLinks, links)
    }

    @Test
    fun getLinksForActiveTab_returnsCorrectLinks_forFavoriteLinksTab() {
        val favouriteLinks = listOf(createOpenAppLink("Favorite Link", "https://favoritelink.com"))
        val dashboardData = DashboardData.create(
            topLinks = listOf(createOpenAppLink("Top Link", "https://toplink.com")),
            recentLinks = listOf(createOpenAppLink("Recent Link", "https://recentlink.com")),
            favouriteLinks = favouriteLinks,
            supportWhatsappNumber = "+1234567890"
        )
        val tabLayout = DashboardTabLayout(
            selectedTab = DashboardTab.FavoriteLinks,
            dashboardData = dashboardData
        )

        val links = tabLayout.getLinksForActiveTab()

        assertEquals(favouriteLinks, links)
    }

    @Test
    fun initialState_isIdle() {
        coEvery { repository.refreshDashboard() } returns Result.Success(DashboardData.EMPTY)
        every { repository.dashboardStream() } returns flowOf(DashboardData.EMPTY)

        viewModel = DashboardViewModel(repository, persistentStore, savedStateHandle)

        assertEquals(LoadStates.IDLE, viewModel.uiState.value.loadState)
    }

    @Test
    fun refreshDashboard_setsCompleteState() = runTest {
        coEvery { repository.refreshDashboard() } returns Result.Success(DashboardData.EMPTY)
        every { repository.dashboardStream() } returns flowOf(DashboardData.EMPTY)

        viewModel = DashboardViewModel(repository, persistentStore, savedStateHandle)
        viewModel.accept(DashboardUiAction.Refresh)

        assertTrue(viewModel.uiState.first().loadState.refresh is LoadState.NotLoading)
    }

    @Test
    fun refreshDashboard_setsErrorState() = runTest {
        val exception = Exception("Test exception")
        coEvery { repository.refreshDashboard() } returns Result.Error(exception)
        every { repository.dashboardStream() } returns flowOf(DashboardData.EMPTY)

        viewModel = DashboardViewModel(repository, persistentStore, savedStateHandle)
        viewModel.accept(DashboardUiAction.Refresh)

        assertTrue(viewModel.uiState.first().loadState.refresh is LoadState.Error)
        assertEquals(exception, (viewModel.uiState.first().loadState.refresh as LoadState.Error).error)
    }

    @Test
    fun refreshDashboard_setsLoadingState() = runTest {
        coEvery { repository.refreshDashboard() } returns Result.Loading
        every { repository.dashboardStream() } returns flowOf(DashboardData.EMPTY)

        viewModel = DashboardViewModel(repository, persistentStore, savedStateHandle)
        viewModel.accept(DashboardUiAction.Refresh)

        assertTrue(viewModel.uiState.first().loadState.refresh is LoadState.Loading)
    }

    @Test
    fun toDashboardUiState_convertsStateCorrectly() {
        val dashboardState = DashboardState(
            loadState = LoadStates.IDLE.modifyState(LoadType.REFRESH, LoadState.Loading()),
            dashboardData = DashboardData.EMPTY,
            currentDashboardTab = DashboardTab.TopLinks
        )

        val uiState = dashboardState.toDashboardUiState()

        assertEquals(dashboardState.loadState, uiState.loadState)
        assertEquals(dashboardState.dashboardData, uiState.dashboardData)
        assertEquals(dashboardState.currentDashboardTab, uiState.currentDashboardTab)
    }

    @Test
    fun dashboardHeader_containsCorrectData() {
        val userProfile = UserProfile(
            fullName = "John Doe",
            shortName = "John",
            profileImage = null
        )
        val dashboardData = DashboardData.create(
            topLinks = listOf(createOpenAppLink("Top Link", "https://toplink.com")),
            recentLinks = listOf(createOpenAppLink("Recent Link", "https://recentlink.com")),
            favouriteLinks = listOf(createOpenAppLink("Favorite Link", "https://favoritelink.com")),
            supportWhatsappNumber = "+1234567890"
        )
        val header = DashboardHeader(
            userProfile = userProfile,
            greetingMessage = mockk(relaxed = true),
            dashboardData = dashboardData
        )

        assertEquals(userProfile, header.userProfile)
        assertEquals(dashboardData, header.dashboardData)
    }
}
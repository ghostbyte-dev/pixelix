package com.daniebeler.pfpixelix

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarColors
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.FloatingToolbarScrollBehavior
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.ui.composables.profile.own_profile.AccountSwitchBottomSheet
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.PreferencesComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.ReverseModalNavigationDrawer
import com.daniebeler.pfpixelix.ui.events.GlobalNavigationEvent
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.ui.navigation.appEntryProvider
import com.daniebeler.pfpixelix.ui.navigation.rememberAppNavigationState
import com.daniebeler.pfpixelix.ui.theme.PixelixTheme
import com.daniebeler.pfpixelix.utils.end
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.bookmark
import pixelix.app.generated.resources.default_avatar
import pixelix.app.generated.resources.home
import pixelix.app.generated.resources.house
import pixelix.app.generated.resources.house_strong
import pixelix.app.generated.resources.notifications
import pixelix.app.generated.resources.notifications_strong
import pixelix.app.generated.resources.profile
import pixelix.app.generated.resources.search
import pixelix.app.generated.resources.search_strong
import kotlin.time.Duration.Companion.milliseconds

val LocalSnackbarPresenter = compositionLocalOf<(String) -> Unit> {
    error("No LocalSnackbarPresenter provided")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App(
    appComponent: AppComponent,
    browserNavigation: @Composable (Destination) -> Unit = {},
    exitApp: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    DisposableEffect(uriHandler) {
        val systemUrlHandler = appComponent.systemUrlHandler
        systemUrlHandler.uriHandler = uriHandler
        onDispose {
            systemUrlHandler.uriHandler = null
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (appComponent.systemUrlHandler.isAuthInProgress) {
                    coroutineScope.launch {
                        delay(200.milliseconds)
                        appComponent.systemUrlHandler.cancelWaiting()
                    }
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    CompositionLocalProvider(
        LocalAppComponent provides appComponent
    ) {
        PixelixTheme {
            var activeUser by remember { mutableStateOf<String?>("unknown") }
            LaunchedEffect(Unit) {
                appComponent.preferences.preload()
                val authService = appComponent.authService
                authService.openSessionIfExist()

                appComponent.notificationBadgeRefresher.start()

                authService.activeUser.collect {
                    activeUser = it
                }
            }
            if (activeUser == "unknown") return@PixelixTheme

            key(activeUser) {
                val scope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                var showAccountSwitchBottomSheet by remember { mutableStateOf(false) }
                val startDestination =
                    if (activeUser == null) Destination.FirstLogin else Destination.HomeTabFeeds
                val navigationState = rememberAppNavigationState(startDestination)
                val navController = remember(navigationState, exitApp) {
                    AppNavigator(navigationState, exitApp)
                }

                val snackbarHostState = remember { SnackbarHostState() }
                val snackBarPresenter: (String) -> Unit = { msg ->
                    scope.launch {
                        snackbarHostState.showSnackbar(msg)
                    }
                }

                //Note that wrapping something in key
                // won't actually clean up any ViewModel instances associated with destinations -
                // they'll continue to exist and run for the entire lifetime of the containing
                // Activity/Fragment because you didn't actually destroy them properly,
                // you just dropped any access to them
                LaunchedEffect(activeUser) {
                    navController.clearBackStack(Destination.HomeTabFeeds)
                    navController.clearBackStack(Destination.HomeTabSearch)
                    navController.clearBackStack(Destination.HomeTabNewPost)
                    navController.clearBackStack(Destination.HomeTabNotifications)
                    navController.clearBackStack(Destination.HomeTabOwnProfile)
                }

                LaunchedEffect(appComponent.globalNavigator) {
                    appComponent.globalNavigator.navigationEvents.collect { event ->
                        when (event) {
                            is GlobalNavigationEvent.NavigateToLogin -> {
                                navController.clearAndNavigate(Destination.FirstLogin)
                            }
                        }
                    }
                }

                CompositionLocalProvider(
                    LocalSnackbarPresenter provides snackBarPresenter
                ) {
                    ReverseModalNavigationDrawer(
                        gesturesEnabled = drawerState.isOpen,
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(
                                drawerState = drawerState,
                                drawerShape = shapes.extraLarge.end(0.dp),
                            ) {
                                PreferencesComposable(navController, drawerState, {
                                    scope.launch {
                                        drawerState.close()
                                    }
                                })
                            }
                        }) {
                        val scrollBehaviorBottom =
                            FloatingToolbarDefaults.exitAlwaysScrollBehavior(exitDirection = FloatingToolbarExitDirection.Bottom)
                        Scaffold(
                            contentWindowInsets = WindowInsets(0),
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            modifier = Modifier.nestedScroll(scrollBehaviorBottom)
                        ) { paddingValues ->
                            Box(Modifier.fillMaxSize().padding(paddingValues)) {
                                browserNavigation(navigationState.currentDestination)
                                NavDisplay(
                                    entries = navigationState.decoratedEntries(
                                        appEntryProvider(
                                            navController,
                                            { scope.launch { drawerState.open() } },
                                            exitApp,
                                        )
                                    ),
                                    onBack = navController::popBackStack,
                                    sceneStrategies = listOf(
                                        remember { DialogSceneStrategy() },
                                        remember { SinglePaneSceneStrategy() },
                                    ),
                                    modifier = Modifier.fillMaxSize(),
                                )

                                val currentDestination = navigationState.currentDestination
                                val showBottomBar = currentDestination == Destination.Feeds ||
                                    currentDestination is Destination.Search ||
                                    currentDestination == Destination.Notifications ||
                                    currentDestination == Destination.OwnProfile ||
                                    currentDestination in HomeTab.entries.map { it.destination }

                                if (showBottomBar) {
                                    Box(
                                        modifier = Modifier.align(Alignment.BottomCenter)
                                    ) {
                                        BottomBarFloating(
                                            navController, navigationState.currentDestination,
                                            navigationState.currentTopLevel, scrollBehaviorBottom
                                        )

                                    }
                                }
                            }
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    appComponent.systemFileShare.shareFilesRequests.collect { uris ->
                        if (activeUser != null) {
                            navController.navigate(
                                Destination.NewPost(uris.map { it.toString() })
                            )
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    appComponent.accountIntentHandler.pendingAccount.collect { accountPair ->
                        if (accountPair.first.isNotEmpty() && accountPair.second.isNotEmpty()) {
                            navController.navigate(
                                Destination.Profile(accountPair.first, accountPair.second)
                            )
                        }
                    }
                }

                if (showAccountSwitchBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showAccountSwitchBottomSheet = false
                        }, sheetState = sheetState
                    ) {
                        AccountSwitchBottomSheet(
                            navController = navController,
                            closeBottomSheet = { showAccountSwitchBottomSheet = false },
                            null
                        )
                    }
                }
            }
        }
    }
}

private enum class HomeTab(
    val destination: Destination,
    val icon: DrawableResource,
    val activeIcon: DrawableResource,
    val label: StringResource
) {
    Feeds(
        Destination.HomeTabFeeds, Res.drawable.house, Res.drawable.house_strong, Res.string.home
    ),
    Search(
        Destination.HomeTabSearch,
        Res.drawable.search,
        Res.drawable.search_strong,
        Res.string.search
    ),
    Notifications(
        Destination.HomeTabNotifications,
        Res.drawable.notifications,
        Res.drawable.notifications_strong,
        Res.string.notifications
    ),
    OwnProfile(
        Destination.HomeTabOwnProfile,
        Res.drawable.bookmark,
        Res.drawable.bookmark,
        Res.string.profile
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BottomBarFloating(
    navController: AppNavigator,
    currentDestination: Destination,
    currentTopLevel: Destination,
    scrollBehavior: FloatingToolbarScrollBehavior
) {
    var avatar by remember { mutableStateOf<String?>(null) }
    val appComponent = LocalAppComponent.current
    val unreadCount by appComponent.notificationBadgeState.count.collectAsState()
    LaunchedEffect(Unit) {
        val authService = appComponent.authService
        authService.activeUser.map { authService.getCurrentSession() }.collect {
            avatar = it?.avatar
        }
    }

    val systemNavigationBarHeight =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    HorizontalFloatingToolbar(
        expanded = true,
        scrollBehavior = if (currentDestination is Destination.NewPost) null else scrollBehavior,
        modifier = Modifier.padding(bottom = systemNavigationBarHeight + 4.dp),
        colors = FloatingToolbarColors(
            toolbarContentColor = MaterialTheme.colorScheme.onSurface,
            toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            fabContentColor = MaterialTheme.colorScheme.primary,
            fabContainerColor = MaterialTheme.colorScheme.error
        )
    ) {
        HomeTab.entries.forEachIndexed { _, tab ->
            val isSelected = currentTopLevel == tab.destination


            val containerColor =
                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            val contentColor =
                if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            Box {
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = containerColor, contentColor = contentColor
                    ), onClick = {
                            if (!isSelected) {
                                navController.navigate(tab.destination) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = false
                                        saveState = true
                                    }
                                }
                            } else {
                                if (currentDestination is Destination.Search || currentTopLevel == Destination.HomeTabSearch) {
                                    appComponent.searchFieldFocus.focus()
                                } else if (currentDestination == Destination.Feeds || currentTopLevel == Destination.HomeTabFeeds) {
                                    appComponent.backToTopTrigger.scrollToTop()
                                }
                            }
                    }) {
                    if (tab == HomeTab.OwnProfile && avatar != null) {
                        AsyncImage(
                            model = avatar,
                            error = painterResource(Res.drawable.default_avatar),
                            contentDescription = "",
                            modifier = Modifier.height(30.dp).width(30.dp).clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = vectorResource(
                                if (isSelected) tab.activeIcon else tab.icon
                            ),
                            modifier = Modifier.size(28.dp),
                            contentDescription = stringResource(tab.label)
                        )
                    }
                }
                if (tab == HomeTab.Notifications && unreadCount > 0) {
                    Badge(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 4.dp)
                    ) {
                        Text(if (unreadCount > 99) "99+" else unreadCount.toString())
                    }
                }
            }
        }
    }
}

expect fun EdgeToEdgeDialogProperties(
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = false,
    usePlatformDefaultWidth: Boolean = false
): DialogProperties

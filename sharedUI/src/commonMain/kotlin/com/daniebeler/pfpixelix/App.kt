package com.daniebeler.pfpixelix

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.*
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
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.ui.composables.profile.own_profile.AccountSwitchBottomSheet
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.PreferencesComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.ReverseModalNavigationDrawer
import com.daniebeler.pfpixelix.ui.events.GlobalNavigationEvent
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.ui.navigation.appGraph
import com.daniebeler.pfpixelix.ui.theme.PixelixTheme
import com.daniebeler.pfpixelix.utils.end
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.*
import pixelix.app.generated.resources.*

val LocalSnackbarPresenter = compositionLocalOf<(String) -> Unit> {
    error("No LocalSnackbarPresenter provided")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App(
    appComponent: AppComponent,
    onNavHostReady: suspend (NavController) -> Unit,
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
                        delay(200)
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
                val navController = rememberNavController()

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
                    navController.clearBackStack<Destination.HomeTabFeeds>()
                    navController.clearBackStack<Destination.HomeTabSearch>()
                    navController.clearBackStack<Destination.HomeTabNewPost>()
                    navController.clearBackStack<Destination.HomeTabNotifications>()
                    navController.clearBackStack<Destination.HomeTabOwnProfile>()
                }

                LaunchedEffect(appComponent.globalNavigator) {
                    appComponent.globalNavigator.navigationEvents.collect { event ->
                        when (event) {
                            is GlobalNavigationEvent.NavigateToLogin -> {
                                navController.navigate(Destination.FirstLogin) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
                }

                // Bridges the NavController to the host platform once the graph is set up.
                // On web this binds browser Back/Forward and the address bar to navigation;
                // other platforms pass the default no-op.
                LaunchedEffect(navController) {
                    onNavHostReady(navController)
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
                            FloatingToolbarDefaults.exitAlwaysScrollBehavior(exitDirection = FloatingToolbarExitDirection.Bottom);
                        Scaffold(
                            contentWindowInsets = WindowInsets(0),
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            modifier = Modifier.nestedScroll(scrollBehaviorBottom)
                        ) { paddingValues ->
                            Box(Modifier.fillMaxSize().padding(paddingValues)) {
                                val startDestination =
                                    if (activeUser == null) Destination.FirstLogin
                                    else Destination.HomeTabFeeds
                                NavHost(
                                    modifier = Modifier.fillMaxSize(),
                                    navController = navController,
                                    startDestination = startDestination,
                                    builder = {
                                        appGraph(
                                            navController,
                                            { scope.launch { drawerState.open() } },
                                            exitApp
                                        )
                                    })

                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentDestination = navBackStackEntry?.destination

                                val showBottomBar =
                                    currentDestination?.hasRoute<Destination.OwnProfile>() == true || currentDestination?.hasRoute<Destination.Feeds>() == true || currentDestination?.hasRoute<Destination.Search>() == true || currentDestination?.hasRoute<Destination.Notifications>() == true

                                if (showBottomBar) {
                                    Box(
                                        modifier = Modifier.align(Alignment.BottomCenter)
                                    ) {
                                        BottomBarFloating(
                                            navController, openAccountSwitchBottomSheet = {
                                                showAccountSwitchBottomSheet = true
                                            }, scrollBehaviorBottom
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
    navController: NavController,
    openAccountSwitchBottomSheet: () -> Unit,
    scrollBehavior: FloatingToolbarScrollBehavior
) {
    var avatar by remember { mutableStateOf<String?>(null) }
    val appComponent = LocalAppComponent.current
    LaunchedEffect(Unit) {
        val authService = appComponent.authService
        authService.activeUser.map { authService.getCurrentSession() }.collect {
            avatar = it?.avatar
        }
    }

    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentDestination = navBackStackEntry?.destination ?: return
    val tabContainer = currentDestination.parent ?: return

    val systemNavigationBarHeight =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    HorizontalFloatingToolbar(
        expanded = true,
        scrollBehavior = if (currentDestination.hasRoute<Destination.NewPost>()) null else scrollBehavior,
        modifier = Modifier.padding(bottom = systemNavigationBarHeight + 4.dp),
        colors = FloatingToolbarColors(
            toolbarContentColor = MaterialTheme.colorScheme.onSurface,
            toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            fabContentColor = MaterialTheme.colorScheme.primary,
            fabContainerColor = MaterialTheme.colorScheme.error
        )
    ) {
        HomeTab.entries.forEachIndexed { index, tab ->
            val isSelected = currentDestination.hierarchy.any {
                it.hasRoute(tab.destination::class)
            }

            val interactionSource = remember { MutableInteractionSource() }
            val coroutineScope = rememberCoroutineScope()
            var isLongPress by remember { mutableStateOf(false) }

            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> {
                            isLongPress = false
                            coroutineScope.launch {
                                delay(500L)
                                if (tab == HomeTab.OwnProfile) {
                                    openAccountSwitchBottomSheet()
                                }
                                isLongPress = true
                            }
                        }

                        is PressInteraction.Release, is PressInteraction.Cancel -> {
                            coroutineScope.coroutineContext.cancelChildren()
                        }
                    }
                }
            }
            val containerColor =
                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            val contentColor =
                if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

            IconButton(
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = containerColor, contentColor = contentColor
                ), onClick = {

                    if (!isLongPress) {
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
                            val tabRoot = tabContainer.findStartDestination()
                            val isOnRoot = currentDestination == tabRoot
                            if (!isOnRoot) {
                                navController.popBackStack(
                                    route = tabRoot.route!!, inclusive = false
                                )
                            } else if (currentDestination.hasRoute<Destination.Search>()) {
                                appComponent.searchFieldFocus.focus()
                            } else if (currentDestination.hasRoute<Destination.Feeds>()) {
                                appComponent.backToTopTrigger.scrollToTop()
                            }
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
            if (index < HomeTab.entries.lastIndex) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

expect fun EdgeToEdgeDialogProperties(
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = false,
    usePlatformDefaultWidth: Boolean = false
): DialogProperties

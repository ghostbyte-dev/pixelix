package com.daniebeler.pfpixelix.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.contribute.ContributeBottomSheet
import com.daniebeler.pfpixelix.ui.composables.timelines.global_timeline.GlobalTimelineComposable
import com.daniebeler.pfpixelix.ui.composables.timelines.home_timeline.HomeTimelineComposable
import com.daniebeler.pfpixelix.ui.composables.timelines.local_timeline.LocalTimelineComposable
import com.daniebeler.pfpixelix.ui.navigation.Destination
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.app_name
import pixelix.app.generated.resources.global
import pixelix.app.generated.resources.global_timeline_explained
import pixelix.app.generated.resources.coffee
import pixelix.app.generated.resources.home
import pixelix.app.generated.resources.home_timeline_explained
import pixelix.app.generated.resources.local
import pixelix.app.generated.resources.local_timeline_explained
import pixelix.app.generated.resources.mail
import pixelix.app.generated.resources.settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeComposable(
    navController: NavController,
    openPreferencesDrawer: () -> Unit,
    viewModel: HomeViewModel = injectViewModel("homeViewModel") { homeViewModel },
) {
    val pagerState = rememberPagerState { 3 }
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    val donationSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDonationBottomSheet by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior, title = {
                    Text(
                        stringResource(Res.string.app_name),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }, actions = {
                    Row {
                        IconButton(onClick = {
                            showDonationBottomSheet = true
                        }) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.coffee),
                                contentDescription = "Coffee"
                            )
                        }

                        if (viewModel.capabilities.general.supportsDMs) {
                            IconButton(onClick = {
                                navController.navigate(Destination.Conversations)
                            }) {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.mail),
                                    contentDescription = "Conversations"
                                )
                            }
                        }


                        IconButton(onClick = {
                            openPreferencesDrawer()
                        }) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.settings),
                                contentDescription = "Settings"
                            )
                        }
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }) { paddingValues ->
        Box(
            Modifier.fillMaxSize().padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                divider = {},
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.clip(
                    RoundedCornerShape(
                        bottomStart = 24.dp, bottomEnd = 24.dp
                    )
                ).zIndex(1f)
            ) {
                Tab(
                    text = { Text(stringResource(Res.string.home)) },
                    selected = pagerState.currentPage == 0,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    })

                Tab(
                    text = { Text(stringResource(Res.string.local)) },
                    selected = pagerState.currentPage == 1,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    })

                Tab(
                    text = { Text(stringResource(Res.string.global)) },
                    selected = pagerState.currentPage == 2,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    })
            }

            HorizontalPager(
                state = pagerState,
                beyondViewportPageCount = 3,
                userScrollEnabled = viewModel.isSwipeBetweenTabsEnabled,
                modifier = Modifier.padding(top = 24.dp)
                    .background(MaterialTheme.colorScheme.background).zIndex(0f)
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> Box(modifier = Modifier.fillMaxSize()) {
                        HomeTimelineComposable(navController)
                    }

                    1 -> Box(modifier = Modifier.fillMaxSize()) {
                        LocalTimelineComposable(navController)
                    }

                    2 -> Box(modifier = Modifier.fillMaxSize()) {
                        GlobalTimelineComposable(navController)
                    }
                }
            }
        }
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            }, sheetState = sheetState
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Column {
                    Spacer(modifier = Modifier.height(18.dp))

                    SheetItem(
                        header = stringResource(Res.string.home),
                        description = stringResource(Res.string.home_timeline_explained)
                    )

                    SheetItem(
                        header = stringResource(Res.string.local),
                        description = stringResource(Res.string.local_timeline_explained)
                    )

                    SheetItem(
                        header = stringResource(Res.string.global),
                        description = stringResource(Res.string.global_timeline_explained)
                    )
                }
            }
        }
    }

    if (showDonationBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showDonationBottomSheet = false
            }, sheetState = donationSheetState
        ) {
            ContributeBottomSheet { url -> viewModel.openUrl(url) }
        }
    }
}


@Composable
fun SheetItem(header: String, description: String) {
    Column {
        Text(text = header, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = description)
        Spacer(modifier = Modifier.height(16.dp))
    }
}
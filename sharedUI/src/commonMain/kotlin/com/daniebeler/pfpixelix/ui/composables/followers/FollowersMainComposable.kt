@file:OptIn(ExperimentalMaterial3Api::class)

package com.daniebeler.pfpixelix.ui.composables.followers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.followers
import pixelix.app.generated.resources.following

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowersMainComposable(
    navController: NavController,
    accountId: String,
    isFollowers: Boolean,
    viewModel: FollowersViewModel = injectViewModel(key = "followers-viewmodel-key") { followersViewModel }
) {

    LaunchedEffect(Unit) {
        viewModel.setAccountIdValue(accountId)
        viewModel.getAccount(accountId)
        viewModel.getFollowersFirstLoad()
        viewModel.getFollowingFirstLoad()
        viewModel.setLoggedInAccountIdValue()
    }

    val pageId = if (isFollowers) 0 else 1
    val pagerState = rememberPagerState(initialPage = pageId, pageCount = { 2 })

    val scope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                Column {
                    Text(
                        text = viewModel.accountState.account?.username ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = viewModel.accountState.account?.url?.substringAfter("https://")
                            ?.substringBefore("/") ?: "", fontSize = 12.sp, lineHeight = 6.sp
                    )
                }
            }, navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = ""
                    )
                }
            }, colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
            )

        }) { paddingValues ->
        Box(
            Modifier.fillMaxSize().padding(paddingValues)
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
                    text = {
                        if (viewModel.accountState.account != null) {
                            Text(
                                viewModel.accountState.account?.followersCount.toString() + " " + stringResource(
                                    Res.string.followers
                                )
                            )
                        } else {
                            Text(text = stringResource(Res.string.followers))
                        }
                    },
                    selected = pagerState.currentPage == 0,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    })

                Tab(
                    text = {
                        if (viewModel.accountState.account != null) {
                            Text(
                                viewModel.accountState.account?.followingCount.toString() + " " + stringResource(
                                    Res.string.following
                                )
                            )
                        } else {
                            Text(text = stringResource(Res.string.following))
                        }
                    },
                    selected = pagerState.currentPage == 1,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    })
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.padding(top = 24.dp)
                    .background(MaterialTheme.colorScheme.background).zIndex(0f)
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> Box(modifier = Modifier.fillMaxSize()) {
                        FollowersComposable(navController = navController, viewModel)
                    }

                    1 -> Box(modifier = Modifier.fillMaxSize()) {
                        FollowingComposable(navController = navController, viewModel)
                    }

                }
            }
        }
    }
}
package com.daniebeler.pfpixelix.ui.composables.explore.trending

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.ui.composables.SheetItem
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts.TrendingAccountsComposable
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags.TrendingHashtagsComposable
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_posts.TrendingPostsComposable
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.accounts
import pixelix.app.generated.resources.hashtags
import pixelix.app.generated.resources.posts
import pixelix.app.generated.resources.trending_account_description
import pixelix.app.generated.resources.trending_hashtag_description
import pixelix.app.generated.resources.trending_post_description
import pixelix.app.generated.resources.what_makes_a_hashtag_trend
import pixelix.app.generated.resources.what_makes_a_post_trend
import pixelix.app.generated.resources.what_makes_an_account_trend

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingComposable(navController: NavController, initialPage: Int, isSwipeEnabled: Boolean) {

    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { 3 })

    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val range by remember { mutableStateOf("daily") }


    Box(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            userScrollEnabled = isSwipeEnabled,
            state = pagerState,
            beyondViewportPageCount = 3,
            modifier = Modifier.padding(top = 24.dp).background(MaterialTheme.colorScheme.background)
        ) { tabIndex ->
            when (tabIndex) {
                0 -> Box(modifier = Modifier.fillMaxSize()) {
                    TrendingPostsComposable(range, navController = navController)
                }

                1 -> Box(modifier = Modifier.fillMaxSize()) {
                    TrendingAccountsComposable(range, navController = navController)
                }

                2 -> Box(modifier = Modifier.fillMaxSize()) {
                    TrendingHashtagsComposable(range, navController = navController)
                }

            }
        }

        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            divider = {},
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
        ) {
            Tab(
                text = { Text(stringResource(Res.string.posts)) },
                selected = pagerState.currentPage == 0,
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }

                })

            Tab(
                text = { Text(stringResource(Res.string.accounts)) },
                selected = pagerState.currentPage == 1,
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                })

            Tab(
                text = { Text(stringResource(Res.string.hashtags)) },
                selected = pagerState.currentPage == 2,
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                })
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            }, sheetState = sheetState
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)
            ) {
                Column {
                    Spacer(modifier = Modifier.height(18.dp))

                    SheetItem(
                        header = stringResource(Res.string.what_makes_a_post_trend),
                        description = stringResource(Res.string.trending_post_description)
                    )

                    SheetItem(
                        header = stringResource(Res.string.what_makes_an_account_trend),
                        description = stringResource(Res.string.trending_account_description)
                    )

                    SheetItem(
                        header = stringResource(Res.string.what_makes_a_hashtag_trend),
                        description = stringResource(Res.string.trending_hashtag_description)
                    )
                }
            }
        }
    }
}
package com.daniebeler.pfpixelix.ui.composables.explore.trending

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.ui.composables.explore.ExploreViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.cameras.CamerasComposable
import com.daniebeler.pfpixelix.ui.composables.explore.trending.categories.CategoriesComposable
import com.daniebeler.pfpixelix.ui.composables.explore.trending.editors_choice_accounts.EditorsChoiceAccountsComposable
import com.daniebeler.pfpixelix.ui.composables.explore.trending.editors_choice_posts.EditorsChoicePostsComposable
import com.daniebeler.pfpixelix.ui.composables.explore.trending.films.FilmsComposable
import com.daniebeler.pfpixelix.ui.composables.explore.trending.lenses.LensesComposable
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts.TrendingAccountsComposable
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags.TrendingHashtagsComposable
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_posts.TrendingPostsComposable
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.cameras
import pixelix.app.generated.resources.categories
import pixelix.app.generated.resources.editors_choice_accounts
import pixelix.app.generated.resources.editors_choice_posts
import pixelix.app.generated.resources.films
import pixelix.app.generated.resources.lenses
import pixelix.app.generated.resources.trending_accounts
import pixelix.app.generated.resources.trending_hashtags
import pixelix.app.generated.resources.trending_posts

enum class TrendingRange {
    DAILY, MONTHLY, YEARLY;

    fun toApiString() = when (this) {
        DAILY -> "daily"
        MONTHLY -> "monthly"
        YEARLY -> "yearly"
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TrendingComposable(
    navController: AppNavigator,
    viewModel: ExploreViewModel,
    initialPage: Int,
    isSwipeEnabled: Boolean
) {

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { if (viewModel.capabilities.value.trending.supportsAdvancedCategories) 9 else 3 })

    val scope = rememberCoroutineScope()

    Box(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            userScrollEnabled = isSwipeEnabled,
            state = pagerState,
            beyondViewportPageCount = 3,
            modifier = Modifier.padding(top = 24.dp)
                .background(MaterialTheme.colorScheme.background)
        ) { tabIndex ->
            when (tabIndex) {
                0 -> Box(modifier = Modifier.fillMaxSize()) {
                    TrendingPostsComposable(navController = navController)
                }

                1 -> Box(modifier = Modifier.fillMaxSize()) {
                    TrendingAccountsComposable(navController = navController)
                }

                2 -> Box(modifier = Modifier.fillMaxSize()) {
                    TrendingHashtagsComposable(navController = navController)
                }

                3 -> Box(modifier = Modifier.fillMaxSize()) {
                    EditorsChoicePostsComposable(navController = navController)
                }

                4 -> Box(modifier = Modifier.fillMaxSize()) {
                    EditorsChoiceAccountsComposable(navController = navController)
                }

                5 -> Box(modifier = Modifier.fillMaxSize()) {
                    CategoriesComposable(navController = navController)
                }

                6 -> Box(modifier = Modifier.fillMaxSize()) {
                    CamerasComposable(navController = navController)
                }

                7 -> Box(modifier = Modifier.fillMaxSize()) {
                    LensesComposable(navController = navController)
                }

                8 -> Box(modifier = Modifier.fillMaxSize()) {
                    FilmsComposable(navController = navController)
                }

            }
        }

        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 16.dp,
            divider = {},
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Tab(
                text = { Text(stringResource(Res.string.trending_posts)) },
                selected = pagerState.currentPage == 0,
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }

                })

            Tab(
                text = { Text(stringResource(Res.string.trending_accounts)) },
                selected = pagerState.currentPage == 1,
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                })

            Tab(
                text = { Text(stringResource(Res.string.trending_hashtags)) },
                selected = pagerState.currentPage == 2,
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                })

            if (viewModel.capabilities.value.trending.supportsAdvancedCategories) {
                Tab(
                    text = { Text(stringResource(Res.string.editors_choice_posts)) },
                    selected = pagerState.currentPage == 3,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(3)
                        }
                    })

                Tab(
                    text = { Text(stringResource(Res.string.editors_choice_accounts)) },
                    selected = pagerState.currentPage == 4,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(4)
                        }
                    })

                Tab(
                    text = { Text(stringResource(Res.string.categories)) },
                    selected = pagerState.currentPage == 5,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(5)
                        }
                    })

                Tab(
                    text = { Text(stringResource(Res.string.cameras)) },
                    selected = pagerState.currentPage == 6,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(6)
                        }
                    })

                Tab(
                    text = { Text(stringResource(Res.string.lenses)) },
                    selected = pagerState.currentPage == 7,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(7)
                        }
                    })

                Tab(
                    text = { Text(stringResource(Res.string.films)) },
                    selected = pagerState.currentPage == 8,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(8)
                        }
                    })
            }
        }
    }
}
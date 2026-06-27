package com.daniebeler.pfpixelix.ui.composables.explore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.SavedSearchItem
import com.daniebeler.pfpixelix.domain.model.SavedSearchType
import com.daniebeler.pfpixelix.domain.model.toDomain
import com.daniebeler.pfpixelix.ui.composables.custom_account.AccountListItem
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomHashtag
import com.daniebeler.pfpixelix.ui.composables.custom_account.CustomAccount
import com.daniebeler.pfpixelix.ui.composables.explore.trending.TrendingComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.navigation.Destination
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.accounts
import pixelix.app.generated.resources.chevron_left
import pixelix.app.generated.resources.close
import pixelix.app.generated.resources.default_avatar
import pixelix.app.generated.resources.explore
import pixelix.app.generated.resources.hash
import pixelix.app.generated.resources.hashtags
import pixelix.app.generated.resources.search
import pixelix.app.generated.resources.trash

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreComposable(
    navController: NavController,
    initialPage: Int = 0,
    viewModel: ExploreViewModel = injectViewModel(key = "search-viewmodel-key") { exploreViewModel }
) {
    val focusRequester = remember { FocusRequester() }
    val textFieldState = rememberTextFieldState()
    var expanded by rememberSaveable { mutableStateOf(false) }

    val appComponent = LocalAppComponent.current
    LaunchedEffect(Unit) {
        appComponent.searchFieldFocus.events.collect {
            focusRequester.requestFocus()
        }
    }

    Box(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceContainer)
            .semantics { isTraversalGroup = true }) {
        SearchBar(
            modifier = Modifier.align(Alignment.TopCenter).semantics { traversalIndex = 0f },
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = { textFieldState.setTextAndPlaceCursorAtEnd(it) },
                    onSearch = {
                        expanded = false
                        viewModel.onSearch(it)
                        viewModel.saveSearch(it)
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text(stringResource(Res.string.explore)) },
                    modifier = Modifier.focusRequester(focusRequester),
                    leadingIcon = {
                        if (!expanded) {
                            Icon(vectorResource(Res.drawable.search), contentDescription = null)
                        } else {
                            Icon(
                                vectorResource(Res.drawable.chevron_left),
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    expanded = false
                                    textFieldState.clearText()
                                    viewModel.searchState = SearchState()
                                })
                        }
                    },
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = textFieldState.text.isNotBlank(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Icon(
                                vectorResource(Res.drawable.close),
                                contentDescription = "clear search query",
                                modifier = Modifier.clickable {
                                    textFieldState.clearText()
                                    viewModel.searchState = SearchState()
                                })
                        }
                    })
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {

            LaunchedEffect(textFieldState.text) {
                viewModel.textInputChange(textFieldState.text.toString())
            }

            if (textFieldState.text.isBlank() && viewModel.savedSearches.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.imePadding(),
                    contentPadding = PaddingValues(bottom = 60.dp),
                ) {
                    items(viewModel.savedSearches.reversed()) {
                        if (it.savedSearchType == SavedSearchType.Account) {
                            Row {
                                CustomAccount(
                                    account = it.account!!.toDomain(),
                                    relationship = null,
                                    navController = navController,
                                    removeSavedSearch = { viewModel.deleteSavedSearch(it) })
                            }
                        } else {
                            PastSearchItem(item = it, navController, { text ->
                                expanded = false
                                textFieldState.setTextAndPlaceCursorAtEnd(text)
                                viewModel.onSearch(text)
                            }, { viewModel.deleteSavedSearch(it) })
                        }
                    }
                }
            }
            viewModel.searchState.searchResult?.let { searchResult ->
                LazyColumn(
                    modifier = Modifier.imePadding(),
                    contentPadding = PaddingValues(bottom = 60.dp),
                    content = {
                        items(searchResult.accounts.take(5)) {
                            CustomAccount(
                                account = it,
                                relationship = null,
                                onClick = { viewModel.saveAccount(it.username, it) },
                                navController = navController
                            )
                        }
                        item { HorizontalDivider(Modifier.padding(12.dp)) }
                        items(searchResult.tags.take(5)) {
                            CustomHashtag(
                                hashtag = it,
                                onClick = { viewModel.saveHashtag(it.name) },
                                navController = navController
                            )
                        }
                    })
            }

            if (viewModel.searchState.isLoading) {
                LoadingComposable()
            }
        }
        Box(
            Modifier.windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                .semantics { traversalIndex = 1f }.padding(top = 80.dp),
        ) {
            if (textFieldState.text.isNotBlank() && viewModel.searchState.searchResult != null) {
                SearchResultComposable(
                    searchState = viewModel.searchState,
                    saveAccount = { username, account -> viewModel.saveAccount(username, account) },
                    saveHashtag = { hashtag -> viewModel.saveHashtag(hashtag) },
                    navController = navController
                )
            } else {
                TrendingComposable(
                    navController,
                    initialPage = initialPage,
                    isSwipeEnabled = viewModel.isSwipeEnabled
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchResultComposable(
    searchState: SearchState,
    saveAccount: (String, Account) -> Unit,
    saveHashtag: (String) -> Unit,
    navController: NavController
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val scope = rememberCoroutineScope()
    Column {

        PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
            Tab(
                text = { Text(stringResource(Res.string.accounts)) },
                selected = pagerState.currentPage == 0,
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }

                })

            Tab(
                text = { Text(stringResource(Res.string.hashtags)) },
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
            beyondViewportPageCount = 2,
            modifier = Modifier.weight(1f).background(MaterialTheme.colorScheme.background)
        ) { tabIndex ->
            when (tabIndex) {
                0 -> Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(contentPadding = PaddingValues(8.dp), content = {
                        if (searchState.searchResult != null) {
                            itemsIndexed(searchState.searchResult.accounts) { index, account ->
                                AccountListItem(
                                    account = account,
                                    relationship = null,
                                    navController = navController,
                                    index = index,
                                    count = searchState.searchResult.accounts.size,
                                    onClick = { saveAccount(account.username, account) })
                            }
                        }
                    })
                }

                1 -> Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(content = {
                        if (searchState.searchResult != null) {
                            items(searchState.searchResult.tags) {
                                CustomHashtag(
                                    hashtag = it,
                                    onClick = { saveHashtag(it.name) },
                                    navController = navController
                                )
                            }
                        }
                    })
                }
            }
        }
    }
}

@Composable
private fun PastSearchItem(
    item: SavedSearchItem,
    navController: NavController,
    setSearchText: (text: String) -> Unit,
    deleteSavedSearch: () -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth().clickable {
            when (item.savedSearchType) {
                SavedSearchType.Account -> navController.navigate(
                    Destination.Profile(
                        item.account?.id, item.account?.username
                    )
                )

                SavedSearchType.Hashtag -> navController.navigate(
                    Destination.HashtagTimeline(
                        item.value
                    )
                )

                SavedSearchType.Search -> setSearchText(item.value)
            }
        }, verticalAlignment = Alignment.CenterVertically
    ) {
        if (item.savedSearchType == SavedSearchType.Account) {
            AsyncImage(
                model = item.account!!.avatar,
                error = painterResource(Res.drawable.default_avatar),
                contentDescription = "",
                modifier = Modifier.height(46.dp).width(46.dp).clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier.height(46.dp).width(46.dp)
                    .background(MaterialTheme.colorScheme.surfaceBright, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (item.savedSearchType == SavedSearchType.Hashtag) {
                        vectorResource(Res.drawable.hash)
                    } else {
                        vectorResource(Res.drawable.search)
                    }, contentDescription = null, tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))

        val text = when (item.savedSearchType) {
            SavedSearchType.Account -> {
                "@${item.value}"
            }

            SavedSearchType.Hashtag -> {
                "#${item.value}"
            }

            else -> {
                item.value
            }
        }
        Text(text = text, modifier = Modifier.weight(1f), softWrap = true)
        Box(
            modifier = Modifier.height(22.dp).width(22.dp).clickable { deleteSavedSearch() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.close),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
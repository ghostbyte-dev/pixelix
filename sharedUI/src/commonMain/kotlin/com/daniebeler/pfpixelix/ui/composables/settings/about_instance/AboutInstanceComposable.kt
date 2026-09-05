package com.daniebeler.pfpixelix.ui.composables.settings.about_instance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.service.general.BackendType
import com.daniebeler.pfpixelix.ui.composables.custom_account.AccountListItem
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.ScreenScaffold
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.DomainFormat
import com.daniebeler.pfpixelix.utils.StringFormat
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.admin
import pixelix.app.generated.resources.instance_version
import pixelix.app.generated.resources.posts
import pixelix.app.generated.resources.privacy_policy
import pixelix.app.generated.resources.rules
import pixelix.app.generated.resources.stats
import pixelix.app.generated.resources.terms_of_use
import pixelix.app.generated.resources.users

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutInstanceComposable(
    navController: AppNavigator,
    viewModel: AboutInstanceViewModel = injectViewModel(key = "about-instance-key") { aboutInstanceViewModel }
) {

    val lazyListState = rememberLazyListState()

    val colors =
        ListItemDefaults.colors(disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer)


    ScreenScaffold(
        title = DomainFormat.formatDomain(viewModel.ownInstanceDomain),
        navController = navController
    ) {
        LazyColumn(
            state = lazyListState
        ) {
            if (!viewModel.instanceState.isLoading && viewModel.instanceState.error.isEmpty()) {
                item {
                    Box(
                        Modifier.fillParentMaxWidth().height(24.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    )
                    AsyncImage(
                        model = viewModel.instanceState.instance?.thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    viewModel.instanceState.instance?.let {
                        Text(
                            text = if (it.description.length > 100) {
                                it.shortDescription
                            } else {
                                it.description
                            }, Modifier.padding(12.dp, 0.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = stringResource(Res.string.stats),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(12.dp, 0.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = StringFormat.groupDigits(
                                    viewModel.instanceState.instance?.stats?.userCount
                                ), fontWeight = FontWeight.Bold, fontSize = 18.sp
                            )
                            Text(text = stringResource(Res.string.users), fontSize = 12.sp)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = StringFormat.groupDigits(
                                    viewModel.instanceState.instance?.stats?.statusCount
                                ), fontWeight = FontWeight.Bold, fontSize = 18.sp
                            )
                            Text(text = pluralStringResource(Res.plurals.posts, viewModel.instanceState.instance?.stats?.statusCount ?: 0), fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    viewModel.instanceState.instance?.admin?.let { account ->
                        Text(
                            text = stringResource(Res.string.admin),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(12.dp, 0.dp)
                        )

                        Box(Modifier.padding(8.dp)) {
                            AccountListItem(
                                account = account,
                                relationship = null,
                                navController = navController,
                                index = 0,
                                count = 1,
                                onClick = {
                                    navController.navigate(
                                        Destination.Profile(
                                            account.id, account.username
                                        )
                                    )
                                })
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Legal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                    )

                    val privacyPath =
                        if (viewModel.backendType == BackendType.PIXELFED) "/site/privacy" else "/privacy"
                    val termsPath =
                        if (viewModel.backendType == BackendType.PIXELFED) "/site/terms" else "/terms"
                    val domain =
                        DomainFormat.formatDomain(viewModel.instanceState.instance?.domain ?: "")

                    val linkColors =
                        ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer)

                    SegmentedListItem(
                        onClick = { viewModel.instanceState.instance?.let { viewModel.openUrl("https://$domain$privacyPath") } },
                        colors = linkColors,
                        shapes = ListItemDefaults.segmentedShapes(index = 0, count = 2),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 1.dp),
                        content = {
                            Text(
                                text = stringResource(Res.string.privacy_policy),
                                color = MaterialTheme.colorScheme.primary
                            )
                        })

                    SegmentedListItem(
                        onClick = { viewModel.instanceState.instance?.let { viewModel.openUrl("https://$domain$termsPath") } },
                        colors = linkColors,
                        shapes = ListItemDefaults.segmentedShapes(index = 1, count = 2),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 1.dp),
                        content = {
                            Text(
                                text = stringResource(Res.string.terms_of_use),
                                color = MaterialTheme.colorScheme.primary
                            )
                        })

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = stringResource(Res.string.rules),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                    )
                }

                val rules = viewModel.instanceState.instance?.rules ?: emptyList()

                itemsIndexed(rules) { index, rule ->
                    SegmentedListItem(
                        enabled = false,
                        onClick = {},
                        colors = colors,
                        shapes = ListItemDefaults.segmentedShapes(
                            index = index, count = rules.size
                        ),
                        modifier = Modifier.padding(8.dp, 1.dp),
                        leadingContent = {
                            Text(
                                text = rule.id,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        },
                        content = {
                            Text(
                                text = rule.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        })
                }


                item {
                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = stringResource(Res.string.instance_version),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(12.dp, 0.dp)
                    )

                    Text(
                        text = viewModel.instanceState.instance?.version ?: "",
                        modifier = Modifier.padding(12.dp, 0.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

        }

        if (viewModel.instanceState.isLoading) {
            LoadingComposable()
        }

        if (viewModel.instanceState.error.isNotBlank()) {
            ErrorComposable(
                message = viewModel.instanceState.error,
                modifier = Modifier.fillMaxSize().padding(36.dp, 20.dp)
            )
        }
    }
}

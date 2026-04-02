package com.daniebeler.pfpixelix.ui.composables.settings.about_instance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenLoadingComposable
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.StringFormat
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.admin
import pixelix.app.generated.resources.default_avatar
import pixelix.app.generated.resources.instance_version
import pixelix.app.generated.resources.posts
import pixelix.app.generated.resources.privacy_policy
import pixelix.app.generated.resources.rules
import pixelix.app.generated.resources.stats
import pixelix.app.generated.resources.terms_of_use
import pixelix.app.generated.resources.users

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutInstanceComposable(
    navController: NavController,
    viewModel: AboutInstanceViewModel = injectViewModel(key = "about-instance-key") { aboutInstanceViewModel }
) {

    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {

        val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

        Box(
            modifier = Modifier.padding(top = TopAppBarDefaults.TopAppBarExpandedHeight + statusBarPadding - 24.dp)
                .fillMaxSize()
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
                        Text(
                            text = viewModel.instanceState.instance?.description ?: "",
                            Modifier.padding(12.dp, 0.dp)
                        )
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
                                Text(text = stringResource(Res.string.posts), fontSize = 12.sp)
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

                            Row(
                                modifier = Modifier.clickable {
                                    navController.navigate(Destination.Profile(account.id))
                                }.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = account.avatar,
                                    error = painterResource(Res.drawable.default_avatar),
                                    contentDescription = "",
                                    modifier = Modifier.height(46.dp).width(46.dp).clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    if (account.displayname != null) {
                                        Text(text = account.displayname)
                                    }
                                    Text(text = "@${account.username}")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = stringResource(Res.string.privacy_policy),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(12.dp, 0.dp)
                        )

                        Text(
                            text = "https://" + viewModel.instanceState.instance?.domain + "/site/privacy",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(12.dp, 0.dp).clickable {
                                if (viewModel.instanceState.instance != null) {
                                    viewModel.openUrl(
                                        url = "https://" + viewModel.instanceState.instance!!.domain + "/site/privacy"
                                    )
                                }
                            })


                        Spacer(modifier = Modifier.height(18.dp))


                        Text(
                            text = stringResource(Res.string.terms_of_use),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(12.dp, 0.dp)
                        )

                        Text(
                            text = "https://" + viewModel.instanceState.instance?.domain + "/site/terms",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(12.dp, 0.dp).clickable {
                                if (viewModel.instanceState.instance != null) {
                                    viewModel.openUrl(
                                        url = "https://" + viewModel.instanceState.instance!!.domain + "/site/terms"
                                    )
                                }
                            })


                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = stringResource(Res.string.rules),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(12.dp, 0.dp)
                        )
                    }

                    items(viewModel.instanceState.instance?.rules ?: emptyList()) {
                        Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp)) {
                            Text(
                                text = it.id,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(18.dp))
                            Text(text = it.text)
                        }
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

        }

        TopAppBar(
            modifier = Modifier.clip(
                RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ), title = {
                Text(
                    text = viewModel.ownInstanceDomain,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }, navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = ""
                    )
                }
            }, colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )

        if (viewModel.instanceState.isLoading) {
            FullscreenLoadingComposable()
        }

        if (viewModel.instanceState.error.isNotBlank()) {
            FullscreenErrorComposable(message = viewModel.instanceState.error)
        }
    }
}
package com.daniebeler.pfpixelix.ui.composables.profile.server_stats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.FediseaInstance
import com.daniebeler.pfpixelix.domain.model.FediseaSoftware
import com.daniebeler.pfpixelix.ui.composables.widgets.CardButton
import com.daniebeler.pfpixelix.utils.StringFormat
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.active_users
import pixelix.app.generated.resources.chevron_forward_outline
import pixelix.app.generated.resources.fediverse_logo
import pixelix.app.generated.resources.instance_version
import pixelix.app.generated.resources.instances
import pixelix.app.generated.resources.license_label
import pixelix.app.generated.resources.open_outline
import pixelix.app.generated.resources.registration
import pixelix.app.generated.resources.open
import pixelix.app.generated.resources.closed
import pixelix.app.generated.resources.total_posts
import pixelix.app.generated.resources.total_users

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DomainSoftwareComposable(
    domain: String,
    viewModel: ServerStatsViewModel = injectViewModel(key = "serverstats$domain") { serverStatsViewModel }
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.getData(domain)
    }

    if (!viewModel.statsState.isLoading) {
        AsyncImage(
            model = viewModel.statsState.fediSoftware?.iconUrl ?: Res.drawable.fediverse_logo,
            error = painterResource(Res.drawable.fediverse_logo),
            contentDescription = "",
            modifier = Modifier.height(24.dp).clickable { showBottomSheet = true })
    } else {
        CircularProgressIndicator(Modifier.size(18.dp))
    }


    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            }, sheetState = sheetState
        ) {
            ServerStatsSheet(
                instance = viewModel.statsState.fediServer,
                software = viewModel.statsState.fediSoftware,
                onClick = { viewModel.openUrl(it) })/* Column(
                 modifier = Modifier
                     .padding(12.dp)
                     .fillMaxWidth()
                     .verticalScroll(state = rememberScrollState())
             ) {
                 if (viewModel.statsState.fediSoftware != null) {
                     Row(
                         verticalAlignment = Alignment.CenterVertically,
                         horizontalArrangement = Arrangement.Center,
                         modifier = Modifier.fillMaxWidth()
                     ) {
                         viewModel.statsState.fediSoftware?.let {

                         }
                         AsyncImage(
                             model = viewModel.statsState.fediSoftware?.iconUrl
                                 ?: Res.drawable.fediverse_logo,
                             error = painterResource(Res.drawable.fediverse_logo),
                             contentDescription = "",
                             modifier = Modifier.height(56.dp)
                         )
                         Spacer(modifier = Modifier.width(12.dp))
                         Text(
                             text = viewModel.statsState.fediSoftware!!.name
                                 ?: viewModel.statsState.fediSoftware!!.identifier,
                             fontSize = 32.sp,
                             fontWeight = FontWeight.Bold
                         )
                     }

                     if (viewModel.statsState.fediSoftware!!.description != null) {
                         Spacer(modifier = Modifier.height(12.dp))
                         Text(text = viewModel.statsState.fediSoftware!!.description!!)
                     }

                     Spacer(modifier = Modifier.height(12.dp))

                     Row {
                         Text(stringResource(Res.string.instances))
                         Spacer(Modifier.width(8.dp))
                         Text(
                             text = StringFormat.groupDigits(
                                 viewModel.statsState.fediSoftware!!.instances
                             ), fontWeight = FontWeight.Bold
                         )
                     }


                     Spacer(modifier = Modifier.height(12.dp))

                     Row {
                         Text(stringResource(Res.string.total_posts))
                         Spacer(Modifier.width(8.dp))
                         Text(
                             text = StringFormat.groupDigits(
                                 viewModel.statsState.fediSoftware!!.localPosts
                             ), fontWeight = FontWeight.Bold
                         )
                     }


                     Spacer(modifier = Modifier.height(12.dp))

                     Row {
                         Text(stringResource(Res.string.total_users))
                         Spacer(Modifier.width(8.dp))
                         Text(
                             text = StringFormat.groupDigits(
                                 viewModel.statsState.fediSoftware!!.totalUsers
                             ), fontWeight = FontWeight.Bold
                         )
                     }


                     Spacer(modifier = Modifier.height(12.dp))

                     Row {
                         Text(stringResource(Res.string.active_users))
                         Spacer(Modifier.width(8.dp))
                         Text(
                             text = StringFormat.groupDigits(
                                 viewModel.statsState.fediSoftware!!.totalUsers
                             ), fontWeight = FontWeight.Bold
                         )
                     }



                     Spacer(modifier = Modifier.height(12.dp))

                     if (viewModel.statsState.fediSoftware!!.website != null) {
                         TextButton(
                             onClick = {
                                 viewModel.openUrl(
                                     viewModel.statsState.fediSoftware!!.website!!
                                 )
                             },
                             shape = RoundedCornerShape(12.dp),
                             modifier = Modifier.align(Alignment.CenterHorizontally)
                         ) {
                             Text(
                                 text = stringResource(
                                     Res.string.visit_url,
                                     viewModel.statsState.fediSoftware!!.website!!
                                 )
                             )
                         }
                     }

                 }

                 if (viewModel.statsState.fediServer != null) {
                     Spacer(modifier = Modifier.height(12.dp))

                     HorizontalDivider(Modifier.padding(vertical = 12.dp))

                     Spacer(modifier = Modifier.height(12.dp))

                     Text(
                         text = viewModel.statsState.fediServer!!.domain,
                         fontSize = 32.sp,
                         textAlign = TextAlign.Center,
                         fontWeight = FontWeight.Bold,
                         modifier = Modifier.fillMaxWidth()
                     )

                     if (viewModel.statsState.fediServer!!.description != null) {
                         Spacer(modifier = Modifier.height(12.dp))
                         Text(viewModel.statsState.fediServer!!.description!!)
                         Spacer(modifier = Modifier.height(24.dp))
                     }

                     Row {
                         Text(
                             stringResource(
                                 Res.string.server_version,
                                 viewModel.statsState.fediServer!!.software,
                                 viewModel.statsState.fediServer!!.version
                             )
                         )
                     }

                     Spacer(modifier = Modifier.height(12.dp))


                     Row {
                         Text(
                             "Open registration:"
                         )
                         Spacer(Modifier.width(8.dp))

                         if (viewModel.statsState.fediServer!!.openRegistration) {
                             Icon(
                                 imageVector = vectorResource(Res.drawable.confirm),
                                 tint = Color.Green,
                                 contentDescription = "true",
                             )
                         } else {
                             Icon(
                                 imageVector = vectorResource(Res.drawable.close_outline),
                                 tint = Color.Red,
                                 contentDescription = "false",
                             )
                         }
                     }

                     Spacer(modifier = Modifier.height(24.dp))



                     Row {
                         Text(stringResource(Res.string.total_posts))
                         Spacer(Modifier.width(8.dp))
                         Text(
                             text = StringFormat.groupDigits(
                                 viewModel.statsState.fediServer!!.localPosts
                             ), fontWeight = FontWeight.Bold
                         )
                     }

                     Spacer(modifier = Modifier.height(12.dp))

                     Row {
                         Text(stringResource(Res.string.total_users))
                         Spacer(Modifier.width(8.dp))
                         Text(
                             text = StringFormat.groupDigits(
                                 viewModel.statsState.fediServer!!.totalUsers
                             ), fontWeight = FontWeight.Bold
                         )
                     }

                     Spacer(modifier = Modifier.height(12.dp))

                     Row {
                         Text(stringResource(Res.string.active_users))
                         Spacer(Modifier.width(8.dp))
                         Text(
                             text = StringFormat.groupDigits(
                                 viewModel.statsState.fediServer!!.activeUsersMonth
                             ), fontWeight = FontWeight.Bold
                         )
                     }


                     Spacer(modifier = Modifier.height(12.dp))

                     TextButton(
                         onClick = {
                             viewModel.openUrl(
                                 "https://" + viewModel.statsState.fediServer!!.domain
                             )
                         },
                         shape = RoundedCornerShape(12.dp),
                         modifier = Modifier.align(Alignment.CenterHorizontally)
                     ) {
                         Text(
                             text = stringResource(
                                 Res.string.visit_url,
                                 ("https://" + viewModel.statsState.fediServer!!.domain)
                             )
                         )
                     }

                     Spacer(modifier = Modifier.height(12.dp))
                 }
             }
         */
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun ContributeBottomSheetPreview() {
    MaterialTheme {
        ServerStatsSheet(
            FediseaInstance(
                title = "pixelix.social",
                domain = "pixelix.social",
                software = "Pixelfed",
                version = "0.12.6",
                thumbnailUrl = "https://pixelix.social/storage/headers/IVMjtLwQxSUYNuaA0JXuyzuP5gEOnqHF85hXWjm9.png",
                sourceUrl = "https://github.com/pixelfed/pixelfed",
                localPosts = 206,
                totalUsers = 130,
                description = "The Pixelfed instance by the developers of Pixelix",
                localComments = 0,
                softwareLogoUrl = "https://assets.fedisea.surf/logos/pixelfed.svg",
                activeUsersMonth = 36,
                activeUsersHalfyear = 86,
                openRegistration = true
            ), FediseaSoftware(
                identifier = "pixelfed",
                name = "Pixelfed",
                website = "https://pixelfed.org",
                sourceCode = "https://github.com/pixelfed/pixelfed",
                description = "Photo Sharing. For Everyone.",
                license = "AGPL",
                joinUrl = "https://pixelfed.org/servers",
                instances = 742,
                activeUsersHalfyear = 431983,
                activeUsersMonth = 91921,
                totalUsers = 1067454,
                localPosts = 46076236,
                localComments = 0,
                iconUrl = "https://assets.fedisea.surf/logos/pixelfed.svg"
            ), onClick = {})
    }
}

@Composable
fun ServerStatsSheet(
    instance: FediseaInstance?, software: FediseaSoftware?, onClick: (url: String) -> Unit
) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.verticalScroll(scrollState).fillMaxWidth()
        ) {
            software?.let {
                StatsCard(
                    icon = software.iconUrl,
                    thumbnail = null,
                    name = software.name,
                    identifier = software.identifier,
                    description = software.description,
                    stats = buildList {
                        add(
                            Pair(
                                stringResource(Res.string.instances),
                                StringFormat.groupDigits(software.instances)
                            )
                        )
                        add(
                            Pair(
                                stringResource(Res.string.total_users),
                                StringFormat.groupDigits(software.totalUsers)
                            )
                        )
                        add(
                            Pair(
                                stringResource(Res.string.active_users),
                                StringFormat.groupDigits(software.activeUsersMonth)
                            )
                        )
                        add(
                            Pair(
                                stringResource(Res.string.total_posts),
                                StringFormat.groupDigits(software.localPosts)
                            )
                        )
                        software.license?.let {
                            add(
                                Pair(
                                    stringResource(Res.string.license_label) + ":", software.license
                                )
                            )
                        }
                    },
                    website = software.website,
                    onClick = { onClick(software.website ?: "") })
            }
            instance?.let {
                StatsCard(
                    icon = null,
                    thumbnail = instance.thumbnailUrl,
                    name = instance.domain,
                    identifier = instance.domain,
                    description = instance.description,
                    stats = listOf(
                        Pair(
                            stringResource(Res.string.total_users),
                            StringFormat.groupDigits(instance.totalUsers)
                        ), Pair(
                            stringResource(Res.string.active_users),
                            StringFormat.groupDigits(instance.activeUsersMonth)
                        ), Pair(
                            stringResource(Res.string.total_posts),
                            StringFormat.groupDigits(instance.localPosts)
                        )
                    ),
                    secondStats = listOf(
                        Pair(
                            stringResource(Res.string.instance_version) + ":", instance.version
                        ), Pair(
                            stringResource(Res.string.registration) + ":",
                            if (instance.openRegistration) {
                                stringResource(Res.string.open)
                            } else {
                                stringResource(Res.string.closed)
                            }
                        )
                    ),
                    website = instance.domain,
                    onClick = { onClick("https://" + instance.domain) })
            }
            Box(Modifier.fillMaxWidth()) {
                Text(
                    buildAnnotatedString {
                        append("Powered by ")
                        val link = LinkAnnotation.Url(
                            "https://fedisea.surf", TextLinkStyles(
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        )
                        withLink(link) { append("fedisea.surf") }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun StatsCard(
    icon: String?,
    thumbnail: String?,
    name: String?,
    identifier: String,
    description: String?,
    stats: List<Pair<String, String>>,
    secondStats: List<Pair<String, String>>? = null,
    website: String?,
    onClick: () -> Unit,
) {
    Card(
        shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ), modifier = Modifier.fillMaxWidth()
    ) {
        thumbnail?.let {
            AsyncImage(
                model = it,
                contentDescription = "Instance Thumbnail",
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(
            Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (thumbnail == null) {
                    AsyncImage(
                        model = icon ?: Res.drawable.fediverse_logo,
                        error = painterResource(Res.drawable.fediverse_logo),
                        contentDescription = "",
                        modifier = Modifier.height(36.dp)
                    )
                }
                Text(
                    name ?: identifier,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(4.dp))
            description?.let {
                Text(
                    it.trim(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(
                Modifier.padding(vertical = 8.dp)
            )

            for (stat in stats) {
                StatCol(stat.first, stat.second)
            }

            secondStats?.let {
                HorizontalDivider(
                    Modifier.padding(vertical = 8.dp)
                )
                for (stat in secondStats) {
                    StatCol(stat.first, stat.second)
                }
            }

            Spacer(Modifier.height(12.dp))
            website?.let {
                CardButton(
                    leadingIcon = Res.drawable.open_outline,
                    title = website,
                    desc = "Visit website",
                    trailingContent = Res.drawable.chevron_forward_outline,
                    onClick = onClick
                )
            }
        }
    }

}

@Composable
fun StatCol(title: String, value: String) {
    Row {
        Text(
            buildAnnotatedString {
                append("$title ")
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(value)
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
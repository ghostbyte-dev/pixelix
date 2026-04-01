package com.daniebeler.pfpixelix.ui.composables.profile.server_stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.utils.StringFormat
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.active_users
import pixelix.app.generated.resources.default_avatar
import pixelix.app.generated.resources.fediverse_logo
import pixelix.app.generated.resources.instances
import pixelix.app.generated.resources.server_version
import pixelix.app.generated.resources.total_posts
import pixelix.app.generated.resources.total_users
import pixelix.app.generated.resources.visit_url

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
            model = viewModel.statsState.fediSoftware?.iconUrl
                ?: Res.drawable.fediverse_logo,
            error = painterResource(Res.drawable.fediverse_logo),
            contentDescription = "",
            modifier = Modifier
                .height(24.dp)
                .clickable { showBottomSheet = true })
    } else {
        CircularProgressIndicator(Modifier.size(18.dp))
    }


    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            }, sheetState = sheetState
        ) {
            Column(
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
                                imageVector = Icons.Rounded.Check,
                                tint = Color.Green,
                                contentDescription = "true",
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Close,
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
        }
    }
}
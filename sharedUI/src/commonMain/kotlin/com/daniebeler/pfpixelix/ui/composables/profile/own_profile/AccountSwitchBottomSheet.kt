package com.daniebeler.pfpixelix.ui.composables.profile.own_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.Credentials
import com.daniebeler.pfpixelix.domain.model.credentialsToAccount
import com.daniebeler.pfpixelix.ui.navigation.Destination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.add
import pixelix.app.generated.resources.add_account
import pixelix.app.generated.resources.are_you_sure_you_want_to_remove_this_account
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.default_avatar
import pixelix.app.generated.resources.remove
import pixelix.app.generated.resources.remove_account

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AccountSwitchBottomSheet(
    navController: NavController,
    closeBottomSheet: () -> Unit,
    ownProfileViewModel: OwnProfileViewModel?,
    viewModel: AccountSwitchViewModel = injectViewModel(key = "account_switcher_viewmodel") { accountSwitchViewModel }
) {
    val showRemoveLoginDataAlert = remember { mutableStateOf<Credentials?>(null) }
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    Column(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 12.dp)
    ) {
        val allCredentials = remember(viewModel.sessionStorage) {
            viewModel.sessionStorage?.sessions?.values?.toList().orEmpty()
        }

        val totalItems = allCredentials.size + 1

        if (allCredentials.isNotEmpty()) {
            Column {
                allCredentials.forEachIndexed { index, credentials ->
                    val account = credentialsToAccount(credentials)
                    val isCurrentAccount =
                        credentials.accountId == viewModel.currentCredentials?.accountId

                    SegmentedListItem(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                        onClick = {
                            if (isCurrentAccount) {
                                closeBottomSheet()
                            } else {
                                viewModel.switchAccount(credentials.key()) {
                                    ownProfileViewModel?.updateAccountSwitch()
                                    closeBottomSheet()
                                }
                            }
                        },
                        onLongClick = {
                            showRemoveLoginDataAlert.value = credentials
                        },
                        shapes = ListItemDefaults.segmentedShapes(
                            index = index, count = totalItems
                        ),
                        leadingContent = {
                            AsyncImage(
                                model = account.avatar,
                                error = painterResource(Res.drawable.default_avatar),
                                contentDescription = null,
                                modifier = Modifier.height(46.dp).width(46.dp).clip(CircleShape)
                            )
                        },
                        trailingContent = {
                            RadioButton(
                                selected = isCurrentAccount, onClick = null
                            )
                        },
                        supportingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = account.username,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                val domain =
                                    account.url.substringAfter("https://").substringBefore("/")
                                Text(
                                    text = " \u2022 $domain",
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        },
                        content = {
                            Text(
                                text = account.displayname ?: account.username,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        })
                }

                SegmentedListItem(
                    onClick = {
                        navController.navigate(Destination.NewLogin)
                        closeBottomSheet()
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                    shapes = ListItemDefaults.segmentedShapes(
                        index = allCredentials.size, count = totalItems
                    ),
                    leadingContent = {
                        Box(
                            modifier = Modifier.size(46.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.add),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    content = {
                        Text(
                            text = stringResource(Res.string.add_account), fontWeight = FontWeight.Bold
                        )
                    })
            }
        }
    }

    if (showRemoveLoginDataAlert.value != null) {
        AlertDialog(title = {
            Text(text = stringResource(Res.string.remove_account))
        }, text = {
            Text(text = stringResource(Res.string.are_you_sure_you_want_to_remove_this_account))
        }, onDismissRequest = {
            showRemoveLoginDataAlert.value = null
        }, confirmButton = {
            TextButton(onClick = {
                CoroutineScope(Dispatchers.Default).launch {
                    viewModel.removeAccount(showRemoveLoginDataAlert.value!!.key())
                    showRemoveLoginDataAlert.value = null
                }
            }) {
                Text(stringResource(Res.string.remove))
            }
        }, dismissButton = {
            TextButton(onClick = {
                showRemoveLoginDataAlert.value = null
            }) {
                Text(stringResource(Res.string.cancel))
            }
        })
    }
}

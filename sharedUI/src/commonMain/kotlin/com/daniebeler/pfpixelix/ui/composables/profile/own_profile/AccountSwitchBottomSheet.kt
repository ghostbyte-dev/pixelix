package com.daniebeler.pfpixelix.ui.composables.profile.own_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.credentialsToAccount
import com.daniebeler.pfpixelix.ui.composables.custom_account.CustomAccount
import com.daniebeler.pfpixelix.ui.navigation.Destination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.add_pixelfed_account
import pixelix.app.generated.resources.are_you_sure_you_want_to_remove_this_account
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.current_account
import pixelix.app.generated.resources.other_accounts
import pixelix.app.generated.resources.remove
import pixelix.app.generated.resources.remove_account

@Composable
fun AccountSwitchBottomSheet(
    navController: NavController,
    closeBottomSheet: () -> Unit,
    ownProfileViewModel: OwnProfileViewModel?,
    viewModel: AccountSwitchViewModel = injectViewModel(key = "account_switcher_viewmodel") { accountSwitchViewModel }
) {
    val showRemoveLoginDataAlert = remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val sessionStorage = viewModel.sessionStorage
        if (viewModel.currentCredentials != null) {
            Text(
                text = stringResource(Res.string.current_account),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
            CustomAccount(account = credentialsToAccount(viewModel.currentCredentials!!), showFollowers = false)
        }
        val otherAccounts = remember(sessionStorage) {
            viewModel.sessionStorage?.sessions?.filter { it.accountId != (viewModel.currentCredentials?.accountId ?: "") }.orEmpty()
        }
        if (otherAccounts.isNotEmpty()) {
            Text(
                text = stringResource(Res.string.other_accounts),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
            otherAccounts.map { otherAccount ->
                Box(Modifier.clickable {
                    viewModel.switchAccount(otherAccount) {
                        ownProfileViewModel?.let {
                            ownProfileViewModel.updateAccountSwitch()
                        }
                        closeBottomSheet()
                    }
                }) {
                    CustomAccount(
                        account = credentialsToAccount(otherAccount),
                        logoutButton = true,
                        showFollowers = false,
                        logout = { showRemoveLoginDataAlert.value = otherAccount.accountId })
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clickable {
                    navController.navigate(Destination.NewLogin)
                    closeBottomSheet()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(46.dp)
                    .height(46.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "add account",
                    Modifier
                        .height(32.dp)
                        .width(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(Res.string.add_pixelfed_account),
                lineHeight = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (showRemoveLoginDataAlert.value.isNotBlank()) {
        AlertDialog(title = {
            Text(text = stringResource(Res.string.remove_account))
        }, text = {
            Text(text = stringResource(Res.string.are_you_sure_you_want_to_remove_this_account))
        }, onDismissRequest = {
            showRemoveLoginDataAlert.value = ""
        }, confirmButton = {
            TextButton(onClick = {
                CoroutineScope(Dispatchers.Default).launch {
                    viewModel.removeAccount(showRemoveLoginDataAlert.value)
                    showRemoveLoginDataAlert.value = ""
                }
            }) {
                Text(stringResource(Res.string.remove))
            }
        }, dismissButton = {
            TextButton(onClick = {
                showRemoveLoginDataAlert.value = ""
            }) {
                Text(stringResource(Res.string.cancel))
            }
        })
    }
}

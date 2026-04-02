package com.daniebeler.pfpixelix.ui.composables.settings.muted_accounts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.ui.composables.profile.other_profile.UnMuteAccountAlert
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.default_avatar
import pixelix.app.generated.resources.unmute

@Composable
fun CustomMutedAccountRow(
    account: Account,
    navController: NavController,
    viewModel: MutedAccountsViewModel
) {
    Row(
        Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {

        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                navController.navigate(Destination.Profile(account.id))
            }
        ) {
            AsyncImage(
                model = account.avatar,
                error = painterResource(Res.drawable.default_avatar),
                contentDescription = "",
                modifier = Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))

            Text(text = account.username, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = {
                viewModel.unmuteAccountAlert = account.id
            }) {
                Text(text = stringResource(Res.string.unmute))
            }
        }
    }

    if (viewModel.unmuteAccountAlert == account.id) {
        UnMuteAccountAlert(
            onDismissRequest = { viewModel.unmuteAccountAlert = "" },
            onConfirmation = {
                viewModel.unmuteAccountAlert = ""
                viewModel.unmuteAccount(account.id) },
            account = account
        )
    }
}
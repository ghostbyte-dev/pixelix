package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.service.platform.PlatformFeatures
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.AutoplayVideoPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.CaptionTemplate
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.ClearCachePref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.CustomizeAppIconPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.DefaultVisibilityPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.DeleteAccountPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.HideAltTextButtonPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.HideMetadataPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.HideSensitiveContentPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.LogoutPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.MoreSettingsPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.RepostSettingsPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.SwipeBetweenTimelines
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.ThemePref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.UseInAppBrowserPref
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.close
import pixelix.app.generated.resources.settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesComposable(
    navController: NavController,
    drawerState: DrawerState,
    closePreferencesDrawer: () -> Unit,
    viewModel: PreferencesViewModel = injectViewModel(key = "preferences-viewmodel-key") { preferencesViewModel }
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(scrollBehavior = scrollBehavior, title = {
                Text(
                    text = stringResource(Res.string.settings),
                    style = MaterialTheme.typography.headlineSmall
                )
            }, navigationIcon = {
                IconButton(onClick = {
                    closePreferencesDrawer()
                }) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.close), contentDescription = ""
                    )
                }
            })
        }) { paddingValues ->
        Column(
            Modifier.padding(paddingValues).padding(horizontal = 18.dp).fillMaxSize()
                .verticalScroll(state = rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Content settings",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 6.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))

            HideSensitiveContentPref()

            HideAltTextButtonPref()

            if (viewModel.capabilities.post.showCameraMetadata) {
                HideMetadataPref()
            }

            if (PlatformFeatures.inAppBrowser) {
                UseInAppBrowserPref()
            }

            AutoplayVideoPref()

            SwipeBetweenTimelines()


            if (viewModel.capabilities.profile.showRepostSettings) {
                RepostSettingsPref { viewModel.openRepostSettings() }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "App customization",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 6.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))

            ThemePref()

            if (PlatformFeatures.customAppIcon) {
                val icon = viewModel.appIcon.collectAsState()
                CustomizeAppIconPref(navController, closePreferencesDrawer, icon.value)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "New post settings",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 6.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))

            CaptionTemplate(viewModel.suggestionsManager)
            DefaultVisibilityPref(viewModel.capabilities)

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Other",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 6.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))

            ClearCachePref(drawerState)

            MoreSettingsPref { viewModel.openMoreSettingsPage() }

            LogoutPref { viewModel.logout() }

            DeleteAccountPref { viewModel.openDeleteAccountPage() }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Pixelix v" + viewModel.versionName,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
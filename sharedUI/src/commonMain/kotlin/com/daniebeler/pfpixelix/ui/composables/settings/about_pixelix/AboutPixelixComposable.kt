package com.daniebeler.pfpixelix.ui.composables.settings.about_pixelix

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.widgets.ButtonRowElement
import com.daniebeler.pfpixelix.ui.composables.widgets.ScreenScaffold
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.about_pixelix
import pixelix.app.generated.resources.browsers_outline
import pixelix.app.generated.resources.code_slash_outline
import pixelix.app.generated.resources.developed_by
import pixelix.app.generated.resources.mastodon_logo
import pixelix.app.generated.resources.pixelfed_logo
import pixelix.app.generated.resources.shield_outline
import pixelix.app.generated.resources.star
import pixelix.app.generated.resources.website

@Composable
fun AboutPixelixComposable(
    navController: NavController,
    viewModel: AboutPixelixViewModel = injectViewModel(key = "about-pixelix-viewmodel-key") { aboutPixelixViewModel }
) {
    val scrollState = rememberScrollState()

    ScreenScaffold(title = stringResource(Res.string.about_pixelix), navController = navController) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(bottom = 60.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 80.dp, bottom = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val icon = viewModel.appIcon.collectAsState()
                Image(
                    painterResource(icon.value),
                    contentDescription = null,
                    Modifier.width(84.dp).height(84.dp).clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Pixelix", fontSize = 36.sp, fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Version " + viewModel.versionName,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                )

            }

            HorizontalDivider(Modifier.padding(12.dp))

            ButtonRowElement(
                icon = Res.drawable.star,
                text = "Rate Pixelix on Google Play Store",
                onClick = { viewModel.rateApp() })

            HorizontalDivider(Modifier.padding(12.dp))

            ButtonRowElement(
                icon = Res.drawable.browsers_outline,
                text = "Homepage",
                smallText = "https://app.pixelix.social",
                onClick = { viewModel.openUrl("https://app.pixelix.social") })

            ButtonRowElement(
                icon = Res.drawable.shield_outline,
                text = "Privacy Policy",
                smallText = "https://app.pixelix.social/privacy",
                onClick = { viewModel.openUrl("https://app.pixelix.social/privacy") })

            ButtonRowElement(
                icon = Res.drawable.code_slash_outline,
                text = "Source Code",
                smallText = "https://github.com/daniebeler/pixelix",
                onClick = { viewModel.openUrl("https://github.com/daniebeler/pixelix") })


            HorizontalDivider(Modifier.padding(12.dp))


            Text(
                text = stringResource(Res.string.developed_by),
                fontSize = 18.sp,
                modifier = Modifier.padding(12.dp, 0.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                Text(text = "Emanuel Hiebeler", fontWeight = FontWeight.Bold)

                Row {
                    Image(
                        painter = painterResource(Res.drawable.pixelfed_logo),
                        contentDescription = null,
                        Modifier.width(32.dp).height(32.dp).clickable {
                            navController.navigate(Destination.ProfileByUsername("hiebeler05@pixelix.social"))
                        })

                    Spacer(modifier = Modifier.width(16.dp))

                    Image(
                        painter = painterResource(Res.drawable.mastodon_logo),
                        contentDescription = null,
                        Modifier.width(32.dp).height(32.dp).clickable {
                            viewModel.openUrl("https://techhub.social/@Hiebeler05")
                        })

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        imageVector = vectorResource(Res.drawable.website),
                        contentDescription = "",
                        Modifier.size(32.dp).clickable {
                            viewModel.openUrl("https://emanuelhiebeler.me")
                        },
                        tint = Color(0xFF4793FF)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                Text(text = "Daniel Hiebeler", fontWeight = FontWeight.Bold)

                Row {
                    Image(
                        painter = painterResource(Res.drawable.pixelfed_logo),
                        contentDescription = null,
                        Modifier.width(32.dp).height(32.dp).clickable {
                            navController.navigate(Destination.ProfileByUsername("daniebeler@pixelix.social"))
                        })

                    Spacer(modifier = Modifier.width(16.dp))

                    Image(
                        painter = painterResource(Res.drawable.mastodon_logo),
                        contentDescription = null,
                        Modifier.width(32.dp).height(32.dp).clickable {
                            viewModel.openUrl("https://techhub.social/@daniebeler")
                        })

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        imageVector = vectorResource(Res.drawable.website),
                        contentDescription = "",
                        Modifier.size(32.dp).clickable {
                            viewModel.openUrl("https://daniebeler.com")
                        },
                        tint = Color(0xFF4793FF)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                Text(text = "Konstantin Tskhovrebov", fontWeight = FontWeight.Bold)

                Row {
                    Image(
                        painter = painterResource(Res.drawable.pixelfed_logo),
                        contentDescription = null,
                        Modifier.width(32.dp).height(32.dp).clickable {
                            navController.navigate(Destination.ProfileByUsername("dagboek@pixey.org"))
                        })

                    Spacer(modifier = Modifier.width(16.dp))

                    Image(
                        painter = painterResource(Res.drawable.mastodon_logo),
                        contentDescription = null,
                        Modifier.width(32.dp).height(32.dp).clickable {
                            viewModel.openUrl("https://androiddev.social/@terrakok")
                        })

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        imageVector = vectorResource(Res.drawable.website),
                        contentDescription = "",
                        Modifier.size(32.dp).clickable {
                            viewModel.openUrl("https://github.com/terrakok")
                        },
                        tint = Color(0xFF4793FF)
                    )
                }
            }
        }
    }
}

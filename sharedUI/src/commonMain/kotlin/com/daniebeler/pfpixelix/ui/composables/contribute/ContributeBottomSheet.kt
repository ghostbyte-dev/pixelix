package com.daniebeler.pfpixelix.ui.composables.contribute

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.domain.service.platform.PlatformFeatures
import com.daniebeler.pfpixelix.ui.composables.widgets.CardButton
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.bug
import pixelix.app.generated.resources.chevron_right
import pixelix.app.generated.resources.coffee
import pixelix.app.generated.resources.feedback
import pixelix.app.generated.resources.feedback_btn_desc
import pixelix.app.generated.resources.feedback_btn_title
import pixelix.app.generated.resources.feedback_desc
import pixelix.app.generated.resources.feedback_title
import pixelix.app.generated.resources.report_btn_desc
import pixelix.app.generated.resources.report_btn_title
import pixelix.app.generated.resources.report_desc
import pixelix.app.generated.resources.report_title
import pixelix.app.generated.resources.sponsor_btn_desc
import pixelix.app.generated.resources.sponsor_btn_title
import pixelix.app.generated.resources.sponsor_desc
import pixelix.app.generated.resources.sponsor_title
import pixelix.app.generated.resources.support_headline
import pixelix.app.generated.resources.translation
import pixelix.app.generated.resources.translations_btn_desc
import pixelix.app.generated.resources.translations_btn_title
import pixelix.app.generated.resources.translations_desc
import pixelix.app.generated.resources.translations_title


@Composable
fun ContributeBottomSheet(openUrl: (url: String) -> Unit) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            Text(
                text = stringResource(Res.string.support_headline),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 12.dp)
            )

            if (PlatformFeatures.supportsSponsorship) {
                ContributeCard(
                    title = stringResource(Res.string.sponsor_title),
                    desc = stringResource(Res.string.sponsor_desc),
                    onClick = { openUrl("https://github.com/ghostbyte-dev/pixelix/wiki/Sponsor") },
                    buttonTitle = stringResource(Res.string.sponsor_btn_title),
                    buttonDesc = stringResource(Res.string.sponsor_btn_desc),
                    buttonIcon = Res.drawable.coffee
                )
            }

            ContributeCard(
                title = stringResource(Res.string.report_title),
                desc = stringResource(Res.string.report_desc),
                onClick = { openUrl("https://github.com/ghostbyte-dev/pixelix/issues") },
                buttonTitle = stringResource(Res.string.report_btn_title),
                buttonDesc = stringResource(Res.string.report_btn_desc),
                buttonIcon = Res.drawable.bug
            )

            ContributeCard(
                stringResource(Res.string.feedback_title),
                desc = stringResource(Res.string.feedback_desc),
                onClick = { openUrl("https://app.formbricks.com/s/cmplkixsldbx0qh01yijw6fcy") },
                buttonTitle = stringResource(Res.string.feedback_btn_title),
                buttonDesc = stringResource(Res.string.feedback_btn_desc),
                buttonIcon = Res.drawable.feedback
            )

            ContributeCard(
                title = stringResource(Res.string.translations_title),
                desc = stringResource(Res.string.translations_desc),
                onClick = { openUrl("https://hosted.weblate.org/projects/pixelix/") },
                buttonTitle = stringResource(Res.string.translations_btn_title),
                buttonDesc = stringResource(Res.string.translations_btn_desc),
                buttonIcon = Res.drawable.translation
            )

            Spacer(modifier = Modifier.height(18.dp))

        }
    }
}

@Composable
fun ContributeCard(
    title: String,
    desc: String,
    onClick: () -> Unit = {},
    buttonTitle: String,
    buttonDesc: String,
    buttonIcon: DrawableResource
) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = desc, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))


        CardButton(
            leadingIcon = buttonIcon,
            title = buttonTitle,
            desc = buttonDesc,
            onClick = onClick,
            trailingContent = Res.drawable.chevron_right
        )

    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun ContributeBottomSheetPreview() {
    MaterialTheme {
        ContributeBottomSheet(openUrl = {})
    }
}

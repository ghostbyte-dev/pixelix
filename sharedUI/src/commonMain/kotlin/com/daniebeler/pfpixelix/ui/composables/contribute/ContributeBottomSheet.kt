package com.daniebeler.pfpixelix.ui.composables.contribute

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.ui.composables.widgets.CardButton
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.bug
import pixelix.app.generated.resources.chevron_forward_outline
import pixelix.app.generated.resources.coffee
import pixelix.app.generated.resources.feedback
import pixelix.app.generated.resources.translation


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
                text = "Help improve Pixelix",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 12.dp)
            )
            ContributeCard(
                title = "Sponsor",
                desc = "Pixelix is built and maintained in our free time. If you enjoy using it, you can help support ongoing development and infrastructure costs.",
                onClick = { openUrl("https://github.com/ghostbyte-dev/pixelix/wiki/Sponsor") },
                buttonTitle = "Sponsor",
                buttonDesc = "Support the creators with a coffee",
                buttonIcon = Res.drawable.coffee
            )

            ContributeCard(
                title = "Report a bug",
                desc = "Found something that isn’t working correctly? Let us know so we can fix it. Every report helps improve Pixelix for everyone.",
                onClick = { openUrl("https://github.com/ghostbyte-dev/pixelix/issues") },
                buttonTitle = "Report an issue",
                buttonDesc = "We appreciate your help tracking down bugs",
                buttonIcon = Res.drawable.bug
            )

            ContributeCard(
                "Share feedback",
                desc = "Have an idea for a feature or improvement? We’d love to hear it. Suggestions help shape the future of Pixelix.",
                onClick = { openUrl("https://github.com/ghostbyte-dev/pixelix/issues") },
                buttonTitle = "Share feedback",
                buttonDesc = "Your ideas directly improve the app",
                buttonIcon = Res.drawable.feedback
            )

            ContributeCard(
                title = "Translations",
                desc = "Want to see Pixelix in your language? We rely on our amazing community to help make the app accessible to everyone around the world.",
                onClick = { openUrl("https://hosted.weblate.org/projects/pixelix/") },
                buttonTitle = "Help translate",
                buttonDesc = "Share your language skills with the community",
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
            trailingContent = Res.drawable.chevron_forward_outline
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

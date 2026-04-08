package com.daniebeler.pfpixelix.ui.composables.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.abusive_or_harmful
import pixelix.app.generated.resources.adult_or_sensitive_content
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.chevron_forward_outline
import pixelix.app.generated.resources.copyright_infringement
import pixelix.app.generated.resources.impersonation
import pixelix.app.generated.resources.ok
import pixelix.app.generated.resources.scam
import pixelix.app.generated.resources.spam
import pixelix.app.generated.resources.terrorism
import pixelix.app.generated.resources.underage_account
import pixelix.app.generated.resources.violence
import pixelix.app.generated.resources.report
import pixelix.app.generated.resources.reported

@Composable
fun ReportDialog(
    dismissDialog: () -> Unit,
    reportState: ReportState?,
    report: (category: String) -> Unit
) {
    Dialog(
        onDismissRequest = dismissDialog,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (reportState != null) {
                    if (reportState.isLoading) {
                        CircularProgressIndicator()
                    } else if (reportState.error.isNotBlank()) {
                        Text("an unexpected error occurred")
                    } else {
                        Text(stringResource(Res.string.reported))
                    }
                    HorizontalDivider()
                    Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { dismissDialog() }) {
                            Text(stringResource(Res.string.ok))
                        }
                    }
                } else {
                    Text(stringResource(Res.string.report), style = MaterialTheme.typography.headlineSmall)
                    HorizontalDivider()
                    ReportCategoryButton(Res.string.spam, { report("spam") })
                    ReportCategoryButton(Res.string.adult_or_sensitive_content, { report("sensitive") })
                    ReportCategoryButton(Res.string.abusive_or_harmful, { report("abusive") })
                    ReportCategoryButton(Res.string.underage_account, { report("underage") })
                    ReportCategoryButton(Res.string.violence, { report("violence") })
                    ReportCategoryButton(Res.string.copyright_infringement, { report("copyright") })
                    ReportCategoryButton(Res.string.impersonation, { report("impersonation") })
                    ReportCategoryButton(Res.string.scam, { report("scam") })
                    ReportCategoryButton(Res.string.terrorism, { report("terrorism") })

                    HorizontalDivider()
                    Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { dismissDialog() }) {
                            Text(stringResource(Res.string.cancel))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportCategoryButton(category: StringResource, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onClick() }) {
        Text(stringResource(category))
        Icon(
            imageVector = vectorResource(Res.drawable.chevron_forward_outline),
            contentDescription = stringResource(category),
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
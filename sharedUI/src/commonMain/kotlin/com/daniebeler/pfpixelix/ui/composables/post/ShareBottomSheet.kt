package com.daniebeler.pfpixelix.ui.composables.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.LocalSnackbarPresenter
import com.daniebeler.pfpixelix.domain.model.MediaAttachment
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.Visibility
import com.daniebeler.pfpixelix.domain.service.platform.PlatformFeatures
import com.daniebeler.pfpixelix.ui.composables.ButtonRowElement
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.audience_public
import pixelix.app.generated.resources.cloud_download_outline
import pixelix.app.generated.resources.delete_this_post
import pixelix.app.generated.resources.document_text_outline
import pixelix.app.generated.resources.download_image
import pixelix.app.generated.resources.edit_post
import pixelix.app.generated.resources.eye_outline
import pixelix.app.generated.resources.followers_only
import pixelix.app.generated.resources.license
import pixelix.app.generated.resources.open_in_browser
import pixelix.app.generated.resources.open_outline
import pixelix.app.generated.resources.pencil_outline
import pixelix.app.generated.resources.report_this_post
import pixelix.app.generated.resources.share_social_outline
import pixelix.app.generated.resources.share_this_post
import pixelix.app.generated.resources.trash_outline
import pixelix.app.generated.resources.unlisted
import pixelix.app.generated.resources.visibility_x
import pixelix.app.generated.resources.warning

@Composable
fun ShareBottomSheet(
    url: String,
    minePost: Boolean,
    viewModel: PostViewModel,
    post: Post,
    currentMediaAttachmentNumber: Int,
    navController: NavController,
    closeBottomSheet: () -> Unit
) {

    var humanReadableVisibility by remember {
        mutableStateOf("")
    }

    var isReportDialogOpen by remember { mutableStateOf(false) }

    val mediaAttachment: MediaAttachment? = viewModel.post?.mediaAttachments?.let { attachments ->
        if (attachments.isNotEmpty() && currentMediaAttachmentNumber in attachments.indices) {
            attachments[currentMediaAttachmentNumber]
        } else {
            null
        }
    }

    LaunchedEffect(Unit) {
        humanReadableVisibility = when (post.visibility) {
            Visibility.PUBLIC -> getString(Res.string.audience_public)
            Visibility.UNLISTED -> getString(Res.string.unlisted)
            Visibility.PRIVATE -> getString(Res.string.followers_only)
            else -> ""
        }
    }


    Column(
        modifier = Modifier.padding(bottom = 32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.eye_outline),
                contentDescription = "",
                Modifier.padding(start = 18.dp, top = 12.dp, bottom = 12.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(text = stringResource(Res.string.visibility_x, humanReadableVisibility))
        }
        if (mediaAttachment?.license != null) {
            ButtonRowElement(
                icon = Res.drawable.document_text_outline, text = stringResource(
                    Res.string.license, mediaAttachment.license.title
                ), onClick = {
                    viewModel.openUrl(mediaAttachment.license.url)
                    closeBottomSheet()
                })
        }

        HorizontalDivider(Modifier.padding(12.dp))

        ButtonRowElement(
            icon = Res.drawable.open_outline, text = stringResource(
                Res.string.open_in_browser
            ), onClick = {
                viewModel.openUrl(url)
                closeBottomSheet()
            })

        ButtonRowElement(
            icon = Res.drawable.share_social_outline,
            text = stringResource(Res.string.share_this_post),
            onClick = {
                viewModel.shareText(url)
                closeBottomSheet()
            })

        if (
            PlatformFeatures.downloadToGallery &&
            mediaAttachment?.url != null
        ) {
            val snackbarPresenter = LocalSnackbarPresenter.current
            ButtonRowElement(
                icon = Res.drawable.cloud_download_outline,
                text = stringResource(Res.string.download_image),
                onClick = {
                    viewModel.saveImage(mediaAttachment.url)
                    snackbarPresenter("Image saved to the gallery")
                    closeBottomSheet()
                }
            )
        }

        if (minePost) {
            HorizontalDivider(Modifier.padding(12.dp))

            ButtonRowElement(
                icon = Res.drawable.pencil_outline,
                text = stringResource(Res.string.edit_post),
                onClick = {
                    navController.navigate(Destination.EditPost(post.id))
                }
            )
            ButtonRowElement(
                icon = Res.drawable.trash_outline,
                text = stringResource(Res.string.delete_this_post),
                onClick = {
                    viewModel.deleteDialog = post.id
                },
                color = MaterialTheme.colorScheme.error
            )
        } else {
            HorizontalDivider(Modifier.padding(12.dp))

            ButtonRowElement(
                icon = Res.drawable.warning,
                text = stringResource(Res.string.report_this_post),
                onClick = {
                    isReportDialogOpen = true
                },
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    if (isReportDialogOpen) {
        ReportDialog(
            dismissDialog = {
                isReportDialogOpen = false
                viewModel.reportState = null
            },
            reportState = viewModel.reportState
        ) { category ->
            viewModel.reportPost(category)
            viewModel.reportState = null
        }
    }
}

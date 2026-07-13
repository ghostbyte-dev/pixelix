package com.daniebeler.pfpixelix.ui.composables.newpost

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.utils.parseExifMetadata
import com.daniebeler.pfpixelix.utils.toKmpUri
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.photo

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EmptyImageTab(addImage: (KmpUri, MediaAttachmentMetadataRequest) -> Unit) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val launcher = rememberFilePickerLauncher(
            type = FileKitType.ImageAndVideo, mode = FileKitMode.Multiple()
        ) { files ->
            files?.forEach { file ->
                scope.launch(Dispatchers.Default) {
                    try {
                        val bytes = file.readBytes()

                        val extractedMetadata = parseExifMetadata(bytes)

                        withContext(Dispatchers.Main) {
                            addImage(file.toKmpUri(), extractedMetadata)
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        }

        Text("Select one or more images to add them to your post", textAlign = TextAlign.Center)
        Spacer(Modifier.height(12.dp))

        val buttonSize = ButtonDefaults.MediumContainerHeight
        Button(
            contentPadding = ButtonDefaults.contentPaddingFor(buttonSize, hasStartIcon = true),
            onClick = {
                launcher.launch()
            },
        ) {

            Icon(
                vectorResource(Res.drawable.photo),
                contentDescription = "",
                modifier = Modifier.size(ButtonDefaults.iconSizeFor(buttonSize)),
            )
            Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(buttonSize)))
            Text("Select images", style = ButtonDefaults.textStyleFor(buttonSize))
        }
    }
}

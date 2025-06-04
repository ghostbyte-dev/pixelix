package com.daniebeler.pfpixelix

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.ref.WeakReference

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        FileKit.init(this)
        MyApplication.currentActivity = WeakReference(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App(MyApplication.appComponent) { finish() }
        }
        if (savedInstanceState == null) {
            handleNewIntent(intent, this)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNewIntent(intent, this)
    }

    private fun handleNewIntent(intent: Intent, appActivity: AppActivity) {
        when (intent.action) {
            Intent.ACTION_VIEW -> {
                intent.dataString?.let { onExternalUrl(it) }
            }
            Intent.ACTION_SEND, Intent.ACTION_SEND_MULTIPLE -> {
                val imageUris = handleSharePhotoIntent(intent, contentResolver, cacheDir, appActivity)
                if (imageUris.isNotEmpty()) {
                    imageUris.forEach { uri ->
                        try {
                            contentResolver.takePersistableUriPermission(
                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                        } catch (e: SecurityException) {
                            e.printStackTrace() // Handle permission denial gracefully
                        }
                    }
                    onExternalFileShare(imageUris)
                }
            }
        }
    }

    private fun onExternalUrl(url: String) {
        val systemUrlHandler = MyApplication.appComponent.systemUrlHandler
        systemUrlHandler.onRedirect(url)
    }

    private fun onExternalFileShare(uris: List<Uri>) {
        val systemFileShare = MyApplication.appComponent.systemFileShare
        systemFileShare.share(uris)
    }
}

actual fun EdgeToEdgeDialogProperties(
    dismissOnBackPress: Boolean,
    dismissOnClickOutside: Boolean,
    usePlatformDefaultWidth: Boolean
): DialogProperties = DialogProperties(
    dismissOnBackPress = dismissOnBackPress,
    dismissOnClickOutside = dismissOnClickOutside,
    usePlatformDefaultWidth = usePlatformDefaultWidth,
    decorFitsSystemWindows = false
)

private fun saveUriToCache(uri: Uri, contentResolver: ContentResolver, cacheDir: File, appActivity: AppActivity): Uri? {
    try {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        inputStream?.use { input ->
            val file = File(cacheDir, "shared_image_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
            return FileProvider.getUriForFile(
                appActivity,
                "${appActivity.packageName}.provider",
                file
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

private fun handleSharePhotoIntent(
    intent: Intent, contentResolver: ContentResolver, cacheDir: File, appActivity: AppActivity
): List<Uri> {
    val action = intent.action
    val type = intent.type
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    var imageUris: List<Uri> = emptyList()
    when {
        Intent.ACTION_SEND == action && type != null -> {
            if (type.startsWith("image/") || type.startsWith("video/")) {
                val singleUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(
                        Intent.EXTRA_STREAM, Uri::class.java
                    )
                } else {
                    @Suppress("DEPRECATION") intent.getParcelableExtra(
                        Intent.EXTRA_STREAM
                    ) as? Uri
                }
                singleUri?.let { uri ->
                    val cachedUri = saveUriToCache(uri, contentResolver, cacheDir, appActivity)
                    imageUris =
                        cachedUri?.let { listOf(it) } ?: emptyList() // Wrap single image in a list
                }
            }
        }

        Intent.ACTION_SEND_MULTIPLE == action && type != null -> {
            val receivedUris = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayListExtra(
                    Intent.EXTRA_STREAM, Uri::class.java
                )
            } else {
                @Suppress("DEPRECATION") intent.getParcelableArrayListExtra(
                    Intent.EXTRA_STREAM
                )
            }
            imageUris = receivedUris?.mapNotNull {
                saveUriToCache(
                    it, contentResolver, cacheDir, appActivity
                )
            } ?: emptyList()
        }
    }
    return imageUris
}
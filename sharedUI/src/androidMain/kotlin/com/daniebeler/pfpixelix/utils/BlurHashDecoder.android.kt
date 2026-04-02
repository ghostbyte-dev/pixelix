package com.daniebeler.pfpixelix.utils

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream


actual fun createBitmap(pixels: IntArray, width: Int, height: Int): ImageBitmap =
    Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888).asImageBitmap()

actual fun ImageBitmap.encodeToPngBytes(quality: Int): ByteArray? {
    ByteArrayOutputStream().use { bytes ->
        this.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, quality, bytes)
        return bytes.toByteArray()
    }
}
package com.daniebeler.pfpixelix.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

actual fun createBitmap(
    pixels: IntArray,
    width: Int,
    height: Int
) = ImageBitmap(width, height, ImageBitmapConfig.Argb8888).also { bitmap ->
    val pixelBytes = ByteArray(pixels.size * 4) { i ->
        val pixel = pixels[i / 4]
        val color = when (i % 4) {
            0 -> (pixel shr 16 and 0xFF)// Red
            1 -> (pixel shr 8 and 0xFF)// Green
            2 -> (pixel and 0xFF)// Blue
            else -> (pixel shr 24 and 0xFF)// Alpha
        }
        color.toByte()
    }
    bitmap.asSkiaBitmap().installPixels(pixelBytes)
}

actual fun ImageBitmap.encodeToPngBytes(quality: Int): ByteArray? {
    return Image.makeFromBitmap(this.asSkiaBitmap())
        .encodeToData(EncodedImageFormat.PNG, quality)
        ?.bytes
}
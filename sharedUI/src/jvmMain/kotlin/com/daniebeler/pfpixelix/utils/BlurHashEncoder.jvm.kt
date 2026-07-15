package com.daniebeler.pfpixelix.utils

import com.vanniktech.blurhash.BlurHash
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

actual object BlurHashEncoder {
    actual fun encode(byteArray: ByteArray): String? {
        val inputStream = ByteArrayInputStream(byteArray)
        return BlurHash.encode(ImageIO.read(inputStream), 4, 3)
    }
}
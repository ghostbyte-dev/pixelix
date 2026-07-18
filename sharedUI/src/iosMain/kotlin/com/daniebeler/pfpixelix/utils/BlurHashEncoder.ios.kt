package com.daniebeler.pfpixelix.utils

import com.vanniktech.blurhash.BlurHash
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.dataWithBytes
import platform.UIKit.UIImage


actual object BlurHashEncoder {
    @OptIn(ExperimentalForeignApi::class)
    actual fun encode(byteArray: ByteArray): String? {
        val nsData = byteArray.usePinned { pinned ->
            platform.Foundation.NSData.dataWithBytes(pinned.addressOf(0), byteArray.size.toULong())
        }
        val uiImage = UIImage.imageWithData(nsData) ?: error("Failed to create UIImage from bytes")

        return BlurHash.encode(uiImage, 4, 3)
    }
}
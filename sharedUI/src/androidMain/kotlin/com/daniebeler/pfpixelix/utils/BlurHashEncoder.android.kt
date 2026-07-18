package com.daniebeler.pfpixelix.utils

import android.graphics.BitmapFactory
import com.vanniktech.blurhash.BlurHash

actual object BlurHashEncoder {
    actual fun encode(byteArray: ByteArray): String? {
        return BlurHash.encode(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size), 4, 4)
    }
}
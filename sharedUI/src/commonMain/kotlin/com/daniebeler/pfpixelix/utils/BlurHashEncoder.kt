package com.daniebeler.pfpixelix.utils

expect object BlurHashEncoder {
    fun encode(byteArray: ByteArray): String?
}
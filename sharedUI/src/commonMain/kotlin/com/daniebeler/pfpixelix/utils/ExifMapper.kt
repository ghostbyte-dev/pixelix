package com.daniebeler.pfpixelix.utils

fun getFlashReadableString(flashValue: String?): String {
    val code = flashValue?.toIntOrNull() ?: return "Unknown"
    return when (code) {
        0 -> "Flash did not fire"
        1 -> "Flash fired"
        5 -> "Flash fired, strobe return light not detected"
        7 -> "Flash fired, strobe return light detected"
        8 -> "Flash did not fire, compulsory flash mode"
        9 -> "Flash fired, compulsory flash mode"
        13 -> "Flash fired, compulsory flash mode, return light not detected"
        15 -> "Flash fired, compulsory flash mode, return light detected"
        16 -> "Flash did not fire, compulsory flash mode" // <-- Your value 16!
        24 -> "Flash did not fire, auto mode"
        25 -> "Flash fired, auto mode"
        29 -> "Flash fired, auto mode, return light not detected"
        31 -> "Flash fired, auto mode, return light detected"
        32 -> "No flash function"
        65 -> "Flash fired, red-eye reduction mode"
        else -> "Flash status code: $code"
    }
}
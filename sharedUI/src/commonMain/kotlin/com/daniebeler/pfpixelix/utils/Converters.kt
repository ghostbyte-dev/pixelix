package com.daniebeler.pfpixelix.utils

fun String?.toCoordinateOrNull(): Double? =
    this
        ?.trim()
        ?.replace(",", ".")
        ?.toDoubleOrNull()
        ?.takeIf { it.isFinite() }
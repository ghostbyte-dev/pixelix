package com.daniebeler.pfpixelix.domain.service.platform

actual object PlatformFeatures {
    actual val notificationWidgets = false
    actual val inAppBrowser = true
    actual val downloadToGallery = false //https://github.com/vinceglb/FileKit/issues/215
    actual val customAppIcon = true
    actual val addCollection = false
    actual val customAccentColors = true
}
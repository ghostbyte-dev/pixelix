package com.daniebeler.pfpixelix.domain.service.preferences

import com.daniebeler.pfpixelix.domain.model.AppAccentColor
import com.daniebeler.pfpixelix.domain.model.AppThemeMode
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.boolean
import com.russhwolf.settings.coroutines.toBlockingSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import com.russhwolf.settings.int
import com.russhwolf.settings.long
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
@Inject
class UserPreferences(observableSettings: DataStoreSettings) {
    private val settings = observableSettings.toBlockingSettings()

    var hideSensitiveContent by settings.boolean("k_hide_sensitive_content", true)
    var blurSensitiveContent by settings.boolean("k_blur_sensitive_content", true)
    var blurSensitiveContentFlow = observableSettings.getBooleanFlow("k_blur_sensitive_content", blurSensitiveContent)

    var useInAppBrowser by settings.boolean("k_use_in_app_browser", true)

    var hideAltTextButton by settings.boolean("k_hide_alt_text_button", false)
    val hideAltTextButtonFlow = observableSettings.getBooleanFlow("k_hide_alt_text_button", hideAltTextButton)

    var focusMode by settings.boolean("k_focus_mode", false)
    val focusModeFlow = observableSettings.getBooleanFlow("k_focus_mode", focusMode)

    var autoplayVideo by settings.boolean("k_autoplay_mode", true)
    val autoplayVideoFlow = observableSettings.getBooleanFlow("k_autoplay_mode", autoplayVideo)


    var showUserGridTimeline by settings.int("k_timeline_view", 1)
    val showUserGridTimelineFlow = observableSettings.getIntFlow("k_timeline_view", showUserGridTimeline)

    var enableVolume by settings.boolean("k_enable_volume", true)
    val enableVolumeFlow = observableSettings.getBooleanFlow("k_enable_volume", enableVolume)

    var appThemeMode by settings.int("k_theme_mode", AppThemeMode.FOLLOW_SYSTEM)
    val appThemeModeFlow = observableSettings.getIntFlow("k_theme_mode", appThemeMode)

    var accentColor by settings.long("k_accent_color", AppAccentColor.GREEN)
    val accentColorFlow = observableSettings.getLongFlow("k_accent_color", accentColor)

    var enableSwipeBetweenTabs by settings.boolean("k_enable_swipe_between_timelines", true)
    val enableSwipeBetweenTabsFlow = observableSettings.getBooleanFlow("k_enable_swipe_between_timelines", enableSwipeBetweenTabs)

    var showHomeTimelineHelp by settings.boolean("k_show_home_timeline_help", true)
    val showHomeTimelineHelpFlow = observableSettings.getBooleanFlow("k_show_home_timeline_help", showHomeTimelineHelp)

    var showLocalTimelineHelp by settings.boolean("k_show_local_timeline_help", true)
    val showLocalTimelineHelpFlow = observableSettings.getBooleanFlow("k_show_local_timeline_help", showLocalTimelineHelp)

    var showGlobalTimelineHelp by settings.boolean("k_show_global_timeline_help", true)
    val showGlobalTimelineHelpFlow = observableSettings.getBooleanFlow("k_show_global_timeline_help", showGlobalTimelineHelp)
}
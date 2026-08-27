package com.daniebeler.pfpixelix.domain.service.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.AppAccentColor
import com.daniebeler.pfpixelix.domain.model.AppThemeMode
import com.daniebeler.pfpixelix.domain.model.License
import com.daniebeler.pfpixelix.domain.model.Visibility
import com.daniebeler.pfpixelix.domain.service.platform.PlatformFeatures
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Inject
@AppSingleton
class UserPreferences(private val dataStore: DataStore<Preferences>) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // In-memory snapshot backing the synchronous property accessors. Starts empty (so the
    // defaults apply) and is kept up to date as the DataStore emits persisted snapshots.
    private var cache: Preferences = emptyPreferences()

    private inner class Prop<T>(val key: Preferences.Key<T>, val default: T) : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>) = cache[key] ?: default
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            cache = cache.toMutablePreferences().apply { this[key] = value }
            scope.launch { dataStore.edit { it[key] = value } }
        }
    }

    init {
        dataStore.data.onEach { cache = it }.launchIn(scope)
    }

    /**
     * Warms up the in-memory [cache] with the persisted snapshot. Call once during app startup,
     * before the first UI render, so synchronous property reads return persisted values instead
     * of defaults. Non-blocking: it suspends until the first snapshot arrives.
     */
    suspend fun preload() {
        cache = dataStore.data.first()
    }

    var hideSensitiveContent by boolean("k_hide_sensitive_content", true)
    var blurSensitiveContent by boolean("k_blur_sensitive_content", true)
    val blurSensitiveContentFlow = booleanFlow("k_blur_sensitive_content", true)

    var useInAppBrowser by boolean("k_use_in_app_browser", true)

    var hideAltTextButton by boolean("k_hide_alt_text_button", false)
    val hideAltTextButtonFlow = booleanFlow("k_hide_alt_text_button", false)
    var autoplayVideo by boolean("k_autoplay_mode", true)
    val autoplayVideoFlow = booleanFlow("k_autoplay_mode", true)


    var showUserGridTimeline by int("k_timeline_view", 2)
    val showUserGridTimelineFlow = intFlow("k_timeline_view", 2)

    var enableVolume by boolean("k_enable_volume", true)
    val enableVolumeFlow = booleanFlow("k_enable_volume", true)

    var appThemeMode by int("k_theme_mode", AppThemeMode.FOLLOW_SYSTEM)
    val appThemeModeFlow = intFlow("k_theme_mode", AppThemeMode.FOLLOW_SYSTEM)

    var accentColor by string("k_accent_color_enum", AppAccentColor.GREEN.name)
    val accentColorFlow = stringFlow("k_accent_color_enum", AppAccentColor.GREEN.name)

    var useDynamicColors by boolean("k_dynamic_colors", PlatformFeatures.supportsDynamicColors)
    val useDynamicColorsFlow = booleanFlow("k_dynamic_colors", PlatformFeatures.supportsDynamicColors)

    var enableSwipeBetweenTabs by boolean("k_enable_swipe_between_timelines", true)
    val enableSwipeBetweenTabsFlow = booleanFlow("k_enable_swipe_between_timelines", true)

    var showHomeTimelineHelp by boolean("k_show_home_timeline_help", true)
    val showHomeTimelineHelpFlow = booleanFlow("k_show_home_timeline_help", true)

    var showLocalTimelineHelp by boolean("k_show_local_timeline_help", true)
    val showLocalTimelineHelpFlow = booleanFlow("k_show_local_timeline_help", true)

    var showGlobalTimelineHelp by boolean("k_show_global_timeline_help", true)
    val showGlobalTimelineHelpFlow = booleanFlow("k_show_global_timeline_help", true)

    var captionTemplate by string("k_caption_template", "")
    val captionTemplateFlow = stringFlow("k_caption_template", "")

    var hideMetadata by boolean("k_hide_metadata", false)
    val hideMetadataFlow = booleanFlow("k_hide_metadata", false)

    var defaultHomeTab by int("k_default_home_tab", 1)
    val defaultHomeTabFlow = intFlow("k_default_home_tab", 1)

    private var _defaultVisibility: Int by Prop(intPreferencesKey("k_default_visibility"), Visibility.PUBLIC.ordinal)
    var defaultVisibility: Visibility
        get() = Visibility.entries.getOrElse(_defaultVisibility) { Visibility.PUBLIC }
        set(value) {
            _defaultVisibility = value.ordinal
        }

    val defaultVisibilityFlow: Flow<Visibility> =
        intFlow("k_default_visibility", Visibility.PUBLIC.ordinal)
            .map { ordinal ->
                Visibility.entries.getOrElse(ordinal) { Visibility.PUBLIC }
            }

    private var _defaultLicenseJson by string("k_default_license", "")

    var defaultLicense: License?
        get() = _defaultLicenseJson.takeIf { it.isNotEmpty() }?.let { json ->
            runCatching { Json.decodeFromString<License>(json) }.getOrNull()
        }
        set(value) {
            _defaultLicenseJson = if (value != null) Json.encodeToString(value) else ""
        }

    val defaultLicenseFlow: Flow<License?> =
        stringFlow("k_default_license", "")
            .map { json ->
                json.takeIf { it.isNotEmpty() }?.let {
                    runCatching { Json.decodeFromString<License>(it) }.getOrNull()
                }
            }

    private fun boolean(key: String, default: Boolean) = Prop(booleanPreferencesKey(key), default)
    private fun int(key: String, default: Int) = Prop(intPreferencesKey(key), default)
    private fun long(key: String, default: Long) = Prop(longPreferencesKey(key), default)
    private fun string(key: String, default: String) = Prop(stringPreferencesKey(key), default)

    private fun booleanFlow(key: String, default: Boolean): Flow<Boolean> =
        dataStore.data.map { it[booleanPreferencesKey(key)] ?: default }.distinctUntilChanged()

    private fun intFlow(key: String, default: Int): Flow<Int> =
        dataStore.data.map { it[intPreferencesKey(key)] ?: default }.distinctUntilChanged()

    private fun longFlow(key: String, default: Long): Flow<Long> =
        dataStore.data.map { it[longPreferencesKey(key)] ?: default }.distinctUntilChanged()

    private fun stringFlow(key: String, default: String): Flow<String> =
        dataStore.data.map { it[stringPreferencesKey(key)] ?: default }.distinctUntilChanged()
}
package com.daniebeler.pfpixelix.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import com.daniebeler.pfpixelix.domain.model.SavedSearches
import com.daniebeler.pfpixelix.domain.repository.PixelfedApi
import com.daniebeler.pfpixelix.domain.repository.createPixelfedApi
import com.daniebeler.pfpixelix.domain.repository.serializers.SavedSearchesSerializer
import com.daniebeler.pfpixelix.domain.service.file.FileService
import com.daniebeler.pfpixelix.domain.service.file.toOkIoPath
import com.daniebeler.pfpixelix.domain.service.icon.AppIconManager
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.search.SearchFieldFocus
import com.daniebeler.pfpixelix.domain.service.session.AuthService
import com.daniebeler.pfpixelix.domain.service.session.Session
import com.daniebeler.pfpixelix.domain.service.session.SessionStorage
import com.daniebeler.pfpixelix.domain.service.session.SessionStorageDataSerializer
import com.daniebeler.pfpixelix.domain.service.session.SystemUrlHandler
import com.daniebeler.pfpixelix.domain.service.share.SystemFileShare
import com.daniebeler.pfpixelix.domain.service.widget.WidgetService
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.coilContext
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.datastore.DataStoreSettings
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import io.github.vinceglb.filekit.resolve
import io.github.vinceglb.filekit.toKotlinxIoPath
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import okio.FileSystem
import okio.SYSTEM

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class AppSingleton

@AppSingleton
@Component
abstract class AppComponent(
    @get:Provides val context: KmpContext,
    @get:Provides val iconManager: AppIconManager,
) {
    abstract val systemUrlHandler: SystemUrlHandler
    abstract val systemFileShare: SystemFileShare
    abstract val authService: AuthService
    abstract val widgetService: WidgetService

    abstract val preferences: UserPreferences
    abstract val searchFieldFocus: SearchFieldFocus

    @get:Provides
    @get:AppSingleton
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        encodeDefaults = true
        coerceInputValues = true
    }

    @Provides
    @AppSingleton
    fun provideHttpClient(
        json: Json,
        session: Session
    ): HttpClient = HttpClient {
        install(ContentNegotiation) { json(json) }
        install(Logging) {
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    Logger.v("Pixelix HttpClient") {
                        message.lines().joinToString { "\n\t\t$it" }
                    }
                }
            }
            level = LogLevel.NONE
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60000
            socketTimeoutMillis = 60000
            connectTimeoutMillis = 60000
        }
    }.apply {
        plugin(HttpSend).intercept { request ->
            with(session) { intercept(request) }
        }
    }

    @Provides
    @AppSingleton
    fun providePixelfedApi(client: HttpClient): PixelfedApi =
        Ktorfit.Builder()
            .converterFactories(CallConverterFactory())
            .httpClient(client)
            .baseUrl("https://err.or/")
            .build()
            .createPixelfedApi()

    @Provides
    @AppSingleton
    fun providePreferences(context: KmpContext): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(
            corruptionHandler = null,
            migrations = emptyList(),
            produceFile = {
                FileService.dataStoreDir.resolve("settings.preferences_pb").toOkIoPath()
            },
        )

    @Provides
    @AppSingleton
    fun provideSavedSearchesDataStore(context: KmpContext): DataStore<SavedSearches> =
        DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                producePath = {
                    FileService.dataStoreDir.resolve("saved_searches.json").toOkIoPath()
                },
                serializer = SavedSearchesSerializer,
            )
        )

    @Provides
    @AppSingleton
    fun provideSessionStorageDataStore(context: KmpContext): DataStore<SessionStorage> =
        DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                producePath = {
                    FileService.dataStoreDir.resolve("session_storage_datastore.json").toOkIoPath()
                },
                serializer = SessionStorageDataSerializer,
            )
        )

    @OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
    @Provides
    @AppSingleton
    fun provideSettings(ds: DataStore<Preferences>) = DataStoreSettings(ds)

    @Provides
    @AppSingleton
    fun provideImageLoader(): ImageLoader =
        ImageLoader.Builder(context.coilContext)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache(
                MemoryCache.Builder()
                    .maxSizePercent(context.coilContext, 0.2)
                    .build()
            )
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache(
                DiskCache.Builder()
                    .maxSizeBytes(50L * 1024L * 1024L)
                    .directory(FileService.imageCacheDir.toOkIoPath())
                    .build()
            )
            .build()

    companion object
}

@KmpComponentCreate
expect fun AppComponent.Companion.create(
    context: KmpContext,
    iconManager: AppIconManager,
): AppComponent
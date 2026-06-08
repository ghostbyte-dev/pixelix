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
import com.daniebeler.pfpixelix.domain.model.SessionStorage
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.repository.pixelfed.createPixelfedApi
import com.daniebeler.pfpixelix.domain.repository.serializers.SavedSearchesSerializer
import com.daniebeler.pfpixelix.domain.repository.serializers.SessionStorageSerializer
import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageApi
import com.daniebeler.pfpixelix.domain.repository.vernissage.createVernissageApi
import com.daniebeler.pfpixelix.domain.service.file.FileService
import com.daniebeler.pfpixelix.domain.service.file.toOkIoPath
import com.daniebeler.pfpixelix.domain.service.general.AccountService
import com.daniebeler.pfpixelix.domain.service.general.AccountServiceDelegate
import com.daniebeler.pfpixelix.domain.service.general.AuthServiceDelegate
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.ExploreServiceDelegate
import com.daniebeler.pfpixelix.domain.service.general.TimelineService
import com.daniebeler.pfpixelix.domain.service.general.TimelineServiceDelegate
import com.daniebeler.pfpixelix.domain.service.general.AppIconManager
import com.daniebeler.pfpixelix.domain.service.general.AppIconService
import com.daniebeler.pfpixelix.domain.service.general.AppIconServiceDelegate
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.ui.events.SearchFieldFocus
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import com.daniebeler.pfpixelix.domain.service.general.CollectionService
import com.daniebeler.pfpixelix.domain.service.general.CollectionServiceDelegate
import com.daniebeler.pfpixelix.domain.service.general.DirectMessagesService
import com.daniebeler.pfpixelix.domain.service.general.DirectMessagesServiceDelegate
import com.daniebeler.pfpixelix.domain.service.general.InstanceService
import com.daniebeler.pfpixelix.domain.service.general.InstanceServiceDelegate
import com.daniebeler.pfpixelix.domain.service.general.PostEditorService
import com.daniebeler.pfpixelix.domain.service.general.PostEditorServiceDelegate
import com.daniebeler.pfpixelix.domain.service.general.PostService
import com.daniebeler.pfpixelix.domain.service.general.PostServiceDelegate
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.ui.events.SystemUrlHandler
import com.daniebeler.pfpixelix.ui.events.AccountIntentHandler
import com.daniebeler.pfpixelix.ui.events.SystemFileShare
import com.daniebeler.pfpixelix.ui.events.BackToTopTrigger
import com.daniebeler.pfpixelix.ui.events.GlobalNavigator
import com.daniebeler.pfpixelix.ui.events.GlobalNavigatorImpl
import com.daniebeler.pfpixelix.domain.service.general.WidgetService
import com.daniebeler.pfpixelix.domain.service.general.WidgetServiceDelegate
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedAuthInterceptor
import com.daniebeler.pfpixelix.domain.service.vernissage.VernissageAuthInterceptor
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.coilContext
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.datastore.DataStoreSettings
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import io.github.vinceglb.filekit.resolve
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
import me.tatarka.inject.annotations.Qualifier
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

    abstract val accountIntentHandler: AccountIntentHandler

    abstract val authService: AuthService
    abstract val accountService: AccountService
    abstract val widgetService: WidgetService

    abstract val preferences: UserPreferences
    abstract val searchFieldFocus: SearchFieldFocus
    abstract val backToTopTrigger: BackToTopTrigger
    abstract val globalNavigator: GlobalNavigator

    @Provides
    fun bindGlobalNavigator(impl: GlobalNavigatorImpl): GlobalNavigator = impl

    @Provides
    fun provideTimelineService(delegate: TimelineServiceDelegate): TimelineService = delegate

    @Provides
    fun provideExploreService(delegate: ExploreServiceDelegate): ExploreService = delegate

    @Provides
    fun provideAppIconService(delegate: AppIconServiceDelegate): AppIconService = delegate

    @Provides
    fun providePostEditorService(delegate: PostEditorServiceDelegate): PostEditorService = delegate

    @Provides
    fun providePostService(delegate: PostServiceDelegate): PostService = delegate

    @Provides
    fun provideWidgetService(delegate: WidgetServiceDelegate): WidgetService = delegate

    @Provides
    fun provideInstanceService(delegate: InstanceServiceDelegate): InstanceService = delegate

    @Provides
    fun provideAuthService(delegate: AuthServiceDelegate): AuthService = delegate

    @Provides
    fun provideAccountService(delegate: AccountServiceDelegate): AccountService = delegate

    @Provides
    fun provideCollectionService(delegate: CollectionServiceDelegate): CollectionService = delegate
    @Provides
    fun provideDirectMessagesService(delegate: DirectMessagesServiceDelegate): DirectMessagesService = delegate

    @get:Provides
    @get:AppSingleton
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        encodeDefaults = true
        coerceInputValues = true
    }

    @Qualifier
    @Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
    annotation class PixelfedClient

    @Qualifier
    @Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
    annotation class VernissageClient

    @Provides
    @AppSingleton
    @PixelfedClient
    fun providePixelfedHttpClient(
        json: Json,
        session: Session,
        sessionStorage: DataStore<SessionStorage>,
        globalNavigator: GlobalNavigator
    ): HttpClient {
        val authInterceptor = PixelfedAuthInterceptor(session, json, sessionStorage, globalNavigator)

        return HttpClient {

            expectSuccess = true
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
                with(authInterceptor) { intercept(request) }
            }
        }
    }

    @Provides
    @AppSingleton
    fun providePixelfedApi(@PixelfedClient client: HttpClient): PixelfedApi =
        Ktorfit.Builder()
            .converterFactories(CallConverterFactory())
            .httpClient(client)
            .baseUrl("https://err.or/")
            .build()
            .createPixelfedApi()


    // TODO eigener http client für vernissage
    @Provides
    @AppSingleton
    fun provideVernissageApi(client: HttpClient): VernissageApi =
        Ktorfit.Builder()
            .converterFactories(CallConverterFactory())
            .httpClient(client)
            .baseUrl("https://err.or/")
            .build()
            .createVernissageApi()

    @Provides
    @AppSingleton
    @VernissageClient
    fun provideVernissageHttpClient(
        json: Json,
        session: Session,
        sessionStorage: DataStore<SessionStorage>,
        globalNavigator: GlobalNavigator
    ): HttpClient {
        val authInterceptor = VernissageAuthInterceptor(session, json, sessionStorage, globalNavigator)

        return HttpClient {

            expectSuccess = true
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
                with(authInterceptor) { intercept(request) }
            }
        }
    }

    @Provides
    @AppSingleton
    fun provideVernissageApi(@VernissageClient client: HttpClient): VernissageApi =
        Ktorfit.Builder()
            .converterFactories(CallConverterFactory())
            .httpClient(client)
            .baseUrl("https://err.or/")
            .build()
            .createVernissageApi()

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
                    FileService.dataStoreDir.resolve("saved_searches_2.json").toOkIoPath()
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
                    FileService.dataStoreDir.resolve("session_storage_datastore.json")
                        .toOkIoPath()
                },
                serializer = SessionStorageSerializer,
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
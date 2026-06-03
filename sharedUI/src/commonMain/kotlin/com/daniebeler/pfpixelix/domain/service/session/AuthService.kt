package com.daniebeler.pfpixelix.domain.service.session

import androidx.datastore.core.DataStore
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.service.capabilities.PixelfedCapabilities
import com.daniebeler.pfpixelix.domain.service.capabilities.VernissageCapabilities
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.search.SavedSearchesService
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

@Inject
@AppSingleton
class AuthService(
    private val urlHandler: SystemUrlHandler,
    private val session: Session,
    private val sessionStorage: DataStore<SessionStorage>,
    private val savedSearchesService: SavedSearchesService,
    private val json: Json,
    private val platform: Platform
) {
    companion object {
        private const val clientName = "pixelix"
        private const val grantType = "authorization_code"
        private const val redirectUrl = "dev.ghostbyte.pixelix://callback"
        private val domainRegex: Regex =
            "^((\\*)|((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)|((\\*\\.)?([a-zA-Z0-9-]+\\.){0,5}[a-zA-Z0-9-][a-zA-Z0-9-]+\\.[a-zA-Z]{2,63}?))\$".toRegex()
    }

    val activeUser: Flow<String?> = session.credentials.map { it?.accountId }

    suspend fun auth(host: String) {
        val serverUrl = getServerUrl(host)
        val api = createAuthApi(serverUrl, json)
        val authData = api.getAuthData(clientName, redirectUrl)

        val authUrl = URLBuilder("${serverUrl}oauth/authorize").apply {
            parameters.apply {
                append("response_type", "code")
                append("redirect_uri", redirectUrl)
                append("client_id", authData.clientId)
            }
        }.build()

        urlHandler.isAuthInProgress = true
        platform.openUrl(authUrl.toString())
        val redirectString = urlHandler.redirects.first()
        platform.dismissBrowser()

        if (redirectString == "CANCELLED") {
            error("User canceled the authentication flow.")
        }
        val redirect = Url(redirectString)
        val code = redirect.parameters["code"] ?: error("Redirect doesn't have a code")

        val token = api.getToken(
            authData.clientId,
            authData.clientSecret,
            code,
            redirectUrl,
            grantType
        )

        val account = api.verify("Bearer ${token.accessToken}")

        val newCred = Credentials(
            accountId = requireNotNull(account.id),
            username = requireNotNull(account.username),
            displayName = account.displayname ?: account.username,
            avatar = account.avatar,
            serverUrl = serverUrl.toString(),
            token = token.accessToken,
            refreshToken = token.refreshToken,
            clientId = authData.clientId,
            clientSecret = authData.clientSecret,
            createdAt = token.createdAt
        )
        updateSession(newCred)
    }

    private suspend fun updateSession(newCred: Credentials) {
        val targetKey = newCred.key()
        sessionStorage.updateData { data ->
            data.copy(
                sessions = data.sessions + (targetKey to newCred),
                activeKey = targetKey
            )
        }
        session.setCredentials(newCred)
    }

    suspend fun openSessionIfExist(key: String? = null) {
        var resolvedCredentials: Credentials? = null
        sessionStorage.updateData { data ->
            val cred = if (key == null) {
                data.getActiveSession()
            } else {
                data.sessions[key]
            }
            resolvedCredentials = cred
            if (cred != null) {
                data.copy(activeKey = cred.key())
            } else {
                data
            }
        }
        session.setCredentials(resolvedCredentials)
        session.setBackendType(BackendType.PIXELFED)
    }

    fun isValidHost(host: String): Boolean = domainRegex.matches(host)

    suspend fun deleteSession(keyParam: String? = null) {
        sessionStorage.updateData { data ->
            val key = keyParam ?: data.activeKey
            val newSessions = data.sessions.filter { it.key != key }
            val newId = if (data.activeKey == key) {
                newSessions.values.firstOrNull()?.key()
            } else {
                data.activeKey
            }
            data.copy(sessions = newSessions, activeKey = newId)
        }
        if (keyParam == null) {
            savedSearchesService.clearSavedSearches()
            openSessionIfExist()
        }
    }

    suspend fun getAvailableSessions(): SessionStorage {
        return sessionStorage.data.first()
    }

    suspend fun updateSessionAvatar(accountId: String, avatarUrl: String) {
        sessionStorage.updateData { data ->
            val updatedSessions = data.sessions.mapValues { (_, credentials) ->
                if (credentials.accountId == accountId) {
                    credentials.copy(avatar = avatarUrl)
                } else {
                    credentials
                }
            }

            data.copy(sessions = updatedSessions)
        }
        openSessionIfExist()
    }

    fun getCurrentSession(): Credentials? {
        return session.credentials.value
    }

    private fun getServerUrl(host: String): Url {
        require(isValidHost(host)) { "The host is invalid '$host'" }
        return Url("https://$host/")
    }
}
fun createAuthApi(baseUrl: Url, json: Json): AuthApi {
    val httpClient = HttpClient {
        install(ContentNegotiation) { json(json) }
        install(Logging) {
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    Logger.v(tag = "Pixelix HttpAuth") {
                        message.lines().joinToString { "\n\t\t$it" }
                    }
                }
            }
            level = LogLevel.INFO
        }
    }
    val ktorfit = Ktorfit.Builder()
        .httpClient(httpClient)
        .baseUrl(baseUrl.toString())
        .build()
    return ktorfit.createAuthApi()
}

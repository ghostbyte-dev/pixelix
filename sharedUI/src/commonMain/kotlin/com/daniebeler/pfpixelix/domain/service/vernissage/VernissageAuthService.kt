package com.daniebeler.pfpixelix.domain.service.vernissage

import androidx.datastore.core.DataStore
import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.Credentials
import com.daniebeler.pfpixelix.domain.model.SessionStorage
import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageAuthApi.Companion.createVernissageAuthApi
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import com.daniebeler.pfpixelix.domain.service.general.AuthService.Companion.grantType
import com.daniebeler.pfpixelix.domain.service.general.AuthService.Companion.redirectUrl
import com.daniebeler.pfpixelix.domain.service.general.BackendType
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.search.SavedSearchesService
import com.daniebeler.pfpixelix.domain.service.vernissage.model.JwtClaims
import com.daniebeler.pfpixelix.ui.events.SystemUrlHandler
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random

@Inject
@AppSingleton
class VernissageAuthService(
    private val urlHandler: SystemUrlHandler,
    private val session: Session,
    private val sessionStorage: DataStore<SessionStorage>,
    private val savedSearchesService: SavedSearchesService,
    private val json: Json,
    private val platform: Platform
) : AuthService {
    override val activeUser: Flow<String?> = session.credentials.map { it?.accountId }

    override suspend fun auth(host: String) {
        val serverUrl = getServerUrl(host)
        val api = createVernissageAuthApi(serverUrl, json)

        val authData = api.getAuthData("Pixelix", listOf("dev.ghostbyte.pixelix://callback"))

        val state = Random.nextInt(100000, 999999).toString()
        val nonce = Random.nextInt(100000, 999999).toString()
        val scope = "read write"

        val authUrl = URLBuilder().apply {
            takeFrom(serverUrl)
            encodedPath = "/api/v1/oauth/authorize"

            parameters.apply {
                append("response_type", "code")
                append("client_id", authData.clientId)
                append("redirect_uri", redirectUrl)
                append("scope", scope)
                append("state", state)
                append("nonce", nonce)
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

        val clientId = authData.clientId

        val tokenResponse = api.getToken(
            clientId = clientId,
            clientSecret = "",
            code = code,
            redirectUri = redirectUrl,
            grantType = "authorization_code"
        )

        val username = getUsernameFromToken(tokenResponse.accessToken)

        if (username.isNullOrEmpty()) {
            error("Invalid Token")
        }

        val account = api.verify("Bearer ${tokenResponse.accessToken}", username)


        val newCred = Credentials(
            accountId = requireNotNull(account.id),
            username = requireNotNull(account.username),
            displayName = account.displayname ?: account.username,
            avatar = account.avatar ?: "",
            serverUrl = serverUrl.toString(),
            token = tokenResponse.accessToken,
            refreshToken = tokenResponse.refreshToken,
            clientId = authData.clientId,
            clientSecret = "authData.clientSecret",
            createdAt = "tokenResponse.createdAt",
            backendType = BackendType.VERNISSAGE
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

    override suspend fun openSessionIfExist(key: String?) {
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
    }


    override suspend fun deleteSession(keyParam: String?) {
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

    override suspend fun getAvailableSessions(): SessionStorage {
        return sessionStorage.data.first()
    }

    override suspend fun updateSessionAvatar(accountId: String, avatarUrl: String) {
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

    override fun getCurrentSession(): Credentials? {
        return session.credentials.value
    }


    @OptIn(ExperimentalEncodingApi::class)
    private fun decodeJwtClaims(token: String): JwtClaims? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            // JWT uses URL-safe Base64 without padding — fix it
            val payload = parts[1]
                .replace('-', '+')
                .replace('_', '/')
                .let {
                    // Add padding if needed
                    val pad = it.length % 4
                    if (pad > 0) it + "=".repeat(4 - pad) else it
                }

            val decoded = Base64.decode(payload).decodeToString()

            Json { ignoreUnknownKeys = true }.decodeFromString<JwtClaims>(decoded)
        } catch (e: Exception) {
            null
        }
    }

    private fun getUsernameFromToken(token: String): String? {
        val claims = decodeJwtClaims(token) ?: return null
        return claims.userName
            ?: claims.sub
            ?: claims.email
            ?: claims.name
    }
}

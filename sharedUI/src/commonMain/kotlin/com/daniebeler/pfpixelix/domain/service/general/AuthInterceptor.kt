package com.daniebeler.pfpixelix.domain.service.general

import androidx.datastore.core.DataStore
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.domain.model.Credentials
import com.daniebeler.pfpixelix.domain.model.SessionStorage
import com.daniebeler.pfpixelix.domain.repository.pixelfed.AuthApi
import com.daniebeler.pfpixelix.ui.events.GlobalNavigationEvent
import com.daniebeler.pfpixelix.ui.events.GlobalNavigator
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.Sender
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AuthInterceptor(
    private val session: Session,
    private val json: Json,
    private val sessionStorage: DataStore<SessionStorage>,
    private val globalNavigator: GlobalNavigator
) {
    private val refreshMutex = Mutex()
    suspend fun Sender.intercept(request: HttpRequestBuilder): HttpClientCall {
        val token = session.credentials.value?.token
        if (token != null) {
            request.headers[HttpHeaders.Authorization] = "Bearer $token"
        }
        val executedRequest = execute(request)

        if (executedRequest.response.status == HttpStatusCode.Companion.InternalServerError) {
            Logger.Companion.i(tag = "Unauthorized") {
                "try refreshing token"
            }
            val errorBodyText = executedRequest.response.bodyAsText()
            val isUnauthenticated = try {
                val json = Json.Default.parseToJsonElement(errorBodyText).jsonObject
                json["error"]?.jsonPrimitive?.content == "Unauthenticated."
            } catch (e: Exception) {
                false
            }
            if (isUnauthenticated) {
                return try {
                    refreshToken()
                    val newToken = session.credentials.value?.token
                    if (newToken != null) {
                        request.headers[HttpHeaders.Authorization] = "Bearer $newToken"
                        execute(request)
                    } else {
                        globalNavigator.emit(GlobalNavigationEvent.NavigateToLogin)
                        executedRequest
                    }
                } catch (e: Exception) {
                    Logger.Companion.e(tag = "Unauthorized") {
                        "error refreshing token" + e.message
                    }
                    globalNavigator.emit(GlobalNavigationEvent.NavigateToLogin)
                    executedRequest
                }
            }
        }
        return executedRequest
    }

    suspend fun refreshToken() {
        val localTokenSnapshot = session.credentials.value?.token
            ?: throw NullPointerException("Credentials are null")

        refreshMutex.withLock {
            val currentCredentials = session.credentials.value
                ?: throw NullPointerException("Credentials are null")

            if (currentCredentials.token != localTokenSnapshot) {
                Logger.v(tag = "Pixelix Auth") {
                    "Token was already refreshed by a concurrent request. Skipping network execution."
                }
                return
            }

            //TODO: make authApi.createAuthApi return vernissageAuthApi or pixelfedAuthApi, or fix it differently
            val authApi = AuthApi.createAuthApi(Url(currentCredentials.serverUrl), json)

            val token = authApi.getTokenRefresh(
                clientId = currentCredentials.clientId,
                clientSecret = currentCredentials.clientSecret,
                refreshToken = currentCredentials.refreshToken,
                grantType = "refresh_token"
            )

            updateSession(
                currentCredentials.copy(
                    token = token.accessToken,
                    refreshToken = token.refreshToken,
                    createdAt = token.createdAt
                )
            )
        }
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
}
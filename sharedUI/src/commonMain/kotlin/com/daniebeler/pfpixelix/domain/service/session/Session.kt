package com.daniebeler.pfpixelix.domain.service.session

import com.daniebeler.pfpixelix.di.AppSingleton
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.Sender
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.Url
import io.ktor.http.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
@AppSingleton
class Session {
    private val credentialsState = MutableStateFlow<Credentials?>(null)
    val credentials: StateFlow<Credentials?> = credentialsState.asStateFlow()

    fun setCredentials(credentials: Credentials?) {
        credentialsState.value = credentials
    }

    suspend fun Sender.intercept(request: HttpRequestBuilder): HttpClientCall {
        credentials.value?.let { creds ->
            request.apply {
                if (url.host != "api.fedisea.surf" && url.host != "pixelfed.org") {
                    url.set(host = Url(creds.serverUrl).host)
                   // headers["Authorization"] = "Bearer ${creds.token}"
                }
            }
        }
        return execute(request)
    }
}
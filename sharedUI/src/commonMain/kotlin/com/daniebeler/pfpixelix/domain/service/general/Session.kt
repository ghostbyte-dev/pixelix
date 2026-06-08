package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.Credentials
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.domain.service.capabilities.NoCapabilities
import com.daniebeler.pfpixelix.domain.service.capabilities.PixelfedCapabilities
import com.daniebeler.pfpixelix.domain.service.capabilities.VernissageCapabilities
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.Sender
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.Url
import io.ktor.http.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Inject

@Inject
@AppSingleton
class Session {
    private val credentialsState = MutableStateFlow<Credentials?>(null)
    val credentials: StateFlow<Credentials?> = credentialsState.asStateFlow()

    private val backendTypeState = MutableStateFlow(BackendType.PIXELFED)
    val backendType: StateFlow<BackendType> = backendTypeState.asStateFlow()

    private val capabilitiesSate = MutableStateFlow(NoCapabilities)
    val capabilities: StateFlow<Capabilities> = capabilitiesSate.asStateFlow()

    fun setCredentials(credentials: Credentials?) {
        credentialsState.value = credentials
        if (credentials != null) {
            setBackendType(credentials.backendType)
        }
    }

    fun setBackendType(backendType: BackendType) {
        backendTypeState.value = backendType
        capabilitiesSate.value = backendType.toCapabilities()
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

enum class BackendType {
    PIXELFED,
    VERNISSAGE
}

fun BackendType.toCapabilities(): Capabilities = when (this) {
    BackendType.PIXELFED   -> PixelfedCapabilities
    BackendType.VERNISSAGE -> VernissageCapabilities
}
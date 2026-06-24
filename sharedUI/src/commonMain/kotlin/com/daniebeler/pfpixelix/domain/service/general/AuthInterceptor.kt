package com.daniebeler.pfpixelix.domain.service.general

import androidx.datastore.core.DataStore
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.domain.model.Credentials
import com.daniebeler.pfpixelix.domain.model.SessionStorage
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedAuthApi
import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageAuthApi
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

interface AuthInterceptor{
    suspend fun Sender.intercept(request: HttpRequestBuilder): HttpClientCall
}
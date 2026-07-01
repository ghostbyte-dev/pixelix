package com.daniebeler.pfpixelix.domain.service.general

import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.Sender
import io.ktor.client.request.HttpRequestBuilder

interface AuthInterceptor{
    suspend fun Sender.intercept(request: HttpRequestBuilder): HttpClientCall
}
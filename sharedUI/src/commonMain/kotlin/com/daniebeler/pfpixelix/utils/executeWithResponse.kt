package com.daniebeler.pfpixelix.utils

import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Callback
import io.ktor.client.statement.HttpResponse
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


suspend fun <T> Call<T>.executeWithResponse() = suspendCoroutine { cont ->
    onExecute(object : Callback<T> {
        override fun onResponse(call: T, response: HttpResponse) {
            cont.resume(response to call)
        }

        override fun onError(exception: Throwable) {
            cont.resumeWithException(exception)
        }
    })
}
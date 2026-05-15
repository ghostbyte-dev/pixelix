package com.daniebeler.pfpixelix.utils

import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Callback
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Helper function to get the Response of an api call and a pagination info from the header link
 * Executes the Call and then parses the pagination element and returns both
 *
 * @param directionNext the direction of the Pagination, if true, function gets next
 *        page, if false it get previous
 * @param paginationName the name of the pagination element, (cursor, max_id, ...)
 *
 * @return a [PaginatedResponse] with the data and the next page
 */
suspend fun <T> Call<T>.executeAndParsePagination(
    directionNext: Boolean,
    paginationName: String
): PaginatedResponse<T> {
    val (response, data) = this.executeWithResponse()
    val linkHeader = response.headers["link"] ?: ""
    val links = linkHeader.split(",")
    val direction = if (directionNext) "next" else "prev"
    val nextLink = links.find { it.contains("rel=\"$direction\"", ignoreCase = true) } ?: ""
    val regex = "$paginationName=([^&>\\s\"']+)".toRegex()
    val matchResult = regex.find(nextLink)
    val next = matchResult?.groupValues?.get(1)

    return PaginatedResponse<T>(data, next)
}

private suspend fun <T> Call<T>.executeWithResponse() = suspendCancellableCoroutine { cont ->
    onExecute(object : Callback<T> {
        override fun onResponse(call: T, response: HttpResponse) {
            cont.resume(response to call)
        }

        override fun onError(exception: Throwable) {
            cont.resumeWithException(exception)
        }
    })
}
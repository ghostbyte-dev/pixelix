package com.daniebeler.pfpixelix.domain.service.utils

import com.daniebeler.pfpixelix.domain.service.utils.Resource
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

internal inline fun <reified T> loadResource(
    crossinline call: suspend () -> T
): Flow<Resource<T>> = flow {
    emit(Resource.Loading())
    try {
        val data = call()
        emit(Resource.Success(data))
    } catch (e: ClientRequestException) {
        val errorBody = e.response.bodyAsText()
        val errorMessage = parseErrorMessage(errorBody) ?: "Client Error"
        emit(Resource.Error(errorMessage))
    } catch (e: Exception) {
        emit(Resource.Error(e.message ?: "Unknown Error"))
    }
}


internal inline fun <reified T> loadListResources(
    crossinline call: suspend () -> List<T>
): Flow<Resource<List<T>>> = flow {
    emit(Resource.Loading())
    try {
        val data = call()
        emit(Resource.Success(data))
    } catch (e: ClientRequestException) {
        val errorBody = e.response.bodyAsText()
        val errorMessage = parseErrorMessage(errorBody) ?: "Client Error"
        emit(Resource.Error(errorMessage))
    } catch (e: Exception) {
        emit(Resource.Error(e.message ?: "Unknown Error"))
    }
}

private fun parseErrorMessage(jsonString: String): String? {
    return try {
        val json = Json { ignoreUnknownKeys = true }
        json.decodeFromString<Map<String, String>>(jsonString)["error"]
    } catch (e: Exception) {
        null
    }
}
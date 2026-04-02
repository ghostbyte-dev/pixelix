package com.daniebeler.pfpixelix.widget.notifications.utils

import androidx.datastore.core.Serializer
import com.daniebeler.pfpixelix.widget.notifications.models.LatestImageStore
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object LatestImageStoreSerializer: Serializer<LatestImageStore> {
    override val defaultValue: LatestImageStore
        get() = LatestImageStore()

    override suspend fun readFrom(input: InputStream): LatestImageStore {
        return try {
            Json.decodeFromString(
                deserializer = LatestImageStore.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: LatestImageStore, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = LatestImageStore.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}
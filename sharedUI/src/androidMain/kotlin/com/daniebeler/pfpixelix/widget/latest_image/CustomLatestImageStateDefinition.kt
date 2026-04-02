package com.daniebeler.pfpixelix.widget.latest_image

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.glance.state.GlanceStateDefinition
import com.daniebeler.pfpixelix.widget.notifications.models.LatestImageStore
import com.daniebeler.pfpixelix.widget.notifications.utils.LatestImageStoreSerializer
import java.io.File

object CustomLatestImageStateDefinition : GlanceStateDefinition<LatestImageStore> {
    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<LatestImageStore> {
        return context.dataStore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        // Note: The Datastore Preference file resides is in the context.applicationContext.filesDir + "datastore/"
        return File(context.applicationContext.filesDir, "datastore/$fileName")
    }

    private const val fileName = "latest_image_widget_store"
    private val Context.dataStore: DataStore<LatestImageStore> by dataStore(fileName, LatestImageStoreSerializer)
}


package com.daniebeler.pfpixelix.ui.composables.places

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.vectorResource
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.interaction.MapInteractions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.location

@Composable
actual fun MapComposable(lat: Double, lon: Double) {

    val mapState = rememberMapState(
        baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
        initialCameraPosition = CameraPosition(
            center = Position(latitude = lat, longitude = lon), zoom = 8.0
        )
    )

    MaplibreMap(
        modifier = Modifier.fillMaxWidth().height(300.dp),
        state = mapState,
        interactions = MapInteractions {
            camera {
                rotate { enabled = false }
                pitch { enabled = false }
            }
        }) {
//        Icon(
//            imageVector = vectorResource(Res.drawable.location),
//            contentDescription = "",
//            tint = MaterialTheme.colorScheme.error,
//            modifier = Modifier
//                .placedAt(
//                    position = Position(latitude = lat, longitude = lon),
//                    alignment = Alignment.BottomCenter // tip of the pin sits on the point
//                )
//                .size(40.dp)
//        )
    }
}
package com.daniebeler.pfpixelix.ui.composables.places

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position

@Composable
actual fun MapComposable(lat: Double, lon: Double) {

    val mapState = rememberMapState(
        baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
        initialCameraPosition = CameraPosition(
            center = Position(latitude = lat, longitude = lon), zoom = 11.0
        )
    )

    MaplibreMap(modifier = Modifier.fillMaxWidth().height(300.dp), state = mapState) {

    }
}
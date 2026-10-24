package com.example.sulitpackgoui

import android.os.Bundle
import androidx.activity.ComponentActivity
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions

class MapActivity : ComponentActivity() {

    private lateinit var mapView: MapView
    private var pickupLocation: LatLng? = null
    private var dropLocation: LatLng? = null
    private var pickupMarker: Marker? = null
    private var dropMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapLibre.getInstance(this)

        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)

        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { map ->

            map.setStyle(
                "https://demotiles.maplibre.org/style.json"
            )
            map.addOnMapClickListener { point ->

                if (pickupLocation == null) {

                    pickupLocation = point

                    pickupMarker = map.addMarker(
                        MarkerOptions()
                            .position(point)
                            .title("Pickup")
                    )

                } else if (dropLocation == null) {

                    dropLocation = point

                    dropMarker = map.addMarker(
                        MarkerOptions()
                            .position(point)
                            .title("Drop-off")
                    )

                } else {

                    // Remove the old markers
                    pickupMarker?.let {
                        map.removeMarker(it)
                    }

                    dropMarker?.let {
                        map.removeMarker(it)
                    }

                    // Start again
                    pickupLocation = point
                    dropLocation = null

                    pickupMarker = map.addMarker(
                        MarkerOptions()
                            .position(point)
                            .title("Pickup")
                    )

                    dropMarker = null
                }

                true
            }

            map.cameraPosition =
                CameraPosition.Builder()
                    .target(
                        LatLng(
                            14.0,
                            121.0
                        )
                    )
                    .zoom(7.0)
                    .build()
        }
    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }


    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }


    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }


    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }


    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mapView.onSaveInstanceState(outState)
    }
}
package com.example.sulitpackgoui.uic.customer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.logic.NasugbuBoundary
import com.google.android.material.button.MaterialButton
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

class CustomerLocationPickerActivity : ComponentActivity() {

    private lateinit var mapView:
            MapView


    private lateinit var nasugbuBoundary:
            NasugbuBoundary


    private var selectedLatitude =
        0.0


    private var selectedLongitude =
        0.0


    private var locationSelected =
        false


    private lateinit var tvCoordinates:
            TextView


    private lateinit var btnConfirm:
            MaterialButton


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        MapLibre.getInstance(
            this,
            "",
            WellKnownTileServer.MapLibre
        )


        setContentView(
            R.layout.customer_location_picker
        )


        mapView =
            findViewById(
                R.id.mapView
            )


        mapView.onCreate(
            savedInstanceState
        )


        nasugbuBoundary =
            NasugbuBoundary(
                this
            )


        setupViews()

        setupMap()
    }


    private fun setupViews() {

        var tvTitle =
            findViewById<TextView>(
                R.id.tvMapTitle
            )


        var tvInstruction =
            findViewById<TextView>(
                R.id.tvMapInstruction
            )


        tvCoordinates =
            findViewById(
                R.id.tvSelectedCoordinates
            )


        btnConfirm =
            findViewById(
                R.id.btnConfirmLocation
            )


        var btnCancel =
            findViewById<MaterialButton>(
                R.id.btnCancel
            )


        var locationType =
            readTextExtra(
                "locationType"
            )


        if (locationType == "pickup") {

            tvTitle.text =
                "Select Pickup Location"


            tvInstruction.text =
                "Tap the exact pickup location inside Nasugbu."

        } else {

            tvTitle.text =
                "Select Drop-off Location"


            tvInstruction.text =
                "Tap the exact drop-off location inside Nasugbu."
        }


        btnConfirm.setOnClickListener {

            confirmLocation()
        }


        btnCancel.setOnClickListener {

            finish()
        }
    }


    private fun setupMap() {

        mapView.getMapAsync { map ->


            map.setStyle(
                "https://tiles.openfreemap.org/styles/liberty"
            ) {


                map.cameraPosition =
                    CameraPosition
                        .Builder()
                        .target(
                            LatLng(
                                14.07,
                                120.63
                            )
                        )
                        .zoom(
                            12.0
                        )
                        .build()


                map.addOnMapClickListener { point ->


                    var insideNasugbu =
                        nasugbuBoundary
                            .isInsideNasugbu(
                                point.latitude,
                                point.longitude
                            )


                    if (
                        insideNasugbu == false
                    ) {

                        showMessage(
                            "Please select a location inside Nasugbu."
                        )

                    } else {

                        selectedLatitude =
                            point.latitude


                        selectedLongitude =
                            point.longitude


                        locationSelected =
                            true


                        map.clear()


                        map.addMarker(
                            MarkerOptions()
                                .position(
                                    point
                                )
                                .title(
                                    "Selected Location"
                                )
                        )


                        tvCoordinates.text =
                            "Selected: " +
                                    String.format(
                                        "%.6f",
                                        selectedLatitude
                                    ) +
                                    ", " +
                                    String.format(
                                        "%.6f",
                                        selectedLongitude
                                    )


                        btnConfirm.isEnabled =
                            true
                    }


                    true
                }
            }
        }
    }


    private fun confirmLocation() {

        if (
            locationSelected == false
        ) {

            showMessage(
                "Select a location first."
            )

            return
        }


        var resultIntent =
            Intent()


        resultIntent.putExtra(
            "latitude",
            selectedLatitude
        )


        resultIntent.putExtra(
            "longitude",
            selectedLongitude
        )


        setResult(
            Activity.RESULT_OK,
            resultIntent
        )


        finish()
    }


    private fun readTextExtra(
        name: String
    ): String {

        var value =
            intent.getStringExtra(
                name
            )


        if (value == null) {

            return ""
        }


        return value
    }


    private fun showMessage(
        message: String
    ) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
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

        mapView.onPause()

        super.onPause()
    }


    override fun onStop() {

        mapView.onStop()

        super.onStop()
    }


    override fun onDestroy() {

        mapView.onDestroy()

        super.onDestroy()
    }


    override fun onLowMemory() {

        super.onLowMemory()

        mapView.onLowMemory()
    }
}
package com.example.sulitpackgoui.uic

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.admin.VehicleFeeDatabase
import com.example.sulitpackgoui.logic.DeliveryLocation
import com.example.sulitpackgoui.logic.FeeCalculator
import com.example.sulitpackgoui.logic.NasugbuBoundary
import com.example.sulitpackgoui.logic.RouteDistance
import com.example.sulitpackgoui.logic.Vehicle
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

class MapTestActivity : ComponentActivity() {

    private lateinit var mapView: MapView
    private lateinit var nasugbuBoundary: NasugbuBoundary

    private var pickupPoint: LatLng? = null
    private var dropoffPoint: LatLng? = null

    private var deliveryLocation = DeliveryLocation()

    private var routeDistance = RouteDistance()

    private var vehicleFeeDatabase =
        VehicleFeeDatabase()

    private var vehicle =
        Vehicle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapLibre.getInstance(
            this,
            "",
            WellKnownTileServer.MapLibre
        )

        setContentView(R.layout.map_test)

        nasugbuBoundary =
            NasugbuBoundary(this)

        mapView =
            findViewById(R.id.mapView)

        mapView.onCreate(savedInstanceState)

        // Motor is used only for this backend test
        vehicle.selectVehicle("Motor")

        mapView.getMapAsync { map ->

            map.setStyle(
                "https://tiles.openfreemap.org/styles/liberty"
            ) {

                var cameraPosition =
                    CameraPosition.Builder()
                        .target(
                            LatLng(
                                14.07,
                                120.63
                            )
                        )
                        .zoom(12.0)
                        .build()

                map.cameraPosition =
                    cameraPosition

                map.addOnMapClickListener { point ->

                    var insideNasugbu =
                        nasugbuBoundary.isInsideNasugbu(
                            point.latitude,
                            point.longitude
                        )

                    if (!insideNasugbu) {

                        Toast.makeText(
                            this,
                            "Delivery service is only available within Nasugbu.",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@addOnMapClickListener true
                    }

                    if (pickupPoint == null) {

                        pickupPoint = point

                        deliveryLocation.pickUpLatitude =
                            point.latitude

                        deliveryLocation.pickUpLongitude =
                            point.longitude

                        map.addMarker(
                            MarkerOptions()
                                .position(point)
                                .title("Pickup Location")
                        )

                        Toast.makeText(
                            this,
                            "Pickup location selected",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else if (dropoffPoint == null) {

                        dropoffPoint = point

                        deliveryLocation.dropLatitude =
                            point.latitude

                        deliveryLocation.dropLongitude =
                            point.longitude

                        map.addMarker(
                            MarkerOptions()
                                .position(point)
                                .title("Drop-off Location")
                        )

                        Toast.makeText(
                            this,
                            "Calculating delivery fee...",
                            Toast.LENGTH_SHORT
                        ).show()

                        calculateDeliveryFee()

                    } else {

                        Toast.makeText(
                            this,
                            "Pickup and drop-off are already selected.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    true
                }
            }
        }
    }

    private fun calculateDeliveryFee() {

        routeDistance.getRoadDistance(
            deliveryLocation
        ) { distanceSuccess, roadDistanceKm, distanceMessage ->

            if (!distanceSuccess) {

                runOnUiThread {

                    Toast.makeText(
                        this,
                        distanceMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }

                return@getRoadDistance
            }

            vehicleFeeDatabase.getVehicleFee(
                vehicle.vehicleType
            ) { feeSuccess, feeMessage, vehicleFee ->

                if (!feeSuccess || vehicleFee == null) {

                    runOnUiThread {

                        Toast.makeText(
                            this,
                            feeMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    return@getVehicleFee
                }

                var calculator =
                    FeeCalculator(
                        devLocation = deliveryLocation,
                        feeVehicle = vehicle,
                        vehicleFee = vehicleFee,
                        roadDistanceKm = roadDistanceKm
                    )

                var baseFee =
                    calculator.calculateBaseFee()

                var distanceFee =
                    calculator.calculateDistanceFee()

                var totalFee =
                    calculator.calculateTotalFee()

                runOnUiThread {

                    var result =
                        "Vehicle: ${vehicle.vehicleType}\n" +
                                "Road Distance: %.2f km\n".format(
                                    roadDistanceKm
                                ) +
                                "Base Fee: ₱%.2f\n".format(
                                    baseFee
                                ) +
                                "Distance Fee: ₱%.2f\n".format(
                                    distanceFee
                                ) +
                                "Total Fee: ₱%.2f".format(
                                    totalFee
                                )

                    Toast.makeText(
                        this,
                        result,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
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
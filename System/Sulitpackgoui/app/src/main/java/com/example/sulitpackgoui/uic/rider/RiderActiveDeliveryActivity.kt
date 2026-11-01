package com.example.sulitpackgoui.uic.rider

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.order.OrderData
import com.example.sulitpackgoui.rider.Rider
import com.example.sulitpackgoui.rider.RiderDatabase
import com.example.sulitpackgoui.rider.RiderOrder
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

class RiderActiveDeliveryActivity : ComponentActivity() {

    // Firebase
    private var firebaseAuth =
        FirebaseAuth.getInstance()


    // Rider backend
    private var riderDatabase =
        RiderDatabase()

    private var riderOrder =
        RiderOrder()


    // Current Rider
    private var currentRider: Rider? =
        null


    // Current Order
    private var currentOrder: OrderData? =
        null


    // Map
    private lateinit var mapView: MapView


    // Text
    private lateinit var tvStatus: TextView

    private lateinit var tvPickupAddress: TextView
    private lateinit var tvPickupDetails: TextView

    private lateinit var tvDropoffAddress: TextView
    private lateinit var tvDropoffDetails: TextView


    // Button
    private lateinit var btnUpdateStatus: MaterialButton


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)


        // Initialize MapLibre first.
        //
        // This is the same setup used
        // by your Customer map picker.
        MapLibre.getInstance(
            this,
            "",
            WellKnownTileServer.MapLibre
        )


        setContentView(
            R.layout.rider_active_delivery
        )


        setupViews()


        // Start MapView
        mapView.onCreate(
            savedInstanceState
        )


        setupButtons()

        loadRider()
    }


    private fun setupViews() {

        mapView =
            findViewById(
                R.id.mapView
            )


        tvStatus =
            findViewById(
                R.id.tvStatus
            )


        tvPickupAddress =
            findViewById(
                R.id.tvPickupAddress
            )


        tvPickupDetails =
            findViewById(
                R.id.tvPickupDetails
            )


        tvDropoffAddress =
            findViewById(
                R.id.tvDropoffAddress
            )


        tvDropoffDetails =
            findViewById(
                R.id.tvDropoffDetails
            )


        btnUpdateStatus =
            findViewById(
                R.id.btnUpdateStatus
            )
    }


    private fun setupButtons() {

        val btnBack =
            findViewById<ImageButton>(
                R.id.btnBack
            )


        btnBack.setOnClickListener {

            finish()
        }


        btnUpdateStatus.setOnClickListener {

            updateDeliveryStatus()
        }
    }


    private fun loadRider() {

        val currentUser =
            firebaseAuth.currentUser


        if (currentUser == null) {

            showMessage(
                "Rider is not logged in."
            )

            finish()

            return
        }


        riderDatabase.getRider(
            currentUser.uid
        ) { success, message, rider ->


            if (success && rider != null) {

                currentRider =
                    rider


                if (rider.currentOrderId.isBlank()) {

                    showMessage(
                        "You have no active delivery."
                    )

                    finish()

                    return@getRider
                }


                loadCurrentOrder(
                    rider
                )

            } else {

                showMessage(
                    message
                )

                finish()
            }
        }
    }


    private fun loadCurrentOrder(
        rider: Rider
    ) {

        riderOrder.getCurrentOrder(
            rider
        ) { success, message, order ->


            if (success && order != null) {

                currentOrder =
                    order


                showOrderInformation(
                    order
                )


                showDeliveryMap(
                    order
                )

            } else {

                showMessage(
                    message
                )

                finish()
            }
        }
    }


    private fun showOrderInformation(
        order: OrderData
    ) {

        // -------------------------
        // STATUS
        // -------------------------

        tvStatus.text =
            order.status


        // -------------------------
        // PICKUP
        // -------------------------

        tvPickupAddress.text =
            order.pickupAddress


        if (order.pickupAddressDetails.isBlank()) {

            tvPickupDetails.text =
                "No additional pickup details."

        } else {

            tvPickupDetails.text =
                order.pickupAddressDetails
        }


        // -------------------------
        // DROP-OFF
        // -------------------------

        tvDropoffAddress.text =
            order.dropAddress


        if (order.dropAddressDetails.isBlank()) {

            tvDropoffDetails.text =
                "No additional drop-off details."

        } else {

            tvDropoffDetails.text =
                order.dropAddressDetails
        }


        // -------------------------
        // BUTTON
        // -------------------------

        if (order.status == "Pick Up Pending") {

            btnUpdateStatus.text =
                "Mark as Out for Delivery"

            btnUpdateStatus.isEnabled =
                true

        } else if (order.status == "Out for Delivery") {

            btnUpdateStatus.text =
                "Mark as Delivered"

            btnUpdateStatus.isEnabled =
                true

        } else {

            btnUpdateStatus.text =
                order.status

            btnUpdateStatus.isEnabled =
                false
        }
    }


    private fun showDeliveryMap(
        order: OrderData
    ) {

        val pickupLocation =
            LatLng(
                order.pickupLatitude,
                order.pickupLongitude
            )


        val dropoffLocation =
            LatLng(
                order.dropLatitude,
                order.dropLongitude
            )


        mapView.getMapAsync { map ->


            map.setStyle(
                "https://tiles.openfreemap.org/styles/liberty"
            ) {


                // Remove old markers first
                map.clear()


                // -------------------------
                // PICKUP MARKER
                // -------------------------

                map.addMarker(

                    MarkerOptions()
                        .position(
                            pickupLocation
                        )
                        .title(
                            "Pickup"
                        )
                        .snippet(
                            order.pickupAddress
                        )
                )


                // -------------------------
                // DROP-OFF MARKER
                // -------------------------

                map.addMarker(

                    MarkerOptions()
                        .position(
                            dropoffLocation
                        )
                        .title(
                            "Drop-off"
                        )
                        .snippet(
                            order.dropAddress
                        )
                )


                // Find the middle point between
                // Pickup and Drop-off.
                val middleLatitude =
                    (
                            order.pickupLatitude +
                                    order.dropLatitude
                            ) / 2


                val middleLongitude =
                    (
                            order.pickupLongitude +
                                    order.dropLongitude
                            ) / 2


                val middleLocation =
                    LatLng(
                        middleLatitude,
                        middleLongitude
                    )


                // Move camera so Rider can
                // see the delivery area.
                map.cameraPosition =
                    CameraPosition.Builder()
                        .target(
                            middleLocation
                        )
                        .zoom(
                            12.0
                        )
                        .build()
            }
        }
    }


    private fun updateDeliveryStatus() {

        val rider =
            currentRider


        val order =
            currentOrder


        if (rider == null) {

            showMessage(
                "Rider information is not ready."
            )

            return
        }


        if (order == null) {

            showMessage(
                "Order information is not ready."
            )

            return
        }


        btnUpdateStatus.isEnabled =
            false


        riderOrder.updateDeliveryStatus(
            rider
        ) { success, message ->


            showMessage(
                message
            )


            if (success) {


                // If the order was:
                //
                // Pick Up Pending
                //       ↓
                // Out for Delivery
                //
                // reload it so the button changes.
                if (
                    order.status ==
                    "Pick Up Pending"
                ) {

                    loadRider()

                } else if (
                    order.status ==
                    "Out for Delivery"
                ) {


                    // Delivery is now finished.
                    //
                    // RiderOrder already changed:
                    //
                    // isAvailable = true
                    // currentOrderId = ""
                    //
                    // We return to Rider Home.
                    finish()
                }

            } else {

                btnUpdateStatus.isEnabled =
                    true
            }
        }
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


    // -----------------------------------
    // MAP LIFECYCLE
    // -----------------------------------

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
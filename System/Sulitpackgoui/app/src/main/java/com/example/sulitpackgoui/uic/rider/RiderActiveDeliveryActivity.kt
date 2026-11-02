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

    private var firebaseAuth =
        FirebaseAuth.getInstance()


    private var riderDatabase =
        RiderDatabase()


    private var riderOrder =
        RiderOrder()


    private var currentRider =
        Rider()


    private var currentOrder =
        OrderData()


    private var riderLoaded =
        false


    private var orderLoaded =
        false


    private lateinit var mapView:
            MapView


    private lateinit var tvStatus:
            TextView

    private lateinit var tvPickupAddress:
            TextView

    private lateinit var tvPickupDetails:
            TextView

    private lateinit var tvDropoffAddress:
            TextView

    private lateinit var tvDropoffDetails:
            TextView


    private lateinit var btnUpdateStatus:
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
            R.layout.rider_active_delivery
        )


        setupViews()


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

        var btnBack =
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

        var currentUser =
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


            if (success == false) {

                showMessage(
                    message
                )


                finish()

            } else if (
                rider == null
            ) {

                showMessage(
                    "Rider profile not found."
                )


                finish()

            } else {

                currentRider =
                    rider


                riderLoaded =
                    true


                checkCurrentOrder()
            }
        }
    }


    private fun checkCurrentOrder() {

        if (
            currentRider.currentOrderId.isBlank()
        ) {

            showMessage(
                "You have no active delivery."
            )


            finish()

            return
        }


        loadCurrentOrder()
    }


    private fun loadCurrentOrder() {

        riderOrder.getCurrentOrder(
            currentRider
        ) { success, message, order ->


            if (success == false) {

                showMessage(
                    message
                )


                finish()

            } else if (
                order == null
            ) {

                showMessage(
                    "Order not found."
                )


                finish()

            } else {

                currentOrder =
                    order


                orderLoaded =
                    true


                showOrderInformation()

                showDeliveryMap()
            }
        }
    }


    private fun showOrderInformation() {

        tvStatus.text =
            currentOrder.status


        tvPickupAddress.text =
            currentOrder.pickupAddress


        if (
            currentOrder.pickupAddressDetails.isBlank()
        ) {

            tvPickupDetails.text =
                "No additional pickup details."

        } else {

            tvPickupDetails.text =
                currentOrder.pickupAddressDetails
        }


        tvDropoffAddress.text =
            currentOrder.dropAddress


        if (
            currentOrder.dropAddressDetails.isBlank()
        ) {

            tvDropoffDetails.text =
                "No additional drop-off details."

        } else {

            tvDropoffDetails.text =
                currentOrder.dropAddressDetails
        }


        if (
            currentOrder.status ==
            "Pick Up Pending"
        ) {

            btnUpdateStatus.text =
                "Mark as Out for Delivery"


            btnUpdateStatus.isEnabled =
                true

        } else if (
            currentOrder.status ==
            "Out for Delivery"
        ) {

            btnUpdateStatus.text =
                "Mark as Delivered"


            btnUpdateStatus.isEnabled =
                true

        } else {

            btnUpdateStatus.text =
                currentOrder.status


            btnUpdateStatus.isEnabled =
                false
        }
    }


    private fun showDeliveryMap() {

        var pickupLocation =
            LatLng(
                currentOrder.pickupLatitude,
                currentOrder.pickupLongitude
            )


        var dropoffLocation =
            LatLng(
                currentOrder.dropLatitude,
                currentOrder.dropLongitude
            )


        mapView.getMapAsync { map ->


            map.setStyle(
                "https://tiles.openfreemap.org/styles/liberty"
            ) {


                map.clear()


                map.addMarker(
                    MarkerOptions()
                        .position(
                            pickupLocation
                        )
                        .title(
                            "Pickup"
                        )
                        .snippet(
                            currentOrder.pickupAddress
                        )
                )


                map.addMarker(
                    MarkerOptions()
                        .position(
                            dropoffLocation
                        )
                        .title(
                            "Drop-off"
                        )
                        .snippet(
                            currentOrder.dropAddress
                        )
                )


                var middleLatitude =
                    (
                            currentOrder.pickupLatitude +
                                    currentOrder.dropLatitude
                            ) / 2


                var middleLongitude =
                    (
                            currentOrder.pickupLongitude +
                                    currentOrder.dropLongitude
                            ) / 2


                var middleLocation =
                    LatLng(
                        middleLatitude,
                        middleLongitude
                    )


                map.cameraPosition =
                    CameraPosition
                        .Builder()
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

        if (riderLoaded == false) {

            showMessage(
                "Rider information is not ready."
            )

            return
        }


        if (orderLoaded == false) {

            showMessage(
                "Order information is not ready."
            )

            return
        }


        var oldStatus =
            currentOrder.status


        btnUpdateStatus.isEnabled =
            false


        riderOrder.updateDeliveryStatus(
            currentRider
        ) { success, message ->


            showMessage(
                message
            )


            if (success == false) {

                btnUpdateStatus.isEnabled =
                    true

            } else {

                handleSuccessfulStatusChange(
                    oldStatus
                )
            }
        }
    }


    private fun handleSuccessfulStatusChange(
        oldStatus: String
    ) {

        if (
            oldStatus ==
            "Pick Up Pending"
        ) {

            loadRider()

        } else if (
            oldStatus ==
            "Out for Delivery"
        ) {

            finish()
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
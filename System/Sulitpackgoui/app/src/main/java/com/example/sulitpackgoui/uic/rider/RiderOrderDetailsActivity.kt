package com.example.sulitpackgoui.uic.rider

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.google.firebase.firestore.FirebaseFirestore

class RiderOrderDetailsActivity : ComponentActivity() {

    private var firebaseAuth =
        FirebaseAuth.getInstance()

    private var database =
        FirebaseFirestore.getInstance()

    private var riderDatabase =
        RiderDatabase()

    private var riderOrder =
        RiderOrder()


    private var currentRider: Rider? =
        null

    private var currentOrder: OrderData? =
        null

    private var orderId =
        ""


    // Order information
    private lateinit var tvOrderNumber: TextView
    private lateinit var tvDeliveryFee: TextView

    private lateinit var tvPickup: TextView
    private lateinit var tvDropoff: TextView

    private lateinit var tvDistance: TextView
    private lateinit var tvWeight: TextView
    private lateinit var tvVehicle: TextView

    private lateinit var tvDescription: TextView
    private lateinit var tvPickupDetails: TextView

    private lateinit var tvCustomerName: TextView
    private lateinit var tvCustomerPhone: TextView

    private lateinit var btnAcceptOrder: MaterialButton
    private lateinit var btnDeclineOrder: MaterialButton


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.rider_order_details
        )


        setupViews()

        setupButtons()


        // Get the order ID sent from
        // RiderAvailableOrdersActivity.
        orderId =
            intent.getStringExtra(
                "orderId"
            ) ?: ""


        if (orderId.isBlank()) {

            showMessage(
                "Order ID not found."
            )

            finish()

            return
        }


        loadRider()
    }


    private fun setupViews() {

        tvOrderNumber =
            findViewById(
                R.id.tvOrderNumber
            )


        tvDeliveryFee =
            findViewById(
                R.id.tvDeliveryFee
            )


        tvPickup =
            findViewById(
                R.id.tvPickup
            )


        tvDropoff =
            findViewById(
                R.id.tvDropoff
            )


        tvDistance =
            findViewById(
                R.id.tvDistance
            )


        tvWeight =
            findViewById(
                R.id.tvWeight
            )


        tvVehicle =
            findViewById(
                R.id.tvVehicle
            )


        tvDescription =
            findViewById(
                R.id.tvDescription
            )


        tvPickupDetails =
            findViewById(
                R.id.tvPickupDetails
            )


        tvCustomerName =
            findViewById(
                R.id.tvCustomerName
            )


        tvCustomerPhone =
            findViewById(
                R.id.tvCustomerPhone
            )


        btnAcceptOrder =
            findViewById(
                R.id.btnAcceptOrder
            )


        btnDeclineOrder =
            findViewById(
                R.id.btnDeclineOrder
            )


        // Your customer backend does not
        // currently save a phone number.
        //
        // Because of that, we hide this field
        // instead of showing fake information.
        tvCustomerPhone.visibility =
            View.GONE
    }


    private fun setupButtons() {

        val btnBack =
            findViewById<ImageButton>(
                R.id.btnBack
            )


        btnBack.setOnClickListener {

            finish()
        }


        btnDeclineOrder.setOnClickListener {

            // Decline does NOT cancel the order.
            //
            // The Rider simply chooses not
            // to accept this order.
            finish()
        }


        btnAcceptOrder.setOnClickListener {

            acceptOrder()
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


                loadOrder()

            } else {

                showMessage(
                    message
                )

                finish()
            }
        }
    }


    private fun loadOrder() {

        riderOrder.getOrder(
            orderId
        ) { success, message, order ->


            if (success && order != null) {

                currentOrder =
                    order


                showOrderInformation(
                    order
                )


                loadCustomerName(
                    order.customerId
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

        tvOrderNumber.text =
            "Order #${order.orderId}"


        tvDeliveryFee.text =
            "₱${String.format("%.2f", order.fee)}"


        tvPickup.text =
            order.pickupAddress


        tvDropoff.text =
            order.dropAddress


        tvDistance.text =
            "${String.format("%.2f", order.distance)} km"


        tvWeight.text =
            "${String.format("%.2f", order.weight)} kg"


        tvVehicle.text =
            order.vehicleType


        tvDescription.text =
            order.description


        // Show pickup address details
        // if the customer entered one.
        if (order.pickupAddressDetails.isBlank()) {

            tvPickupDetails.text =
                "No pickup details."

        } else {

            tvPickupDetails.text =
                "Pickup details: ${order.pickupAddressDetails}"
        }
    }


    private fun loadCustomerName(
        customerId: String
    ) {

        if (customerId.isBlank()) {

            tvCustomerName.text =
                "Customer"

            return
        }


        database
            .collection("users")
            .document(customerId)
            .get()
            .addOnSuccessListener { document ->


                if (document.exists()) {

                    val customerName =
                        document.getString("name")
                            ?: "Customer"


                    tvCustomerName.text =
                        customerName

                } else {

                    tvCustomerName.text =
                        "Customer"
                }
            }
            .addOnFailureListener {

                tvCustomerName.text =
                    "Customer"
            }
    }


    private fun acceptOrder() {

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


        // Prevent multiple clicks while
        // Firebase is processing.
        btnAcceptOrder.isEnabled =
            false


        riderOrder.acceptOrder(
            rider,
            order.orderId
        ) { success, message ->


            if (success) {

                showMessage(
                    message
                )


                openCurrentDelivery()

            } else {

                btnAcceptOrder.isEnabled =
                    true


                showMessage(
                    message
                )
            }
        }
    }


    private fun openCurrentDelivery() {

        val intent =
            Intent(
                this,
                RiderActiveDeliveryActivity::class.java
            )


        startActivity(
            intent
        )


        finish()
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
}
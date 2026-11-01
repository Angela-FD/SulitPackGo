package com.example.sulitpackgoui.uic.rider

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.customer.CustomerDatabase
import com.example.sulitpackgoui.order.OrderData
import com.example.sulitpackgoui.rider.Rider
import com.example.sulitpackgoui.rider.RiderDatabase
import com.example.sulitpackgoui.rider.RiderOrder
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class RiderOrderDetailsActivity : ComponentActivity() {

    private var auth =
        FirebaseAuth.getInstance()


    private var riderDatabase =
        RiderDatabase()


    private var riderOrder =
        RiderOrder()


    private var customerDatabase =
        CustomerDatabase()


    private var currentRider =
        Rider()


    private var currentOrder =
        OrderData()


    private var riderLoaded =
        false


    private var orderLoaded =
        false


    private lateinit var tvOrderId:
            TextView

    private lateinit var tvFee:
            TextView

    private lateinit var tvCustomerName:
            TextView

    private lateinit var tvCustomerPhone:
            TextView

    private lateinit var tvPickupAddress:
            TextView

    private lateinit var tvPickupDetails:
            TextView

    private lateinit var tvDropoffAddress:
            TextView

    private lateinit var tvDropoffDetails:
            TextView

    private lateinit var tvDescription:
            TextView

    private lateinit var tvWeight:
            TextView

    private lateinit var tvDistance:
            TextView

    private lateinit var tvVehicle:
            TextView

    private lateinit var btnAccept:
            MaterialButton

    private lateinit var btnDecline:
            MaterialButton


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        setContentView(
            R.layout.rider_order_details
        )


        setupViews()

        setupButtons()


        var orderId =
            readTextExtra(
                "orderId"
            )


        if (orderId.isBlank()) {

            showMessage(
                "Order not found."
            )


            finish()

            return
        }


        loadRider(
            orderId
        )
    }


    private fun setupViews() {

        tvOrderId =
            findViewById(
                R.id.tvOrderId
            )


        tvFee =
            findViewById(
                R.id.tvFee
            )


        tvCustomerName =
            findViewById(
                R.id.tvCustomerName
            )


        tvCustomerPhone =
            findViewById(
                R.id.tvCustomerPhone
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


        tvDescription =
            findViewById(
                R.id.tvDescription
            )


        tvWeight =
            findViewById(
                R.id.tvWeight
            )


        tvDistance =
            findViewById(
                R.id.tvDistance
            )


        tvVehicle =
            findViewById(
                R.id.tvVehicle
            )


        btnAccept =
            findViewById(
                R.id.btnAccept
            )


        btnDecline =
            findViewById(
                R.id.btnDecline
            )
    }


    private fun setupButtons() {

        var btnBack =
            findViewById<TextView>(
                R.id.btnBack
            )


        btnBack.setOnClickListener {

            finish()
        }


        btnDecline.setOnClickListener {

            finish()
        }


        btnAccept.setOnClickListener {

            acceptOrder()
        }
    }


    private fun loadRider(
        orderId: String
    ) {

        var user =
            auth.currentUser


        if (user == null) {

            showMessage(
                "Rider is not logged in."
            )


            finish()

            return
        }


        riderDatabase.getRider(
            user.uid
        ) { success, message, rider ->


            if (success == false) {

                showMessage(
                    message
                )

            } else if (
                rider == null
            ) {

                showMessage(
                    "Rider profile not found."
                )

            } else {

                currentRider =
                    rider


                riderLoaded =
                    true


                loadOrder(
                    orderId
                )
            }
        }
    }


    private fun loadOrder(
        orderId: String
    ) {

        riderOrder.getOrder(
            orderId
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


                showOrder()

                loadCustomer(
                    order.customerId
                )
            }
        }
    }


    private fun showOrder() {

        tvOrderId.text =
            "Order #" +
                    currentOrder.orderId


        tvFee.text =
            formatMoney(
                currentOrder.fee
            )


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
            currentOrder.description.isBlank()
        ) {

            tvDescription.text =
                "No description"

        } else {

            tvDescription.text =
                currentOrder.description
        }


        tvWeight.text =
            "Weight: " +
                    formatNumber(
                        currentOrder.weight
                    ) +
                    " kg"


        tvDistance.text =
            "Distance: " +
                    formatNumber(
                        currentOrder.distance
                    ) +
                    " km"


        tvVehicle.text =
            "Vehicle: " +
                    currentOrder.vehicleType
    }


    private fun loadCustomer(
        customerId: String
    ) {

        if (customerId.isBlank()) {

            showUnknownCustomer()

            return
        }


        customerDatabase.getCustomer(
            customerId
        ) { success, message, customer ->


            if (success == false) {

                showUnknownCustomer()

            } else if (
                customer == null
            ) {

                showUnknownCustomer()

            } else {

                tvCustomerName.text =
                    customer.name


                if (
                    customer.phoneNumber.isBlank()
                ) {

                    tvCustomerPhone.text =
                        "Not provided"

                } else {

                    tvCustomerPhone.text =
                        customer.phoneNumber
                }
            }
        }
    }


    private fun showUnknownCustomer() {

        tvCustomerName.text =
            "Customer"


        tvCustomerPhone.text =
            "Not provided"
    }


    private fun acceptOrder() {

        if (riderLoaded == false) {

            showMessage(
                "Rider information is still loading."
            )

            return
        }


        if (orderLoaded == false) {

            showMessage(
                "Order information is still loading."
            )

            return
        }


        btnAccept.isEnabled =
            false


        riderOrder.acceptOrder(
            currentRider,
            currentOrder.orderId
        ) { success, message ->


            if (success) {

                showMessage(
                    message
                )


                openCurrentDelivery()

            } else {

                btnAccept.isEnabled =
                    true


                showMessage(
                    message
                )
            }
        }
    }


    private fun openCurrentDelivery() {

        var intent =
            Intent(
                this,
                RiderActiveDeliveryActivity::class.java
            )


        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP
        )


        startActivity(
            intent
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


    private fun formatNumber(
        number: Double
    ): String {

        return String.format(
            "%.2f",
            number
        )
    }


    private fun formatMoney(
        amount: Double
    ): String {

        return "₱" +
                formatNumber(
                    amount
                )
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
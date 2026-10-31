package com.example.sulitpackgoui.uic.customer

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.admin.VehicleFeeDatabase
import com.example.sulitpackgoui.customer.Customer
import com.example.sulitpackgoui.customer.CustomerOrder
import com.example.sulitpackgoui.logic.DeliveryLocation
import com.example.sulitpackgoui.logic.FeeCalculator
import com.example.sulitpackgoui.logic.PackageInfo
import com.example.sulitpackgoui.logic.RouteDistance
import com.example.sulitpackgoui.logic.Vehicle
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class CustomerReviewActivity : ComponentActivity() {

    private var location =
        DeliveryLocation()

    private var vehicle =
        Vehicle()

    private var routeDistance =
        RouteDistance()

    private var feeDatabase =
        VehicleFeeDatabase()

    private var customerOrder =
        CustomerOrder()

    private lateinit var tvPickup: TextView
    private lateinit var tvDropoff: TextView
    private lateinit var tvVehicle: TextView
    private lateinit var tvPackage: TextView

    private lateinit var tvBaseFee: TextView
    private lateinit var tvDistanceFee: TextView
    private lateinit var tvTotalFee: TextView

    private lateinit var btnPlaceOrder: MaterialButton

    private var feeReady = false

    private var totalFee = 0.0

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.customer_review
        )

        setupViews()
        getOrderData()
        showOrderData()
        calculateFee()
        setupButtons()
    }

    private fun setupViews() {

        tvPickup =
            findViewById(R.id.tvPickupValue)

        tvDropoff =
            findViewById(R.id.tvDropoffValue)

        tvVehicle =
            findViewById(R.id.tvVehicleValue)

        tvPackage =
            findViewById(R.id.tvPackageValue)

        tvBaseFee =
            findViewById(R.id.tvFeeValue)

        tvDistanceFee =
            findViewById(R.id.tvDistanceFeeValue)

        tvTotalFee =
            findViewById(R.id.tvTotalFeeValue)

        btnPlaceOrder =
            findViewById(R.id.btnPlaceOrder)

        btnPlaceOrder.isEnabled = false
    }

    private fun getOrderData() {

        location.address =
            intent.getStringExtra(
                "pickupAddress"
            ) ?: ""

        location.addressDetails =
            intent.getStringExtra(
                "pickupAddressDetails"
            ) ?: ""

        location.pickUpLatitude =
            intent.getDoubleExtra(
                "pickupLatitude",
                0.0
            )

        location.pickUpLongitude =
            intent.getDoubleExtra(
                "pickupLongitude",
                0.0
            )

        location.dropAddress =
            intent.getStringExtra(
                "dropAddress"
            ) ?: ""

        location.dropAddressDetails =
            intent.getStringExtra(
                "dropAddressDetails"
            ) ?: ""

        location.dropLatitude =
            intent.getDoubleExtra(
                "dropLatitude",
                0.0
            )

        location.dropLongitude =
            intent.getDoubleExtra(
                "dropLongitude",
                0.0
            )

        var vehicleType =
            intent.getStringExtra(
                "vehicleType"
            ) ?: ""

        vehicle.selectVehicle(
            vehicleType
        )
    }

    private fun showOrderData() {

        var description =
            intent.getStringExtra(
                "description"
            ) ?: ""

        var weight =
            intent.getDoubleExtra(
                "weight",
                0.0
            )

        var pieces =
            intent.getIntExtra(
                "pieces",
                0
            )

        tvPickup.text =
            location.address

        tvDropoff.text =
            location.dropAddress

        tvVehicle.text =
            vehicle.vehicleType

        tvPackage.text =
            "$description • $weight kg • $pieces piece(s)"
    }

    private fun calculateFee() {

        routeDistance.getRoadDistance(
            location
        ) { success, distance, message ->

            if (!success) {
                showMessage(message)
                return@getRoadDistance
            }

            feeDatabase.getVehicleFee(
                vehicle.vehicleType
            ) { feeSuccess, feeMessage, vehicleFee ->

                if (
                    !feeSuccess ||
                    vehicleFee == null
                ) {

                    showMessage(feeMessage)

                    return@getVehicleFee
                }

                var calculator =
                    FeeCalculator(
                        devLocation = location,
                        feeVehicle = vehicle,
                        vehicleFee = vehicleFee,
                        roadDistanceKm = distance
                    )

                var baseFee =
                    calculator.calculateBaseFee()

                var distanceFee =
                    calculator.calculateDistanceFee()

                totalFee =
                    calculator.calculateTotalFee()

                runOnUiThread {

                    tvBaseFee.text =
                        "₱%.2f".format(
                            baseFee
                        )

                    tvDistanceFee.text =
                        "₱%.2f".format(
                            distanceFee
                        )

                    tvTotalFee.text =
                        "₱%.2f".format(
                            totalFee
                        )

                    feeReady = true

                    btnPlaceOrder.isEnabled =
                        true
                }
            }
        }
    }

    private fun setupButtons() {

        var btnBack =
            findViewById<TextView>(
                R.id.btnBack
            )

        btnBack.setOnClickListener {
            finish()
        }

        btnPlaceOrder.setOnClickListener {

            if (!feeReady) {

                showMessage(
                    "Please wait for the delivery fee."
                )

                return@setOnClickListener
            }

            placeOrder()
        }
    }

    private fun placeOrder() {

        var firebaseUser =
            FirebaseAuth
                .getInstance()
                .currentUser

        if (firebaseUser == null) {

            showMessage(
                "Please log in again."
            )

            return
        }

        var customerName =
            intent.getStringExtra(
                "customerName"
            ) ?: "Customer"

        var customer =
            Customer(
                uid = firebaseUser.uid,
                name = customerName,
                email = firebaseUser.email ?: "",
                role = "customer"
            )

        var packageInfo =
            PackageInfo(
                description =
                    intent.getStringExtra(
                        "description"
                    ) ?: "",

                weight =
                    intent.getDoubleExtra(
                        "weight",
                        0.0
                    ),

                pieces =
                    intent.getIntExtra(
                        "pieces",
                        0
                    ),

                instructions =
                    intent.getStringExtra(
                        "instructions"
                    ) ?: ""
            )

        btnPlaceOrder.isEnabled = false

        btnPlaceOrder.text =
            "Placing Order..."

        customerOrder.createOrder(
            customer,
            location,
            packageInfo,
            vehicle
        ) { success, message, orderId ->

            runOnUiThread {

                if (success) {

                    openConfirmation(
                        orderId,
                        customerName
                    )

                } else {

                    showMessage(message)

                    btnPlaceOrder.text =
                        "Place Order"

                    btnPlaceOrder.isEnabled =
                        true
                }
            }
        }
    }

    private fun openConfirmation(
        orderId: String,
        customerName: String
    ) {

        var confirmationIntent =
            Intent(
                this,
                CustomerConfirmationActivity::class.java
            )

        confirmationIntent.putExtra(
            "orderId",
            orderId
        )

        confirmationIntent.putExtra(
            "totalFee",
            totalFee
        )

        confirmationIntent.putExtra(
            "customerName",
            customerName
        )

        startActivity(
            confirmationIntent
        )

        finish()
    }

    private fun showMessage(
        message: String
    ) {

        runOnUiThread {

            Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
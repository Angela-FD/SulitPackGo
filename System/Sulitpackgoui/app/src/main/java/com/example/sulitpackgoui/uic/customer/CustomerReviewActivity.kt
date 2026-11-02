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


    private var packageInfo =
        PackageInfo()


    private var vehicle =
        Vehicle()


    private var routeDistance =
        RouteDistance()


    private var feeDatabase =
        VehicleFeeDatabase()


    private var customerOrder =
        CustomerOrder()


    private var feeReady =
        false


    private var totalFee =
        0.0


    private lateinit var tvPickup:
            TextView

    private lateinit var tvDropoff:
            TextView

    private lateinit var tvVehicle:
            TextView

    private lateinit var tvPackage:
            TextView

    private lateinit var tvBaseFee:
            TextView

    private lateinit var tvDistanceFee:
            TextView

    private lateinit var tvTotalFee:
            TextView

    private lateinit var btnPlaceOrder:
            MaterialButton


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
            findViewById(
                R.id.tvPickupValue
            )


        tvDropoff =
            findViewById(
                R.id.tvDropoffValue
            )


        tvVehicle =
            findViewById(
                R.id.tvVehicleValue
            )


        tvPackage =
            findViewById(
                R.id.tvPackageValue
            )


        tvBaseFee =
            findViewById(
                R.id.tvFeeValue
            )


        tvDistanceFee =
            findViewById(
                R.id.tvDistanceFeeValue
            )


        tvTotalFee =
            findViewById(
                R.id.tvTotalFeeValue
            )


        btnPlaceOrder =
            findViewById(
                R.id.btnPlaceOrder
            )


        btnPlaceOrder.isEnabled =
            false
    }


    private fun getOrderData() {

        location.address =
            readTextExtra(
                "pickupAddress"
            )


        location.addressDetails =
            readTextExtra(
                "pickupAddressDetails"
            )


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
            readTextExtra(
                "dropAddress"
            )


        location.dropAddressDetails =
            readTextExtra(
                "dropAddressDetails"
            )


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
            readTextExtra(
                "vehicleType"
            )


        vehicle.selectVehicle(
            vehicleType
        )


        packageInfo.description =
            readTextExtra(
                "description"
            )


        packageInfo.weight =
            intent.getDoubleExtra(
                "weight",
                0.0
            )


        packageInfo.pieces =
            intent.getIntExtra(
                "pieces",
                0
            )


        packageInfo.instructions =
            readTextExtra(
                "instructions"
            )
    }


    private fun showOrderData() {

        tvPickup.text =
            location.address


        tvDropoff.text =
            location.dropAddress


        tvVehicle.text =
            vehicle.vehicleType


        tvPackage.text =
            packageInfo.description +
                    " • " +
                    packageInfo.weight.toString() +
                    " kg • " +
                    packageInfo.pieces.toString() +
                    " piece(s)"
    }


    private fun calculateFee() {

        routeDistance.getRoadDistance(
            location
        ) { success, distance, message ->


            if (success == false) {

                showMessage(
                    message
                )

            } else {

                loadVehicleFee(
                    distance
                )
            }
        }
    }


    private fun loadVehicleFee(
        distance: Double
    ) {

        feeDatabase.getVehicleFee(
            vehicle.vehicleType
        ) { success, message, vehicleFee ->


            if (success == false) {

                showMessage(
                    message
                )

            } else if (
                vehicleFee == null
            ) {

                showMessage(
                    "Vehicle fee not found."
                )

            } else {

                var calculator =
                    FeeCalculator(
                        vehicleFee = vehicleFee,
                        roadDistanceKm = distance
                    )


                var baseFee =
                    calculator.calculateBaseFee()


                var distanceFee =
                    calculator.calculateDistanceFee()


                totalFee =
                    calculator.calculateTotalFee()


                showFee(
                    baseFee,
                    distanceFee,
                    totalFee
                )
            }
        }
    }


    private fun showFee(
        baseFee: Double,
        distanceFee: Double,
        total: Double
    ) {

        runOnUiThread {

            tvBaseFee.text =
                formatMoney(
                    baseFee
                )


            tvDistanceFee.text =
                formatMoney(
                    distanceFee
                )


            tvTotalFee.text =
                formatMoney(
                    total
                )


            feeReady =
                true


            btnPlaceOrder.isEnabled =
                true
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

            if (feeReady) {

                placeOrder()

            } else {

                showMessage(
                    "Please wait for the delivery fee."
                )
            }
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


        var customer =
            Customer(
                uid = firebaseUser.uid
            )


        btnPlaceOrder.isEnabled =
            false


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
                        orderId
                    )

                } else {

                    showMessage(
                        message
                    )


                    btnPlaceOrder.text =
                        "Place Order"


                    btnPlaceOrder.isEnabled =
                        true
                }
            }
        }
    }


    private fun openConfirmation(
        orderId: String
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


        startActivity(
            confirmationIntent
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


    private fun formatMoney(
        amount: Double
    ): String {

        return "₱" +
                String.format(
                    "%.2f",
                    amount
                )
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
package com.example.sulitpackgoui.uic.customer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.customer.CustomerDatabase
import com.example.sulitpackgoui.logic.Vehicle
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class CustomerHomeActivity : ComponentActivity() {

    private var vehicle =
        Vehicle()


    private var pickupLatitude =
        0.0

    private var pickupLongitude =
        0.0


    private var dropLatitude =
        0.0

    private var dropLongitude =
        0.0


    private lateinit var tvCustomerName:
            TextView

    private lateinit var tvPickupLocationStatus:
            TextView

    private lateinit var tvDropoffLocationStatus:
            TextView


    private var auth =
        FirebaseAuth.getInstance()


    private var customerDatabase =
        CustomerDatabase()


    private val pickupLocationLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->


            if (
                result.resultCode ==
                Activity.RESULT_OK
            ) {

                var data =
                    result.data


                if (data != null) {

                    pickupLatitude =
                        data.getDoubleExtra(
                            "latitude",
                            0.0
                        )


                    pickupLongitude =
                        data.getDoubleExtra(
                            "longitude",
                            0.0
                        )


                    tvPickupLocationStatus.text =
                        "✓ Precise pickup location selected"


                    tvPickupLocationStatus
                        .setTextColor(
                            getColor(
                                android.R.color.holo_green_dark
                            )
                        )
                }
            }
        }


    private val dropoffLocationLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->


            if (
                result.resultCode ==
                Activity.RESULT_OK
            ) {

                var data =
                    result.data


                if (data != null) {

                    dropLatitude =
                        data.getDoubleExtra(
                            "latitude",
                            0.0
                        )


                    dropLongitude =
                        data.getDoubleExtra(
                            "longitude",
                            0.0
                        )


                    tvDropoffLocationStatus.text =
                        "✓ Precise drop-off location selected"


                    tvDropoffLocationStatus
                        .setTextColor(
                            getColor(
                                android.R.color.holo_green_dark
                            )
                        )
                }
            }
        }


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        setContentView(
            R.layout.customer_home
        )


        setupHome()

        loadCustomerName()
    }


    override fun onResume() {

        super.onResume()


        loadCustomerName()
    }


    private fun setupHome() {

        tvCustomerName =
            findViewById(
                R.id.tvCustomerName
            )


        var etPickupAddress =
            findViewById<EditText>(
                R.id.etPickupAddress
            )


        var etPickupDetails =
            findViewById<EditText>(
                R.id.etPickupDetails
            )


        var etDropAddress =
            findViewById<EditText>(
                R.id.etDropAddress
            )


        var etDropDetails =
            findViewById<EditText>(
                R.id.etDropDetails
            )


        tvPickupLocationStatus =
            findViewById(
                R.id.tvPickupLocationStatus
            )


        tvDropoffLocationStatus =
            findViewById(
                R.id.tvDropoffLocationStatus
            )


        var btnSelectPickup =
            findViewById<MaterialButton>(
                R.id.btnSelectPickupLocation
            )


        var btnSelectDropoff =
            findViewById<MaterialButton>(
                R.id.btnSelectDropoffLocation
            )


        var btnMotor =
            findViewById<TextView>(
                R.id.btnMotor
            )


        var btnTricycle =
            findViewById<TextView>(
                R.id.btnTricycle
            )


        var btnVan =
            findViewById<TextView>(
                R.id.btnVan
            )


        var btnTruck =
            findViewById<TextView>(
                R.id.btnTruck
            )


        var btnContinue =
            findViewById<MaterialButton>(
                R.id.btnContinue
            )


        btnSelectPickup.setOnClickListener {

            openLocationPicker(
                "pickup"
            )
        }


        btnSelectDropoff.setOnClickListener {

            openLocationPicker(
                "dropoff"
            )
        }


        setupVehicles(
            btnMotor,
            btnTricycle,
            btnVan,
            btnTruck
        )


        setupNavigation()


        btnContinue.setOnClickListener {

            var pickupAddress =
                etPickupAddress
                    .text
                    .toString()
                    .trim()


            var pickupDetails =
                etPickupDetails
                    .text
                    .toString()
                    .trim()


            var dropAddress =
                etDropAddress
                    .text
                    .toString()
                    .trim()


            var dropDetails =
                etDropDetails
                    .text
                    .toString()
                    .trim()


            continueOrder(
                pickupAddress,
                pickupDetails,
                dropAddress,
                dropDetails
            )
        }
    }


    private fun loadCustomerName() {

        var user =
            auth.currentUser


        if (user == null) {

            tvCustomerName.text =
                "Customer"

            return
        }


        customerDatabase.getCustomer(
            user.uid
        ) { success, message, customer ->


            if (success) {

                if (customer != null) {

                    tvCustomerName.text =
                        customer.name

                } else {

                    tvCustomerName.text =
                        "Customer"
                }

            } else {

                tvCustomerName.text =
                    "Customer"
            }
        }
    }


    private fun openLocationPicker(
        type: String
    ) {

        var mapIntent =
            Intent(
                this,
                CustomerLocationPickerActivity::class.java
            )


        mapIntent.putExtra(
            "locationType",
            type
        )


        if (type == "pickup") {

            pickupLocationLauncher.launch(
                mapIntent
            )

        } else {

            dropoffLocationLauncher.launch(
                mapIntent
            )
        }
    }


    private fun setupVehicles(
        motor: TextView,
        tricycle: TextView,
        van: TextView,
        truck: TextView
    ) {

        motor.setOnClickListener {

            selectVehicle(
                "Motor",
                motor,
                tricycle,
                van,
                truck
            )
        }


        tricycle.setOnClickListener {

            selectVehicle(
                "Tricycle",
                motor,
                tricycle,
                van,
                truck
            )
        }


        van.setOnClickListener {

            selectVehicle(
                "Van",
                motor,
                tricycle,
                van,
                truck
            )
        }


        truck.setOnClickListener {

            selectVehicle(
                "Truck",
                motor,
                tricycle,
                van,
                truck
            )
        }
    }


    private fun selectVehicle(
        type: String,
        motor: TextView,
        tricycle: TextView,
        van: TextView,
        truck: TextView
    ) {

        vehicle.selectVehicle(
            type
        )


        motor.alpha =
            0.5f

        tricycle.alpha =
            0.5f

        van.alpha =
            0.5f

        truck.alpha =
            0.5f


        when (type) {

            "Motor" -> {

                motor.alpha =
                    1f
            }


            "Tricycle" -> {

                tricycle.alpha =
                    1f
            }


            "Van" -> {

                van.alpha =
                    1f
            }


            "Truck" -> {

                truck.alpha =
                    1f
            }
        }


        showMessage(
            type + " selected"
        )
    }


    private fun continueOrder(
        pickupAddress: String,
        pickupDetails: String,
        dropAddress: String,
        dropDetails: String
    ) {

        if (pickupAddress.isBlank()) {

            showMessage(
                "Enter the pickup address."
            )

            return
        }


        if (pickupLatitude == 0.0) {

            showMessage(
                "Select the precise pickup location on the map."
            )

            return
        }


        if (pickupLongitude == 0.0) {

            showMessage(
                "Select the precise pickup location on the map."
            )

            return
        }


        if (dropAddress.isBlank()) {

            showMessage(
                "Enter the drop-off address."
            )

            return
        }


        if (dropLatitude == 0.0) {

            showMessage(
                "Select the precise drop-off location on the map."
            )

            return
        }


        if (dropLongitude == 0.0) {

            showMessage(
                "Select the precise drop-off location on the map."
            )

            return
        }


        if (vehicle.vehicleType.isBlank()) {

            showMessage(
                "Select a vehicle."
            )

            return
        }


        var packageIntent =
            Intent(
                this,
                CustomerPackageActivity::class.java
            )


        packageIntent.putExtra(
            "pickupAddress",
            pickupAddress
        )


        packageIntent.putExtra(
            "pickupAddressDetails",
            pickupDetails
        )


        packageIntent.putExtra(
            "pickupLatitude",
            pickupLatitude
        )


        packageIntent.putExtra(
            "pickupLongitude",
            pickupLongitude
        )


        packageIntent.putExtra(
            "dropAddress",
            dropAddress
        )


        packageIntent.putExtra(
            "dropAddressDetails",
            dropDetails
        )


        packageIntent.putExtra(
            "dropLatitude",
            dropLatitude
        )


        packageIntent.putExtra(
            "dropLongitude",
            dropLongitude
        )


        packageIntent.putExtra(
            "vehicleType",
            vehicle.vehicleType
        )


        packageIntent.putExtra(
            "customerName",
            tvCustomerName
                .text
                .toString()
        )


        startActivity(
            packageIntent
        )
    }


    private fun setupNavigation() {

        var navOrders =
            findViewById<LinearLayout>(
                R.id.navOrders
            )


        var navProfile =
            findViewById<LinearLayout>(
                R.id.navProfile
            )


        navOrders.setOnClickListener {

            var intent =
                Intent(
                    this,
                    CustomerOrdersActivity::class.java
                )


            startActivity(
                intent
            )
        }


        navProfile.setOnClickListener {

            var intent =
                Intent(
                    this,
                    CustomerProfileActivity::class.java
                )


            startActivity(
                intent
            )
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
}
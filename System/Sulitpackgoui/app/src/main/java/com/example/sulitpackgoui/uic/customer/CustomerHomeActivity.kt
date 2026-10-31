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
import com.example.sulitpackgoui.logic.Vehicle
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CustomerHomeActivity : ComponentActivity() {

    private var vehicle =
        Vehicle()

    private var pickupLatitude: Double? =
        null

    private var pickupLongitude: Double? =
        null

    private var dropLatitude: Double? =
        null

    private var dropLongitude: Double? =
        null

    private lateinit var tvCustomerName: TextView
    private lateinit var tvPickupLocationStatus: TextView
    private lateinit var tvDropoffLocationStatus: TextView

    private var auth =
        FirebaseAuth.getInstance()

    private var database =
        FirebaseFirestore.getInstance()

    private val pickupLocationLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                pickupLatitude =
                    result.data?.getDoubleExtra(
                        "latitude",
                        0.0
                    )

                pickupLongitude =
                    result.data?.getDoubleExtra(
                        "longitude",
                        0.0
                    )

                tvPickupLocationStatus.text =
                    "✓ Precise pickup location selected"

                tvPickupLocationStatus.setTextColor(
                    getColor(
                        android.R.color.holo_green_dark
                    )
                )
            }
        }

    private val dropoffLocationLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                dropLatitude =
                    result.data?.getDoubleExtra(
                        "latitude",
                        0.0
                    )

                dropLongitude =
                    result.data?.getDoubleExtra(
                        "longitude",
                        0.0
                    )

                tvDropoffLocationStatus.text =
                    "✓ Precise drop-off location selected"

                tvDropoffLocationStatus.setTextColor(
                    getColor(
                        android.R.color.holo_green_dark
                    )
                )
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

        val etPickupAddress =
            findViewById<EditText>(
                R.id.etPickupAddress
            )

        val etPickupDetails =
            findViewById<EditText>(
                R.id.etPickupDetails
            )

        val etDropAddress =
            findViewById<EditText>(
                R.id.etDropAddress
            )

        val etDropDetails =
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

        val btnSelectPickup =
            findViewById<MaterialButton>(
                R.id.btnSelectPickupLocation
            )

        val btnSelectDropoff =
            findViewById<MaterialButton>(
                R.id.btnSelectDropoffLocation
            )

        val btnMotor =
            findViewById<TextView>(
                R.id.btnMotor
            )

        val btnTricycle =
            findViewById<TextView>(
                R.id.btnTricycle
            )

        val btnVan =
            findViewById<TextView>(
                R.id.btnVan
            )

        val btnTruck =
            findViewById<TextView>(
                R.id.btnTruck
            )

        val btnContinue =
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

            continueOrder(
                etPickupAddress.text
                    .toString()
                    .trim(),

                etPickupDetails.text
                    .toString()
                    .trim(),

                etDropAddress.text
                    .toString()
                    .trim(),

                etDropDetails.text
                    .toString()
                    .trim()
            )
        }
    }

    private fun loadCustomerName() {

        val user =
            auth.currentUser

        if (user == null) {

            tvCustomerName.text =
                "Customer"

            return
        }

        database
            .collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->

                tvCustomerName.text =
                    document.getString("name")
                        ?: "Customer"
            }
            .addOnFailureListener {

                tvCustomerName.text =
                    "Customer"
            }
    }

    private fun openLocationPicker(
        type: String
    ) {

        val mapIntent =
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

        vehicle.selectVehicle(type)

        motor.alpha = 0.5f
        tricycle.alpha = 0.5f
        van.alpha = 0.5f
        truck.alpha = 0.5f

        when (type) {

            "Motor" ->
                motor.alpha = 1f

            "Tricycle" ->
                tricycle.alpha = 1f

            "Van" ->
                van.alpha = 1f

            "Truck" ->
                truck.alpha = 1f
        }

        showMessage(
            "$type selected"
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

        if (
            pickupLatitude == null ||
            pickupLongitude == null
        ) {
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

        if (
            dropLatitude == null ||
            dropLongitude == null
        ) {
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

        val packageIntent =
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
            pickupLatitude!!
        )

        packageIntent.putExtra(
            "pickupLongitude",
            pickupLongitude!!
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
            dropLatitude!!
        )

        packageIntent.putExtra(
            "dropLongitude",
            dropLongitude!!
        )

        packageIntent.putExtra(
            "vehicleType",
            vehicle.vehicleType
        )

        packageIntent.putExtra(
            "customerName",
            tvCustomerName.text.toString()
        )

        startActivity(
            packageIntent
        )
    }

    private fun setupNavigation() {

        val navOrders =
            findViewById<LinearLayout>(
                R.id.navOrders
            )

        val navProfile =
            findViewById<LinearLayout>(
                R.id.navProfile
            )

        navOrders.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    CustomerOrdersActivity::class.java
                )
            )
        }

        navProfile.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    CustomerProfileActivity::class.java
                )
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
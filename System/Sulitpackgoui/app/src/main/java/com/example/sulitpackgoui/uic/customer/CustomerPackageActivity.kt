package com.example.sulitpackgoui.uic.customer

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.logic.Vehicle
import com.google.android.material.button.MaterialButton

class CustomerPackageActivity : ComponentActivity() {

    private var vehicle = Vehicle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.customer_package)

        var descriptionInput =
            findViewById<EditText>(R.id.etDescription)

        var weightInput =
            findViewById<EditText>(R.id.etWeight)

        var piecesInput =
            findViewById<EditText>(R.id.etPieces)

        var instructionsInput =
            findViewById<EditText>(R.id.etInstructions)

        var btnContinue =
            findViewById<MaterialButton>(R.id.btnContinue)

        // Get selected vehicle from Home
        var vehicleType =
            intent.getStringExtra("vehicleType") ?: ""

        vehicle.selectVehicle(vehicleType)

        btnContinue.setOnClickListener {

            var description =
                descriptionInput.text.toString().trim()

            var weight =
                weightInput.text.toString().toDoubleOrNull()

            var pieces =
                piecesInput.text.toString().toIntOrNull()

            var instructions =
                instructionsInput.text.toString().trim()

            // Check package information
            if (description.isBlank()) {
                showMessage("Enter the package description.")
                return@setOnClickListener
            }

            if (weight == null || weight <= 0) {
                showMessage("Enter a valid package weight.")
                return@setOnClickListener
            }

            if (pieces == null || pieces <= 0) {
                showMessage("Enter a valid number of pieces.")
                return@setOnClickListener
            }

            if (!vehicle.checkWeight(weight)) {
                showMessage(
                    "Maximum weight for ${vehicle.vehicleType} is ${vehicle.weightLimit} kg."
                )
                return@setOnClickListener
            }

            // Go to Review
            var reviewIntent =
                Intent(
                    this,
                    CustomerReviewActivity::class.java
                )

            // Copy delivery information from Home
            reviewIntent.putExtras(intent)

            // Add package information
            reviewIntent.putExtra("description", description)
            reviewIntent.putExtra("weight", weight)
            reviewIntent.putExtra("pieces", pieces)
            reviewIntent.putExtra("instructions", instructions)

            startActivity(reviewIntent)
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}
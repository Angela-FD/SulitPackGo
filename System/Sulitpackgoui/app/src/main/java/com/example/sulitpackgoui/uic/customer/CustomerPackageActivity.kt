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

    private var vehicle =
        Vehicle()


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        setContentView(
            R.layout.customer_package
        )


        var descriptionInput =
            findViewById<EditText>(
                R.id.etDescription
            )


        var weightInput =
            findViewById<EditText>(
                R.id.etWeight
            )


        var piecesInput =
            findViewById<EditText>(
                R.id.etPieces
            )


        var instructionsInput =
            findViewById<EditText>(
                R.id.etInstructions
            )


        var btnContinue =
            findViewById<MaterialButton>(
                R.id.btnContinue
            )


        var vehicleType =
            readTextExtra(
                "vehicleType"
            )


        vehicle.selectVehicle(
            vehicleType
        )


        btnContinue.setOnClickListener {

            var description =
                descriptionInput
                    .text
                    .toString()
                    .trim()


            var weight =
                readWeight(
                    weightInput
                        .text
                        .toString()
                )


            var pieces =
                readPieces(
                    piecesInput
                        .text
                        .toString()
                )


            var instructions =
                instructionsInput
                    .text
                    .toString()
                    .trim()


            continueToReview(
                description,
                weight,
                pieces,
                instructions
            )
        }
    }


    private fun continueToReview(
        description: String,
        weight: Double,
        pieces: Int,
        instructions: String
    ) {

        if (description.isBlank()) {

            showMessage(
                "Enter the package description."
            )

            return
        }


        if (weight <= 0) {

            showMessage(
                "Enter a valid package weight."
            )

            return
        }


        if (pieces <= 0) {

            showMessage(
                "Enter a valid number of pieces."
            )

            return
        }


        if (
            vehicle.checkWeight(
                weight
            ) == false
        ) {

            showMessage(
                "Maximum weight for " +
                        vehicle.vehicleType +
                        " is " +
                        vehicle.weightLimit.toString() +
                        " kg."
            )

            return
        }


        var reviewIntent =
            Intent(
                this,
                CustomerReviewActivity::class.java
            )


        copyDeliveryInformation(
            reviewIntent
        )


        reviewIntent.putExtra(
            "description",
            description
        )


        reviewIntent.putExtra(
            "weight",
            weight
        )


        reviewIntent.putExtra(
            "pieces",
            pieces
        )


        reviewIntent.putExtra(
            "instructions",
            instructions
        )


        startActivity(
            reviewIntent
        )
    }


    private fun copyDeliveryInformation(
        reviewIntent: Intent
    ) {

        reviewIntent.putExtra(
            "pickupAddress",
            readTextExtra(
                "pickupAddress"
            )
        )


        reviewIntent.putExtra(
            "pickupAddressDetails",
            readTextExtra(
                "pickupAddressDetails"
            )
        )


        reviewIntent.putExtra(
            "pickupLatitude",
            intent.getDoubleExtra(
                "pickupLatitude",
                0.0
            )
        )


        reviewIntent.putExtra(
            "pickupLongitude",
            intent.getDoubleExtra(
                "pickupLongitude",
                0.0
            )
        )


        reviewIntent.putExtra(
            "dropAddress",
            readTextExtra(
                "dropAddress"
            )
        )


        reviewIntent.putExtra(
            "dropAddressDetails",
            readTextExtra(
                "dropAddressDetails"
            )
        )


        reviewIntent.putExtra(
            "dropLatitude",
            intent.getDoubleExtra(
                "dropLatitude",
                0.0
            )
        )


        reviewIntent.putExtra(
            "dropLongitude",
            intent.getDoubleExtra(
                "dropLongitude",
                0.0
            )
        )


        reviewIntent.putExtra(
            "vehicleType",
            vehicle.vehicleType
        )


        reviewIntent.putExtra(
            "customerName",
            readTextExtra(
                "customerName"
            )
        )
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


    private fun readWeight(
        text: String
    ): Double {

        if (text.isBlank()) {

            return 0.0
        }


        try {

            return text.toDouble()

        } catch (error: Exception) {

            return 0.0
        }
    }


    private fun readPieces(
        text: String
    ): Int {

        if (text.isBlank()) {

            return 0
        }


        try {

            return text.toInt()

        } catch (error: Exception) {

            return 0
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
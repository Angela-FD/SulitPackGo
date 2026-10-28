package com.example.sulitpackgoui.admin

import com.google.firebase.firestore.FirebaseFirestore

class VehicleFeeDatabase {

    private var database = FirebaseFirestore.getInstance()

    fun getVehicleFee(
        vehicleType: String,
        onResult: (Boolean, String, VehicleFee?) -> Unit
    ) {
        if (vehicleType.isBlank()) {
            onResult(
                false,
                "Vehicle type is missing.",
                null
            )
            return
        }

        database.collection("vehicleFees")
            .document(vehicleType)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {
                    onResult(
                        false,
                        "Vehicle fee not found.",
                        null
                    )
                    return@addOnSuccessListener
                }

                var vehicleFee = VehicleFee(
                    vehicleType = document.getString("vehicleType") ?: vehicleType,
                    baseFee = document.getDouble("baseFee") ?: 0.0,
                    ratePerKm = document.getDouble("ratePerKm") ?: 0.0
                )

                onResult(
                    true,
                    "Vehicle fee loaded successfully.",
                    vehicleFee
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to load vehicle fee.",
                    null
                )
            }
    }

    fun updateVehicleFee(
        vehicleType: String,
        baseFee: Double,
        ratePerKm: Double,
        onResult: (Boolean, String) -> Unit
    ) {
        if (vehicleType.isBlank()) {
            onResult(
                false,
                "Vehicle type is missing."
            )
            return
        }

        if (baseFee < 0) {
            onResult(
                false,
                "Base fee cannot be negative."
            )
            return
        }

        if (ratePerKm < 0) {
            onResult(
                false,
                "Rate per KM cannot be negative."
            )
            return
        }

        var vehicleFeeData = hashMapOf(
            "vehicleType" to vehicleType,
            "baseFee" to baseFee,
            "ratePerKm" to ratePerKm
        )

        database.collection("vehicleFees")
            .document(vehicleType)
            .set(vehicleFeeData)
            .addOnSuccessListener {

                onResult(
                    true,
                    "Vehicle fee updated successfully."
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to update vehicle fee."
                )
            }
    }
}
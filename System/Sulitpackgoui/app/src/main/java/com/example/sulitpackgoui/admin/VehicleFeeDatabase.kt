package com.example.sulitpackgoui.admin

import com.google.firebase.firestore.FirebaseFirestore

class VehicleFeeDatabase {

    private var database =
        FirebaseFirestore.getInstance()


    fun getVehicleFee(
        vehicleType: String,
        onResult: (
            Boolean,
            String,
            VehicleFee?
        ) -> Unit
    ) {

        if (vehicleType.isBlank()) {

            onResult(
                false,
                "Vehicle type is missing.",
                null
            )

            return
        }


        database
            .collection("vehicleFees")
            .document(vehicleType)
            .get()
            .addOnSuccessListener { document ->


                if (
                    document.exists() == false
                ) {

                    onResult(
                        false,
                        "Vehicle fee not found.",
                        null
                    )

                } else {

                    var vehicleFee =
                        document.toObject(
                            VehicleFee::class.java
                        )


                    if (vehicleFee == null) {

                        onResult(
                            false,
                            "Failed to read vehicle fee.",
                            null
                        )

                    } else {

                        if (
                            vehicleFee.vehicleType.isBlank()
                        ) {

                            vehicleFee.vehicleType =
                                vehicleType
                        }


                        onResult(
                            true,
                            "Vehicle fee loaded successfully.",
                            vehicleFee
                        )
                    }
                }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    getErrorMessage(
                        error,
                        "Failed to load vehicle fee."
                    ),
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


        var vehicleFee =
            VehicleFee(
                vehicleType = vehicleType,
                baseFee = baseFee,
                ratePerKm = ratePerKm
            )


        database
            .collection("vehicleFees")
            .document(vehicleType)
            .set(
                vehicleFee
            )
            .addOnSuccessListener {

                onResult(
                    true,
                    "Vehicle fee updated successfully."
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    getErrorMessage(
                        error,
                        "Failed to update vehicle fee."
                    )
                )
            }
    }


    private fun getErrorMessage(
        error: Exception,
        defaultMessage: String
    ): String {

        if (error.message != null) {

            return error.message.toString()
        }


        return defaultMessage
    }
}
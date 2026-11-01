package com.example.sulitpackgoui.rider

import com.google.firebase.firestore.FirebaseFirestore

class RiderDatabase {

    private var database = FirebaseFirestore.getInstance()


    // Save Rider information to Firestore
    fun saveRider(
        rider: Rider,
        onResult: (Boolean, String) -> Unit
    ) {

        var riderData = hashMapOf(
            "uid" to rider.uid,
            "name" to rider.name,
            "email" to rider.email,
            "phoneNumber" to rider.phoneNumber,
            "vehicleType" to rider.vehicleType,
            "isAvailable" to rider.isAvailable,
            "currentOrderId" to rider.currentOrderId,
            "role" to "rider"
        )


        database
            .collection("riders")
            .document(rider.uid)
            .set(riderData)
            .addOnSuccessListener {

                onResult(
                    true,
                    "Rider saved successfully."
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to save rider."
                )
            }
    }


    // Get Rider information from Firestore
    fun getRider(
        uid: String,
        onResult: (Boolean, String, Rider?) -> Unit
    ) {

        if (uid.isBlank()) {

            onResult(
                false,
                "Invalid rider ID.",
                null
            )

            return
        }


        database
            .collection("riders")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->


                if (document.exists()) {

                    var rider = Rider(

                        uid =
                            document.getString("uid") ?: "",

                        name =
                            document.getString("name") ?: "",

                        email =
                            document.getString("email") ?: "",

                        phoneNumber =
                            document.getString("phoneNumber") ?: "",

                        vehicleType =
                            document.getString("vehicleType") ?: "",

                        isAvailable =
                            document.getBoolean("isAvailable") ?: true,

                        currentOrderId =
                            document.getString("currentOrderId") ?: ""
                    )


                    onResult(
                        true,
                        "Rider found.",
                        rider
                    )

                } else {

                    onResult(
                        false,
                        "Rider profile not found.",
                        null
                    )
                }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to get rider.",
                    null
                )
            }
    }


    // Change Rider availability
    //
    // true  = Rider can accept orders
    // false = Rider cannot accept orders
    fun updateAvailability(
        rider: Rider,
        isAvailable: Boolean,
        onResult: (Boolean, String) -> Unit
    ) {

        if (rider.uid.isBlank()) {

            onResult(
                false,
                "Rider is not logged in."
            )

            return
        }


        // Rider cannot become available
        // while an order is still active
        if (
            isAvailable == true &&
            rider.currentOrderId.isNotBlank()
        ) {

            onResult(
                false,
                "Finish your current delivery first."
            )

            return
        }


        database
            .collection("riders")
            .document(rider.uid)
            .update(
                "isAvailable",
                isAvailable
            )
            .addOnSuccessListener {


                // Update the local Rider object too
                rider.isAvailable = isAvailable


                if (isAvailable) {

                    onResult(
                        true,
                        "You are now available."
                    )

                } else {

                    onResult(
                        true,
                        "You are now unavailable."
                    )
                }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to update availability."
                )
            }
    }
}
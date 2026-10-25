package com.example.sulitpackgoui.rider

import com.google.firebase.firestore.FirebaseFirestore

class RiderDatabase {

    private var database = FirebaseFirestore.getInstance()


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

    fun getRider(
        uid: String,
        onResult: (Boolean, String, Rider?) -> Unit
    ) {

        database
            .collection("riders")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    var rider = Rider(
                        uid = document.getString("uid") ?: "",
                        name = document.getString("name") ?: "",
                        email = document.getString("email") ?: "",
                        phoneNumber = document.getString("phoneNumber") ?: "",
                        vehicleType = document.getString("vehicleType") ?: "",
                        isAvailable = document.getBoolean("isAvailable") ?: true,
                        currentOrderId = document.getString("currentOrderId") ?: ""
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
}
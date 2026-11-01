package com.example.sulitpackgoui.rider

import com.google.firebase.firestore.FirebaseFirestore

class RiderDatabase {

    private var database =
        FirebaseFirestore.getInstance()


    fun saveRider(
        rider: Rider,
        onResult: (Boolean, String) -> Unit
    ) {

        database
            .collection("riders")
            .document(rider.uid)
            .set(rider)
            .addOnSuccessListener {

                onResult(
                    true,
                    "Rider saved successfully."
                )
            }
            .addOnFailureListener { error ->

                var message =
                    "Failed to save rider."


                if (error.message != null) {

                    message =
                        error.message.toString()
                }


                onResult(
                    false,
                    message
                )
            }
    }


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

                var rider =
                    document.toObject(
                        Rider::class.java
                    )


                if (rider == null) {

                    onResult(
                        false,
                        "Rider profile not found.",
                        null
                    )

                } else {

                    onResult(
                        true,
                        "Rider found.",
                        rider
                    )
                }
            }
            .addOnFailureListener { error ->

                var message =
                    "Failed to get rider."


                if (error.message != null) {

                    message =
                        error.message.toString()
                }


                onResult(
                    false,
                    message,
                    null
                )
            }
    }


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


        if (isAvailable) {

            if (rider.currentOrderId.isNotBlank()) {

                onResult(
                    false,
                    "Finish your current delivery first."
                )

                return
            }
        }


        database
            .collection("riders")
            .document(rider.uid)
            .update(
                "isAvailable",
                isAvailable
            )
            .addOnSuccessListener {

                rider.isAvailable =
                    isAvailable


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

                var message =
                    "Failed to update availability."


                if (error.message != null) {

                    message =
                        error.message.toString()
                }


                onResult(
                    false,
                    message
                )
            }
    }
}
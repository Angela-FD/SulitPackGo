package com.example.sulitpackgoui.rider

import com.google.firebase.auth.FirebaseAuth

class RiderAuth {

    private var auth = FirebaseAuth.getInstance()
    private var riderDatabase = RiderDatabase()


    fun register(
        riderName: String,
        riderEmail: String,
        riderPassword: String,
        confirmPassword: String,
        riderPhoneNumber: String,
        selectedVehicle: String,
        onResult: (Boolean, String) -> Unit
    ) {

        if (riderName.isBlank()) {
            onResult(false, "Please enter rider name.")
            return
        }

        if (riderEmail.isBlank()) {
            onResult(false, "Please enter rider email.")
            return
        }

        if (riderPassword.length < 8) {
            onResult(false, "Password should have at least 8 characters.")
            return
        }

        if (riderPassword != confirmPassword) {
            onResult(false, "Passwords do not match.")
            return
        }

        if (riderPhoneNumber.isBlank()) {
            onResult(false, "Please enter phone number.")
            return
        }

        if (
            selectedVehicle != "Motor" &&
            selectedVehicle != "Tricycle" &&
            selectedVehicle != "Van" &&
            selectedVehicle != "Truck"
        ) {
            onResult(false, "Please select a valid vehicle.")
            return
        }


        auth.createUserWithEmailAndPassword(
            riderEmail,
            riderPassword
        )
            .addOnSuccessListener {

                var firebaseRider = auth.currentUser

                if (firebaseRider == null) {
                    onResult(false, "Rider account creation failed.")
                    return@addOnSuccessListener
                }


                var rider = Rider(
                    uid = firebaseRider.uid,
                    name = riderName,
                    email = riderEmail,
                    phoneNumber = riderPhoneNumber,
                    vehicleType = selectedVehicle,
                    isAvailable = true,
                    currentOrderId = ""
                )


                riderDatabase.saveRider(
                    rider
                ) { success, message ->

                    onResult(
                        success,
                        message
                    )
                }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Rider registration failed."
                )
            }
    }

    fun logIn(
        riderEmail: String,
        riderPassword: String,
        onResult: (Boolean, String, Rider?) -> Unit
    ) {

        if (riderEmail.isBlank()) {
            onResult(
                false,
                "Please enter rider email.",
                null
            )
            return
        }

        if (riderPassword.isBlank()) {
            onResult(
                false,
                "Please enter rider password.",
                null
            )
            return
        }


        auth.signInWithEmailAndPassword(
            riderEmail,
            riderPassword
        )
            .addOnSuccessListener {

                var firebaseRider = auth.currentUser

                if (firebaseRider == null) {
                    onResult(
                        false,
                        "Rider login failed.",
                        null
                    )
                    return@addOnSuccessListener
                }


                riderDatabase.getRider(
                    firebaseRider.uid
                ) { success, message, rider ->

                    onResult(
                        success,
                        message,
                        rider
                    )
                }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Rider login failed.",
                    null
                )
            }
    }


    fun logOut() {
        auth.signOut()
    }
}
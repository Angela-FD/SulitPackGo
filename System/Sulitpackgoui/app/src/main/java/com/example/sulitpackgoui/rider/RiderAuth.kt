package com.example.sulitpackgoui.rider

import com.google.firebase.auth.FirebaseAuth

class RiderAuth {

    private var firebaseAuth = FirebaseAuth.getInstance()

    private var riderDatabase = RiderDatabase()


    // Register a new Rider account
    fun register(
        name: String,
        email: String,
        password: String,
        phoneNumber: String,
        vehicleType: String,
        onResult: (Boolean, String) -> Unit
    ) {

        if (name.isBlank()) {

            onResult(
                false,
                "Please enter your name."
            )

            return
        }


        if (email.isBlank()) {

            onResult(
                false,
                "Please enter your email."
            )

            return
        }


        if (password.isBlank()) {

            onResult(
                false,
                "Please enter your password."
            )

            return
        }


        if (password.length < 8) {

            onResult(
                false,
                "Password must be at least 8 characters."
            )

            return
        }


        if (phoneNumber.isBlank()) {

            onResult(
                false,
                "Please enter your phone number."
            )

            return
        }


        if (vehicleType.isBlank()) {

            onResult(
                false,
                "Please select a vehicle."
            )

            return
        }


        firebaseAuth
            .createUserWithEmailAndPassword(
                email,
                password
            )
            .addOnSuccessListener {


                var firebaseUser =
                    firebaseAuth.currentUser


                if (firebaseUser == null) {

                    onResult(
                        false,
                        "Failed to create Rider account."
                    )

                    return@addOnSuccessListener
                }


                var rider = Rider(

                    uid = firebaseUser.uid,

                    name = name,

                    email = email,

                    phoneNumber = phoneNumber,

                    vehicleType = vehicleType,

                    isAvailable = true,

                    currentOrderId = ""
                )


                riderDatabase.saveRider(
                    rider
                ) { success, message ->


                    if (success) {

                        onResult(
                            true,
                            "Rider account created successfully."
                        )

                    } else {

                        onResult(
                            false,
                            message
                        )
                    }
                }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message
                        ?: "Failed to register Rider."
                )
            }
    }


    // Login Rider
    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String, Rider?) -> Unit
    ) {

        if (email.isBlank()) {

            onResult(
                false,
                "Please enter your email.",
                null
            )

            return
        }


        if (password.isBlank()) {

            onResult(
                false,
                "Please enter your password.",
                null
            )

            return
        }


        firebaseAuth
            .signInWithEmailAndPassword(
                email,
                password
            )
            .addOnSuccessListener {


                var firebaseUser =
                    firebaseAuth.currentUser


                if (firebaseUser == null) {

                    onResult(
                        false,
                        "Login failed.",
                        null
                    )

                    return@addOnSuccessListener
                }


                riderDatabase.getRider(
                    firebaseUser.uid
                ) { success, message, rider ->


                    if (success && rider != null) {

                        onResult(
                            true,
                            "Login successful.",
                            rider
                        )

                    } else {


                        // If the Firebase account exists
                        // but no Rider profile exists,
                        // do not allow Rider login.

                        firebaseAuth.signOut()


                        onResult(
                            false,
                            "This account is not registered as a Rider.",
                            null
                        )
                    }
                }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message
                        ?: "Login failed.",
                    null
                )
            }
    }


    // Logout Rider
    fun logout() {

        firebaseAuth.signOut()
    }


    // Get current logged-in Firebase Rider UID
    fun getCurrentRiderUid(): String {

        var currentUser =
            firebaseAuth.currentUser


        if (currentUser == null) {

            return ""
        }


        return currentUser.uid
    }
}
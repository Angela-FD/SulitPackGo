package com.example.sulitpackgoui.customer

import com.google.firebase.auth.FirebaseAuth

class CustomerAuth {

    private var auth =
        FirebaseAuth.getInstance()


    private var customerDatabase =
        CustomerDatabase()


    fun register(
        customerName: String,
        customerEmail: String,
        customerPhoneNumber: String,
        customerPassword: String,
        confirmPassword: String,
        onResult: (Boolean, String) -> Unit
    ) {

        if (customerName.isBlank()) {

            onResult(
                false,
                "Please enter your name."
            )

            return
        }


        if (customerEmail.isBlank()) {

            onResult(
                false,
                "Please enter your email."
            )

            return
        }


        if (customerPhoneNumber.isBlank()) {

            onResult(
                false,
                "Please enter your phone number."
            )

            return
        }


        if (customerPhoneNumber.length != 11) {

            onResult(
                false,
                "Phone number must be 11 digits."
            )

            return
        }


        if (
            customerPhoneNumber.startsWith("09") == false
        ) {

            onResult(
                false,
                "Phone number must start with 09."
            )

            return
        }


        for (character in customerPhoneNumber) {

            if (
                character.isDigit() == false
            ) {

                onResult(
                    false,
                    "Phone number must contain numbers only."
                )

                return
            }
        }


        if (customerPassword.length < 8) {

            onResult(
                false,
                "Password must be at least 8 characters."
            )

            return
        }


        if (
            customerPassword !=
            confirmPassword
        ) {

            onResult(
                false,
                "Passwords do not match."
            )

            return
        }


        auth
            .createUserWithEmailAndPassword(
                customerEmail,
                customerPassword
            )
            .addOnSuccessListener {

                var firebaseCustomer =
                    auth.currentUser


                if (firebaseCustomer == null) {

                    onResult(
                        false,
                        "Customer registration failed."
                    )

                } else {

                    var customer =
                        Customer(
                            uid = firebaseCustomer.uid,
                            name = customerName,
                            email = customerEmail,
                            phoneNumber = customerPhoneNumber,
                            role = "customer"
                        )


                    customerDatabase.saveCustomer(
                        customer,
                        onResult
                    )
                }
            }
            .addOnFailureListener { error ->

                var message =
                    "Customer registration failed."


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
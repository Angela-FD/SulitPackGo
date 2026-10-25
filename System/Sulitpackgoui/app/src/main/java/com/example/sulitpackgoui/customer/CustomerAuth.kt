package com.example.sulitpackgoui.customer

import com.google.firebase.auth.FirebaseAuth

class CustomerAuth {

    private var auth = FirebaseAuth.getInstance()
    private var customerDatabase = CustomerDatabase()


    fun register(
        customerName: String,
        customerEmail: String,
        customerPassword: String,
        confirmPassword: String,
        onResult: (Boolean, String) -> Unit
    ) {

        if (customerName.isBlank()) {

            onResult(
                false,
                "Please enter customer name."
            )

            return
        }


        if (customerEmail.isBlank()) {

            onResult(
                false,
                "Please enter customer email."
            )

            return
        }


        if (customerPassword.length < 8) {

            onResult(
                false,
                "Password should have at least 8 characters."
            )

            return
        }


        if (customerPassword != confirmPassword) {

            onResult(
                false,
                "Passwords do not match."
            )

            return
        }


        auth.createUserWithEmailAndPassword(
            customerEmail,
            customerPassword
        )
            .addOnSuccessListener {


                var firebaseCustomer =
                    auth.currentUser


                if (firebaseCustomer == null) {

                    onResult(
                        false,
                        "Customer account creation failed."
                    )

                    return@addOnSuccessListener
                }


                var customer = Customer(
                    uid = firebaseCustomer.uid,
                    name = customerName,
                    email = customerEmail,
                    role = "customer"
                )


                customerDatabase.saveCustomer(
                    customer
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
                    error.message ?: "Customer registration failed."
                )
            }
    }


    fun logIn(
        customerEmail: String,
        customerPassword: String,
        onResult: (Boolean, String, Customer?) -> Unit
    ) {

        if (customerEmail.isBlank()) {

            onResult(
                false,
                "Please enter customer email.",
                null
            )

            return
        }


        if (customerPassword.isBlank()) {

            onResult(
                false,
                "Please enter customer password.",
                null
            )

            return
        }


        auth.signInWithEmailAndPassword(
            customerEmail,
            customerPassword
        )
            .addOnSuccessListener {


                var firebaseCustomer =
                    auth.currentUser


                if (firebaseCustomer == null) {

                    onResult(
                        false,
                        "Customer login failed.",
                        null
                    )

                    return@addOnSuccessListener
                }


                customerDatabase.getCustomer(
                    firebaseCustomer.uid
                ) { success, message, customer ->


                    onResult(
                        success,
                        message,
                        customer
                    )
                }
            }
            .addOnFailureListener { error ->


                onResult(
                    false,
                    error.message ?: "Customer login failed.",
                    null
                )
            }
    }


    fun logOut() {

        auth.signOut()
    }
}
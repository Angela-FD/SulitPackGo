package com.example.sulitpackgoui.customer

import com.google.firebase.firestore.FirebaseFirestore

class CustomerDatabase {

    private var database =
        FirebaseFirestore.getInstance()


    fun saveCustomer(
        customer: Customer,
        onResult: (Boolean, String) -> Unit
    ) {

        database
            .collection("users")
            .document(customer.uid)
            .set(customer)
            .addOnSuccessListener {

                onResult(
                    true,
                    "Customer saved successfully."
                )
            }
            .addOnFailureListener { error ->

                var message =
                    "Failed to save customer."


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


    fun getCustomer(
        uid: String,
        onResult: (
            Boolean,
            String,
            Customer?
        ) -> Unit
    ) {

        if (uid.isBlank()) {

            onResult(
                false,
                "Customer ID is missing.",
                null
            )

            return
        }


        database
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                var customer =
                    document.toObject(
                        Customer::class.java
                    )


                if (customer == null) {

                    onResult(
                        false,
                        "Customer profile not found.",
                        null
                    )

                } else {

                    onResult(
                        true,
                        "Customer found.",
                        customer
                    )
                }
            }
            .addOnFailureListener { error ->

                var message =
                    "Failed to get customer."


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
}
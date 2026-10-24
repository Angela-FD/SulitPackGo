package com.example.sulitpackgoui.customer

import com.google.firebase.firestore.FirebaseFirestore

class CustomerDatabase {

    private var database = FirebaseFirestore.getInstance()


    fun saveCustomer(
        customer: Customer,
        onResult: (Boolean, String) -> Unit
    ) {

        var customerData = hashMapOf(
            "uid" to customer.uid,
            "name" to customer.name,
            "email" to customer.email,
            "role" to customer.role
        )


        database
            .collection("users")
            .document(customer.uid)
            .set(customerData)
            .addOnSuccessListener {

                onResult(
                    true,
                    "Customer saved successfully."
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to save customer."
                )
            }
    }

    fun getCustomer(
        uid: String,
        onResult: (Boolean, String, Customer?) -> Unit
    ) {

        database
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    var customer = Customer(
                        uid = document.getString("uid") ?: "",
                        name = document.getString("name") ?: "",
                        email = document.getString("email") ?: "",
                        role = document.getString("role") ?: "customer"
                    )

                    onResult(
                        true,
                        "Customer found.",
                        customer
                    )

                } else {

                    onResult(
                        false,
                        "Customer profile not found.",
                        null
                    )
                }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to get customer.",
                    null
                )
            }
    }
}
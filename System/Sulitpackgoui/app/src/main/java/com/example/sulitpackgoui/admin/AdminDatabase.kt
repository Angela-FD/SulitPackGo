package com.example.sulitpackgoui.admin

import com.example.sulitpackgoui.customer.Customer
import com.example.sulitpackgoui.order.OrderData
import com.example.sulitpackgoui.rider.Rider
import com.google.firebase.firestore.FirebaseFirestore

class AdminDatabase {
    private var database =
        FirebaseFirestore.getInstance()

    fun getCustomers(
        onResult: (
            Boolean,
            String,
            List<Customer>
        ) -> Unit
    ) {

        database
            .collection("users")
            .whereEqualTo(
                "role",
                "customer"
            )
            .get()
            .addOnSuccessListener { result ->

                var customers =
                    mutableListOf<Customer>()

                for (
                document in
                result.documents
                ) {
                    var customer =
                        document.toObject(
                            Customer::class.java
                        )

                    if (customer != null) {

                        customers.add(
                            customer
                        )
                    }
                }

                onResult(
                    true,
                    "Customers loaded successfully.",
                    customers
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    getErrorMessage(
                        error,
                        "Failed to load customers."
                    ),
                    emptyList()
                )
            }
    }

    fun deleteCustomer(
        customerId: String,
        onResult: (Boolean, String) -> Unit
    ) {

        if (customerId.isBlank()) {

            onResult(
                false,
                "Customer ID is missing."
            )

            return
        }

        database
            .collection("users")
            .document(customerId)
            .delete()
            .addOnSuccessListener {

                onResult(
                    true,
                    "Customer profile deleted successfully."
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    getErrorMessage(
                        error,
                        "Failed to delete customer."
                    )
                )
            }
    }

    fun getRiders(
        onResult: (
            Boolean,
            String,
            List<Rider>
        ) -> Unit
    ) {
        database
            .collection("riders")
            .get()
            .addOnSuccessListener { result ->

                var riders =
                    mutableListOf<Rider>()

                for (
                document in
                result.documents
                ) {
                    var rider =
                        document.toObject(
                            Rider::class.java
                        )

                    if (rider != null) {

                        riders.add(
                            rider
                        )
                    }
                }

                onResult(
                    true,
                    "Riders loaded successfully.",
                    riders
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    getErrorMessage(
                        error,
                        "Failed to load riders."
                    ),
                    emptyList()
                )
            }
    }

    fun deleteRider(
        riderId: String,
        onResult: (Boolean, String) -> Unit
    ) {

        if (riderId.isBlank()) {

            onResult(
                false,
                "Rider ID is missing."
            )

            return
        }

        var riderReference =
            database
                .collection("riders")
                .document(riderId)

        riderReference
            .get()
            .addOnSuccessListener { document ->

                if (
                    document.exists() == false
                ) {

                    onResult(
                        false,
                        "Rider not found."
                    )

                } else {

                    var rider =
                        document.toObject(
                            Rider::class.java
                        )

                    if (rider == null) {

                        onResult(
                            false,
                            "Failed to read Rider."
                        )

                    } else if (
                        rider.currentOrderId.isNotBlank()
                    ) {

                        onResult(
                            false,
                            "Cannot delete rider with an active order."
                        )

                    } else {

                        riderReference
                            .delete()
                            .addOnSuccessListener {

                                onResult(
                                    true,
                                    "Rider profile deleted successfully."
                                )
                            }
                            .addOnFailureListener { error ->

                                onResult(
                                    false,
                                    getErrorMessage(
                                        error,
                                        "Failed to delete rider."
                                    )
                                )
                            }
                    }
                }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    getErrorMessage(
                        error,
                        "Failed to check rider."
                    )
                )
            }
    }

    fun getOrders(
        onResult: (
            Boolean,
            String,
            List<OrderData>
        ) -> Unit
    ) {
        database
            .collection("orders")
            .get()
            .addOnSuccessListener { result ->

                var orders =
                    mutableListOf<OrderData>()

                for (
                document in
                result.documents
                ) {

                    var order =
                        document.toObject(
                            OrderData::class.java
                        )

                    if (order != null) {

                        orders.add(
                            order
                        )
                    }
                }

                orders.sortByDescending { order ->
                    order.createdAt
                }

                onResult(
                    true,
                    "Orders loaded successfully.",
                    orders
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    getErrorMessage(
                        error,
                        "Failed to load orders."
                    ),
                    emptyList()
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
package com.example.sulitpackgoui.admin

import com.example.sulitpackgoui.customer.Customer
import com.example.sulitpackgoui.rider.Rider
import com.example.sulitpackgoui.order.OrderData
import com.google.firebase.firestore.FirebaseFirestore

class AdminDatabase {

    private var database = FirebaseFirestore.getInstance()

    fun getCustomers(
        onResult: (Boolean, String, List<Customer>) -> Unit
    ) {
        database.collection("users")
            .whereEqualTo("role", "customer")
            .get()
            .addOnSuccessListener { result ->

                var customers = mutableListOf<Customer>()

                for (document in result.documents) {

                    var customer = Customer(
                        uid = document.getString("uid") ?: "",
                        name = document.getString("name") ?: "",
                        email = document.getString("email") ?: "",
                        role = document.getString("role") ?: "customer"
                    )

                    customers.add(customer)
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
                    error.message ?: "Failed to load customers.",
                    emptyList()
                )
            }
    }

    fun deleteCustomer(
        customerId: String,
        onResult: (Boolean, String) -> Unit
    ) {
        if (customerId.isBlank()) {
            onResult(false, "Customer ID is missing.")
            return
        }

        database.collection("users")
            .document(customerId)
            .delete()
            .addOnSuccessListener {

                onResult(
                    true,
                    "Customer deleted successfully."
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to delete customer."
                )
            }
    }

    fun getRiders(
        onResult: (Boolean, String, List<Rider>) -> Unit
    ) {
        database.collection("users")
            .whereEqualTo("role", "rider")
            .get()
            .addOnSuccessListener { result ->

                var riders = mutableListOf<Rider>()

                for (document in result.documents) {

                    var rider = Rider(
                        uid = document.getString("uid") ?: "",
                        name = document.getString("name") ?: "",
                        email = document.getString("email") ?: "",
                        phoneNumber = document.getString("phoneNumber") ?: "",
                        vehicleType = document.getString("vehicleType") ?: "",
                        isAvailable = document.getBoolean("isAvailable") ?: true,
                        currentOrderId = document.getString("currentOrderId") ?: ""
                    )

                    riders.add(rider)
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
                    error.message ?: "Failed to load riders.",
                    emptyList()
                )
            }
    }

    fun deleteRider(
        riderId: String,
        onResult: (Boolean, String) -> Unit
    ) {
        if (riderId.isBlank()) {
            onResult(false, "Rider ID is missing.")
            return
        }

        database.collection("users")
            .document(riderId)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {
                    onResult(false, "Rider not found.")
                    return@addOnSuccessListener
                }

                var currentOrderId =
                    document.getString("currentOrderId") ?: ""

                if (currentOrderId.isNotBlank()) {
                    onResult(
                        false,
                        "Cannot delete rider with an active order."
                    )
                    return@addOnSuccessListener
                }

                database.collection("users")
                    .document(riderId)
                    .delete()
                    .addOnSuccessListener {

                        onResult(
                            true,
                            "Rider deleted successfully."
                        )
                    }
                    .addOnFailureListener { error ->

                        onResult(
                            false,
                            error.message ?: "Failed to delete rider."
                        )
                    }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to check rider."
                )
            }
    }

    fun getOrders(
        onResult: (Boolean, String, List<OrderData>) -> Unit
    ) {
        database.collection("orders")
            .get()
            .addOnSuccessListener { result ->

                var orders = mutableListOf<OrderData>()

                for (document in result.documents) {

                    var order = OrderData(
                        orderId = document.getString("orderId") ?: "",
                        customerId = document.getString("customerId") ?: "",
                        riderId = document.getString("riderId") ?: "",

                        pickupAddress = document.getString("pickupAddress") ?: "",
                        pickupAddressDetails = document.getString("pickupAddressDetails") ?: "",

                        dropAddress = document.getString("dropAddress") ?: "",
                        dropAddressDetails = document.getString("dropAddressDetails") ?: "",

                        pickupLatitude = document.getDouble("pickupLatitude") ?: 0.0,
                        pickupLongitude = document.getDouble("pickupLongitude") ?: 0.0,
                        dropLatitude = document.getDouble("dropLatitude") ?: 0.0,
                        dropLongitude = document.getDouble("dropLongitude") ?: 0.0,

                        description = document.getString("description") ?: "",
                        weight = document.getDouble("weight") ?: 0.0,
                        pieces = document.getLong("pieces")?.toInt() ?: 0,
                        instructions = document.getString("instructions") ?: "",

                        vehicleType = document.getString("vehicleType") ?: "",
                        distance = document.getDouble("distance") ?: 0.0,
                        fee = document.getDouble("fee") ?: 0.0,

                        status = document.getString("status") ?: "",
                        createdAt = document.getLong("createdAt") ?: 0L
                    )

                    orders.add(order)
                }

                orders.sortByDescending {
                    it.createdAt
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
                    error.message ?: "Failed to load orders.",
                    emptyList()
                )
            }
    }
}
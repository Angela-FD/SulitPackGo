package com.example.sulitpackgoui.rider

import com.example.sulitpackgoui.order.OrderData
import com.google.firebase.firestore.FirebaseFirestore

class RiderOrder {

    private var database = FirebaseFirestore.getInstance()


    fun getPendingOrders(
        rider: Rider,
        onResult: (Boolean, String, List<OrderData>) -> Unit
    ) {

        database
            .collection("orders")
            .whereEqualTo("status", "Pending")
            .get()
            .addOnSuccessListener { documents ->

                var pendingOrders =
                    mutableListOf<OrderData>()


                for (document in documents) {

                    var order = OrderData(

                        orderId =
                            document.getString("orderId") ?: "",

                        customerId =
                            document.getString("customerId") ?: "",

                        riderId =
                            document.getString("riderId") ?: "",


                        pickupAddress =
                            document.getString("pickupAddress") ?: "",

                        pickupAddressDetails =
                            document.getString("pickupAddressDetails") ?: "",

                        dropAddress =
                            document.getString("dropAddress") ?: "",

                        dropAddressDetails =
                            document.getString("dropAddressDetails") ?: "",


                        pickupLatitude =
                            document.getDouble("pickupLatitude") ?: 0.0,

                        pickupLongitude =
                            document.getDouble("pickupLongitude") ?: 0.0,

                        dropLatitude =
                            document.getDouble("dropLatitude") ?: 0.0,

                        dropLongitude =
                            document.getDouble("dropLongitude") ?: 0.0,


                        description =
                            document.getString("description") ?: "",

                        weight =
                            document.getDouble("weight") ?: 0.0,

                        pieces =
                            document.getLong("pieces")?.toInt() ?: 1,

                        instructions =
                            document.getString("instructions") ?: "",


                        vehicleType =
                            document.getString("vehicleType") ?: "",


                        distance =
                            document.getDouble("distance") ?: 0.0,

                        fee =
                            document.getDouble("fee") ?: 0.0,


                        status =
                            document.getString("status") ?: "",

                        createdAt =
                            document.getLong("createdAt") ?: 0L
                    )


                    if (order.vehicleType == rider.vehicleType) {

                        pendingOrders.add(order)
                    }
                }


                pendingOrders.sortByDescending {
                    it.createdAt
                }


                onResult(
                    true,
                    "Pending orders found.",
                    pendingOrders
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to get pending orders.",
                    emptyList()
                )
            }
    }


    fun acceptOrder(
        rider: Rider,
        orderId: String,
        onResult: (Boolean, String) -> Unit
    ) {

        if (rider.uid.isBlank()) {

            onResult(
                false,
                "Rider is not logged in."
            )

            return
        }


        if (rider.isAvailable == false) {

            onResult(
                false,
                "Rider already has an active order."
            )

            return
        }


        if (orderId.isBlank()) {

            onResult(
                false,
                "Invalid order."
            )

            return
        }


        var orderReference =
            database
                .collection("orders")
                .document(orderId)


        var riderReference =
            database
                .collection("riders")
                .document(rider.uid)


        orderReference
            .get()
            .addOnSuccessListener { document ->


                if (document.exists() == false) {

                    onResult(
                        false,
                        "Order not found."
                    )

                    return@addOnSuccessListener
                }


                var orderStatus =
                    document.getString("status") ?: ""


                if (orderStatus != "Pending") {

                    onResult(
                        false,
                        "Order is no longer available."
                    )

                    return@addOnSuccessListener
                }


                var requiredVehicle =
                    document.getString("vehicleType") ?: ""


                if (requiredVehicle != rider.vehicleType) {

                    onResult(
                        false,
                        "This order requires a $requiredVehicle."
                    )

                    return@addOnSuccessListener
                }


                var orderUpdate = hashMapOf<String, Any>(
                    "riderId" to rider.uid,
                    "status" to "Pick Up Pending"
                )


                orderReference
                    .update(orderUpdate)
                    .addOnSuccessListener {


                        var riderUpdate = hashMapOf<String, Any>(
                            "isAvailable" to false,
                            "currentOrderId" to orderId
                        )


                        riderReference
                            .update(riderUpdate)
                            .addOnSuccessListener {


                                rider.isAvailable = false
                                rider.currentOrderId = orderId


                                onResult(
                                    true,
                                    "Order accepted successfully."
                                )
                            }
                            .addOnFailureListener { error ->

                                onResult(
                                    false,
                                    error.message
                                        ?: "Failed to update rider."
                                )
                            }
                    }
                    .addOnFailureListener { error ->

                        onResult(
                            false,
                            error.message
                                ?: "Failed to accept order."
                        )
                    }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to get order."
                )
            }
    }


    fun updateDeliveryStatus(
        rider: Rider,
        onResult: (Boolean, String) -> Unit
    ) {

        if (rider.uid.isBlank()) {

            onResult(
                false,
                "Rider is not logged in."
            )

            return
        }


        if (rider.currentOrderId.isBlank()) {

            onResult(
                false,
                "Rider has no active order."
            )

            return
        }


        var orderReference =
            database
                .collection("orders")
                .document(rider.currentOrderId)


        orderReference
            .get()
            .addOnSuccessListener { document ->


                if (document.exists() == false) {

                    onResult(
                        false,
                        "Order not found."
                    )

                    return@addOnSuccessListener
                }


                var assignedRiderId =
                    document.getString("riderId") ?: ""


                if (assignedRiderId != rider.uid) {

                    onResult(
                        false,
                        "This order does not belong to this rider."
                    )

                    return@addOnSuccessListener
                }


                var currentStatus =
                    document.getString("status") ?: ""


                var newStatus = ""


                if (currentStatus == "Pick Up Pending") {

                    newStatus = "Out for Delivery"

                } else if (currentStatus == "Out for Delivery") {

                    newStatus = "Delivered"

                } else {

                    onResult(
                        false,
                        "No next delivery status."
                    )

                    return@addOnSuccessListener
                }


                orderReference
                    .update(
                        "status",
                        newStatus
                    )
                    .addOnSuccessListener {


                        if (newStatus == "Delivered") {


                            var riderReference =
                                database
                                    .collection("riders")
                                    .document(rider.uid)


                            var riderUpdate = hashMapOf<String, Any>(
                                "isAvailable" to true,
                                "currentOrderId" to ""
                            )


                            riderReference
                                .update(riderUpdate)
                                .addOnSuccessListener {


                                    rider.isAvailable = true
                                    rider.currentOrderId = ""


                                    onResult(
                                        true,
                                        "Order delivered successfully."
                                    )
                                }
                                .addOnFailureListener { error ->

                                    onResult(
                                        false,
                                        error.message
                                            ?: "Failed to update rider."
                                    )
                                }

                        } else {

                            onResult(
                                true,
                                "Order status updated to $newStatus."
                            )
                        }
                    }
                    .addOnFailureListener { error ->

                        onResult(
                            false,
                            error.message
                                ?: "Failed to update order status."
                        )
                    }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to get order."
                )
            }
    }


    fun getCurrentOrder(
        rider: Rider,
        onResult: (Boolean, String, OrderData?) -> Unit
    ) {

        if (rider.currentOrderId.isBlank()) {

            onResult(
                false,
                "Rider has no active order.",
                null
            )

            return
        }


        database
            .collection("orders")
            .document(rider.currentOrderId)
            .get()
            .addOnSuccessListener { document ->


                if (document.exists() == false) {

                    onResult(
                        false,
                        "Order not found.",
                        null
                    )

                    return@addOnSuccessListener
                }


                var order = OrderData(

                    orderId =
                        document.getString("orderId") ?: "",

                    customerId =
                        document.getString("customerId") ?: "",

                    riderId =
                        document.getString("riderId") ?: "",


                    pickupAddress =
                        document.getString("pickupAddress") ?: "",

                    pickupAddressDetails =
                        document.getString("pickupAddressDetails") ?: "",

                    dropAddress =
                        document.getString("dropAddress") ?: "",

                    dropAddressDetails =
                        document.getString("dropAddressDetails") ?: "",


                    pickupLatitude =
                        document.getDouble("pickupLatitude") ?: 0.0,

                    pickupLongitude =
                        document.getDouble("pickupLongitude") ?: 0.0,

                    dropLatitude =
                        document.getDouble("dropLatitude") ?: 0.0,

                    dropLongitude =
                        document.getDouble("dropLongitude") ?: 0.0,


                    description =
                        document.getString("description") ?: "",


                    weight =
                        document.getDouble("weight") ?: 0.0,

                    pieces =
                        document.getLong("pieces")?.toInt() ?: 1,

                    instructions =
                        document.getString("instructions") ?: "",


                    vehicleType =
                        document.getString("vehicleType") ?: "",


                    distance =
                        document.getDouble("distance") ?: 0.0,

                    fee =
                        document.getDouble("fee") ?: 0.0,


                    status =
                        document.getString("status") ?: "",

                    createdAt =
                        document.getLong("createdAt") ?: 0L
                )


                if (order.riderId != rider.uid) {

                    onResult(
                        false,
                        "This order is not assigned to this rider.",
                        null
                    )

                    return@addOnSuccessListener
                }


                onResult(
                    true,
                    "Current order found.",
                    order
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to get current order.",
                    null
                )
            }
    }
}
package com.example.sulitpackgoui.rider

import com.example.sulitpackgoui.order.OrderData
import com.google.firebase.firestore.FirebaseFirestore

class RiderOrder {

    private var database = FirebaseFirestore.getInstance()


    // Get all Pending orders that match the Rider's vehicle
    fun getPendingOrders(
        rider: Rider,
        onResult: (Boolean, String, List<OrderData>) -> Unit
    ) {

        database
            .collection("orders")
            .whereEqualTo("status", "Pending")
            .get()
            .addOnSuccessListener { documents ->

                var pendingOrders = mutableListOf<OrderData>()


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


                    // Rider only sees orders for the same vehicle type
                    if (order.vehicleType == rider.vehicleType) {

                        pendingOrders.add(order)
                    }
                }


                // Newest orders appear first
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


    // Rider accepts a Pending order
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


    // Change delivery status
    //
    // Pick Up Pending
    //        ↓
    // Out for Delivery
    //        ↓
    // Delivered
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


                        // If delivery is finished,
                        // make Rider available again
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


    // Get the Rider's current active order
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


    // Get one specific order using its order ID
    fun getOrder(
        orderId: String,
        onResult: (Boolean, String, OrderData?) -> Unit
    ) {

        if (orderId.isBlank()) {

            onResult(
                false,
                "Invalid order ID.",
                null
            )

            return
        }


        database
            .collection("orders")
            .document(orderId)
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


                onResult(
                    true,
                    "Order found.",
                    order
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to get order.",
                    null
                )
            }
    }


    // Get all delivered orders for this Rider
    fun getDeliveryHistory(
        rider: Rider,
        onResult: (Boolean, String, List<OrderData>) -> Unit
    ) {

        if (rider.uid.isBlank()) {

            onResult(
                false,
                "Rider is not logged in.",
                emptyList()
            )

            return
        }


        database
            .collection("orders")
            .whereEqualTo("riderId", rider.uid)
            .get()
            .addOnSuccessListener { documents ->

                var history = mutableListOf<OrderData>()


                for (document in documents) {

                    var status =
                        document.getString("status") ?: ""


                    // Only delivered orders belong in history
                    if (status == "Delivered") {

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


                        history.add(order)
                    }
                }


                // Newest completed deliveries first
                history.sortByDescending {
                    it.createdAt
                }


                onResult(
                    true,
                    "Delivery history found.",
                    history
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to get delivery history.",
                    emptyList()
                )
            }
    }
}
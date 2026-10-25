package com.example.sulitpackgoui.rider

import com.google.firebase.firestore.FirebaseFirestore
import com.example.sulitpackgoui.order.OrderData

class RiderOrder {

    private var database = FirebaseFirestore.getInstance()


    fun getPendingOrders(
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
                        orderId = document.getString("orderId") ?: "",
                        customerId = document.getString("customerId") ?: "",
                        riderId = document.getString("riderId") ?: "",

                        pickupAddress =
                            document.getString("pickupAddress") ?: "",

                        pickupAddressDetails =
                            document.getString("pickupAddressDetails") ?: "",

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

                    pendingOrders.add(order)
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
            onResult(false, "Rider is not logged in.")
            return
        }

        if (rider.isAvailable == false) {
            onResult(false, "Rider already has an active order.")
            return
        }

        if (orderId.isBlank()) {
            onResult(false, "Invalid order.")
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


        database.runTransaction { transaction ->

            var orderDocument =
                transaction.get(orderReference)

            if (orderDocument.exists() == false) {
                throw Exception("Order not found.")
            }


            var orderStatus =
                orderDocument.getString("status") ?: ""

            if (orderStatus != "Pending") {
                throw Exception("Order is no longer available.")
            }


            var requiredVehicle =
                orderDocument.getString("vehicleType") ?: ""

            if (requiredVehicle != rider.vehicleType) {
                throw Exception(
                    "This order requires a $requiredVehicle."
                )
            }


            transaction.update(
                orderReference,
                mapOf(
                    "riderId" to rider.uid,
                    "status" to "Accepted"
                )
            )


            transaction.update(
                riderReference,
                mapOf(
                    "isAvailable" to false,
                    "currentOrderId" to orderId
                )
            )


            true
        }
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
                    error.message ?: "Failed to accept order."
                )
            }
    }

    fun updateDeliveryStatus(
        rider: Rider,
        newStatus: String,
        onResult: (Boolean, String) -> Unit
    ) {

        if (rider.uid.isBlank()) {
            onResult(false, "Rider is not logged in.")
            return
        }

        if (rider.currentOrderId.isBlank()) {
            onResult(false, "Rider has no active order.")
            return
        }


        var requiredCurrentStatus = ""

        when (newStatus) {

            "Picked Up" -> {
                requiredCurrentStatus = "Accepted"
            }

            "In Transit" -> {
                requiredCurrentStatus = "Picked Up"
            }

            "Delivered" -> {
                requiredCurrentStatus = "In Transit"
            }

            else -> {
                onResult(false, "Invalid delivery status.")
                return
            }
        }


        var orderReference =
            database
                .collection("orders")
                .document(rider.currentOrderId)

        var riderReference =
            database
                .collection("riders")
                .document(rider.uid)


        database.runTransaction { transaction ->

            var orderDocument =
                transaction.get(orderReference)


            if (orderDocument.exists() == false) {
                throw Exception("Order not found.")
            }


            var assignedRiderId =
                orderDocument.getString("riderId") ?: ""

            if (assignedRiderId != rider.uid) {
                throw Exception("This order does not belong to this rider.")
            }


            var currentStatus =
                orderDocument.getString("status") ?: ""

            if (currentStatus != requiredCurrentStatus) {
                throw Exception(
                    "Order must be $requiredCurrentStatus first."
                )
            }


            transaction.update(
                orderReference,
                "status",
                newStatus
            )


            if (newStatus == "Delivered") {

                transaction.update(
                    riderReference,
                    mapOf(
                        "isAvailable" to true,
                        "currentOrderId" to ""
                    )
                )
            }


            true
        }
            .addOnSuccessListener {

                if (newStatus == "Delivered") {
                    rider.isAvailable = true
                    rider.currentOrderId = ""
                }

                onResult(
                    true,
                    "Order status updated to $newStatus."
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to update order status."
                )
            }
    }

}


package com.example.sulitpackgoui.rider

import com.example.sulitpackgoui.order.OrderData
import com.google.firebase.firestore.FirebaseFirestore

class RiderOrder {

    private var database =
        FirebaseFirestore.getInstance()


    fun getPendingOrders(
        rider: Rider,
        onResult: (
            Boolean,
            String,
            List<OrderData>
        ) -> Unit
    ) {

        database
            .collection("orders")
            .whereEqualTo(
                "status",
                "Pending"
            )
            .get()
            .addOnSuccessListener { result ->


                var pendingOrders =
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

                        if (
                            order.vehicleType ==
                            rider.vehicleType
                        ) {

                            pendingOrders.add(
                                order
                            )
                        }
                    }
                }


                pendingOrders.sortByDescending { order ->

                    order.createdAt
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
                    getErrorMessage(
                        error,
                        "Failed to get pending orders."
                    ),
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


        if (
            rider.isAvailable == false
        ) {

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


                if (
                    document.exists() == false
                ) {

                    onResult(
                        false,
                        "Order not found."
                    )

                } else {

                    var order =
                        document.toObject(
                            OrderData::class.java
                        )


                    if (order == null) {

                        onResult(
                            false,
                            "Failed to read order."
                        )

                    } else if (
                        order.status !=
                        "Pending"
                    ) {

                        onResult(
                            false,
                            "Order is no longer available."
                        )

                    } else if (
                        order.vehicleType !=
                        rider.vehicleType
                    ) {

                        onResult(
                            false,
                            "This order requires a " +
                                    order.vehicleType +
                                    "."
                        )

                    } else {

                        orderReference
                            .update(
                                "riderId",
                                rider.uid,
                                "status",
                                "Pick Up Pending"
                            )
                            .addOnSuccessListener {


                                riderReference
                                    .update(
                                        "isAvailable",
                                        false,
                                        "currentOrderId",
                                        orderId
                                    )
                                    .addOnSuccessListener {


                                        rider.isAvailable =
                                            false


                                        rider.currentOrderId =
                                            orderId


                                        onResult(
                                            true,
                                            "Order accepted successfully."
                                        )
                                    }
                                    .addOnFailureListener { error ->

                                        onResult(
                                            false,
                                            getErrorMessage(
                                                error,
                                                "Failed to update rider."
                                            )
                                        )
                                    }
                            }
                            .addOnFailureListener { error ->

                                onResult(
                                    false,
                                    getErrorMessage(
                                        error,
                                        "Failed to accept order."
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
                        "Failed to get order."
                    )
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


        if (
            rider.currentOrderId.isBlank()
        ) {

            onResult(
                false,
                "Rider has no active order."
            )

            return
        }


        getOrder(
            rider.currentOrderId
        ) { success, message, order ->


            if (success == false) {

                onResult(
                    false,
                    message
                )

            } else if (
                order == null
            ) {

                onResult(
                    false,
                    "Order not found."
                )

            } else if (
                order.riderId !=
                rider.uid
            ) {

                onResult(
                    false,
                    "This order does not belong to this rider."
                )

            } else {

                moveToNextStatus(
                    rider,
                    order,
                    onResult
                )
            }
        }
    }


    private fun moveToNextStatus(
        rider: Rider,
        order: OrderData,
        onResult: (Boolean, String) -> Unit
    ) {

        var newStatus =
            ""


        if (
            order.status ==
            "Pick Up Pending"
        ) {

            newStatus =
                "Out for Delivery"

        } else if (
            order.status ==
            "Out for Delivery"
        ) {

            newStatus =
                "Delivered"

        } else {

            onResult(
                false,
                "No next delivery status."
            )

            return
        }


        var orderReference =
            database
                .collection("orders")
                .document(order.orderId)


        orderReference
            .update(
                "status",
                newStatus
            )
            .addOnSuccessListener {


                if (
                    newStatus ==
                    "Delivered"
                ) {

                    finishDelivery(
                        rider,
                        onResult
                    )

                } else {

                    onResult(
                        true,
                        "Order status updated to " +
                                newStatus +
                                "."
                    )
                }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    getErrorMessage(
                        error,
                        "Failed to update order status."
                    )
                )
            }
    }


    private fun finishDelivery(
        rider: Rider,
        onResult: (Boolean, String) -> Unit
    ) {

        var riderReference =
            database
                .collection("riders")
                .document(rider.uid)


        riderReference
            .update(
                "isAvailable",
                true,
                "currentOrderId",
                ""
            )
            .addOnSuccessListener {


                rider.isAvailable =
                    true


                rider.currentOrderId =
                    ""


                onResult(
                    true,
                    "Order delivered successfully."
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    getErrorMessage(
                        error,
                        "Failed to update rider."
                    )
                )
            }
    }


    fun getCurrentOrder(
        rider: Rider,
        onResult: (
            Boolean,
            String,
            OrderData?
        ) -> Unit
    ) {

        if (
            rider.currentOrderId.isBlank()
        ) {

            onResult(
                false,
                "Rider has no active order.",
                null
            )

            return
        }


        getOrder(
            rider.currentOrderId
        ) { success, message, order ->


            if (success == false) {

                onResult(
                    false,
                    message,
                    null
                )

            } else if (
                order == null
            ) {

                onResult(
                    false,
                    "Order not found.",
                    null
                )

            } else if (
                order.riderId !=
                rider.uid
            ) {

                onResult(
                    false,
                    "This order is not assigned to this rider.",
                    null
                )

            } else {

                onResult(
                    true,
                    "Current order found.",
                    order
                )
            }
        }
    }


    fun getOrder(
        orderId: String,
        onResult: (
            Boolean,
            String,
            OrderData?
        ) -> Unit
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


                if (
                    document.exists() == false
                ) {

                    onResult(
                        false,
                        "Order not found.",
                        null
                    )

                } else {

                    var order =
                        document.toObject(
                            OrderData::class.java
                        )


                    if (order == null) {

                        onResult(
                            false,
                            "Failed to read order.",
                            null
                        )

                    } else {

                        onResult(
                            true,
                            "Order found.",
                            order
                        )
                    }
                }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    getErrorMessage(
                        error,
                        "Failed to get order."
                    ),
                    null
                )
            }
    }


    fun getDeliveryHistory(
        rider: Rider,
        onResult: (
            Boolean,
            String,
            List<OrderData>
        ) -> Unit
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
            .whereEqualTo(
                "riderId",
                rider.uid
            )
            .get()
            .addOnSuccessListener { result ->


                var history =
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

                        if (
                            order.status ==
                            "Delivered"
                        ) {

                            history.add(
                                order
                            )
                        }
                    }
                }


                history.sortByDescending { order ->

                    order.createdAt
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
                    getErrorMessage(
                        error,
                        "Failed to get delivery history."
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
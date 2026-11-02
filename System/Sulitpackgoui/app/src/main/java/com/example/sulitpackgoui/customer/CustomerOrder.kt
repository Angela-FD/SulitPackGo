package com.example.sulitpackgoui.customer

import com.example.sulitpackgoui.admin.VehicleFeeDatabase
import com.example.sulitpackgoui.logic.DeliveryLocation
import com.example.sulitpackgoui.logic.FeeCalculator
import com.example.sulitpackgoui.logic.PackageInfo
import com.example.sulitpackgoui.logic.RouteDistance
import com.example.sulitpackgoui.logic.Vehicle
import com.example.sulitpackgoui.order.OrderData
import com.google.firebase.firestore.FirebaseFirestore

class CustomerOrder {

    private var database =
        FirebaseFirestore.getInstance()


    private var vehicleFeeDatabase =
        VehicleFeeDatabase()


    private var routeDistance =
        RouteDistance()


    fun createOrder(
        customer: Customer,
        location: DeliveryLocation,
        packageInfo: PackageInfo,
        vehicle: Vehicle,
        onResult: (Boolean, String, String) -> Unit
    ) {

        if (customer.uid.isBlank()) {

            onResult(
                false,
                "Customer is not logged in.",
                ""
            )

            return
        }


        if (location.address.isBlank()) {

            onResult(
                false,
                "Pickup address is missing.",
                ""
            )

            return
        }


        if (location.dropAddress.isBlank()) {

            onResult(
                false,
                "Drop-off address is missing.",
                ""
            )

            return
        }


        if (
            location.pickUpLatitude ==
            0.0
        ) {

            onResult(
                false,
                "Pickup location is missing.",
                ""
            )

            return
        }


        if (
            location.pickUpLongitude ==
            0.0
        ) {

            onResult(
                false,
                "Pickup location is missing.",
                ""
            )

            return
        }


        if (
            location.dropLatitude ==
            0.0
        ) {

            onResult(
                false,
                "Drop-off location is missing.",
                ""
            )

            return
        }


        if (
            location.dropLongitude ==
            0.0
        ) {

            onResult(
                false,
                "Drop-off location is missing.",
                ""
            )

            return
        }


        if (
            packageInfo.description.isBlank()
        ) {

            onResult(
                false,
                "Package description is missing.",
                ""
            )

            return
        }


        if (
            packageInfo.weight <= 0
        ) {

            onResult(
                false,
                "Package weight must be greater than 0.",
                ""
            )

            return
        }


        if (
            packageInfo.pieces <= 0
        ) {

            onResult(
                false,
                "Number of pieces must be greater than 0.",
                ""
            )

            return
        }


        if (
            vehicle.vehicleType.isBlank()
        ) {

            onResult(
                false,
                "Please select a vehicle.",
                ""
            )

            return
        }


        if (
            vehicle.checkWeight(
                packageInfo.weight
            ) == false
        ) {

            onResult(
                false,
                "Package is too heavy for the selected vehicle.",
                ""
            )

            return
        }


        routeDistance.getRoadDistance(
            location
        ) { success, distance, message ->


            if (success == false) {

                onResult(
                    false,
                    message,
                    ""
                )

            } else {

                getFeeAndSaveOrder(
                    customer,
                    location,
                    packageInfo,
                    vehicle,
                    distance,
                    onResult
                )
            }
        }
    }


    private fun getFeeAndSaveOrder(
        customer: Customer,
        location: DeliveryLocation,
        packageInfo: PackageInfo,
        vehicle: Vehicle,
        distance: Double,
        onResult: (Boolean, String, String) -> Unit
    ) {

        vehicleFeeDatabase.getVehicleFee(
            vehicle.vehicleType
        ) { success, message, vehicleFee ->


            if (success == false) {

                onResult(
                    false,
                    message,
                    ""
                )

            } else if (
                vehicleFee == null
            ) {

                onResult(
                    false,
                    "Vehicle fee not found.",
                    ""
                )

            } else {

                var calculator =
                    FeeCalculator(
                        vehicleFee = vehicleFee,
                        roadDistanceKm = distance
                    )


                var totalFee =
                    calculator.calculateTotalFee()


                saveOrder(
                    customer,
                    location,
                    packageInfo,
                    vehicle,
                    distance,
                    totalFee,
                    onResult
                )
            }
        }
    }


    private fun saveOrder(
        customer: Customer,
        location: DeliveryLocation,
        packageInfo: PackageInfo,
        vehicle: Vehicle,
        distance: Double,
        totalFee: Double,
        onResult: (Boolean, String, String) -> Unit
    ) {

        var orderDocument =
            database
                .collection("orders")
                .document()


        var orderId =
            orderDocument.id


        var order =
            OrderData(
                orderId = orderId,

                customerId = customer.uid,

                riderId = "",

                pickupAddress =
                    location.address,

                pickupAddressDetails =
                    location.addressDetails,

                pickupLatitude =
                    location.pickUpLatitude,

                pickupLongitude =
                    location.pickUpLongitude,

                dropAddress =
                    location.dropAddress,

                dropAddressDetails =
                    location.dropAddressDetails,

                dropLatitude =
                    location.dropLatitude,

                dropLongitude =
                    location.dropLongitude,

                description =
                    packageInfo.description,

                weight =
                    packageInfo.weight,

                pieces =
                    packageInfo.pieces,

                instructions =
                    packageInfo.instructions,

                vehicleType =
                    vehicle.vehicleType,

                distance =
                    distance,

                fee =
                    totalFee,

                status =
                    "Pending",

                createdAt =
                    System.currentTimeMillis()
            )


        orderDocument
            .set(
                order
            )
            .addOnSuccessListener {

                onResult(
                    true,
                    "Order created successfully.",
                    orderId
                )
            }
            .addOnFailureListener { error ->

                var message =
                    "Failed to create order."


                if (error.message != null) {

                    message =
                        error.message.toString()
                }


                onResult(
                    false,
                    message,
                    ""
                )
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
                "Order ID is missing.",
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
                            "Order loaded successfully.",
                            order
                        )
                    }
                }
            }
            .addOnFailureListener { error ->

                var message =
                    "Failed to load order."


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


    fun getMyOrders(
        customer: Customer,
        onResult: (
            Boolean,
            String,
            List<OrderData>
        ) -> Unit
    ) {

        if (customer.uid.isBlank()) {

            onResult(
                false,
                "Customer is not logged in.",
                emptyList()
            )

            return
        }


        database
            .collection("orders")
            .whereEqualTo(
                "customerId",
                customer.uid
            )
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

                var message =
                    "Failed to load orders."


                if (error.message != null) {

                    message =
                        error.message.toString()
                }


                onResult(
                    false,
                    message,
                    emptyList()
                )
            }
    }


    fun cancelOrder(
        customer: Customer,
        orderId: String,
        onResult: (Boolean, String) -> Unit
    ) {

        if (customer.uid.isBlank()) {

            onResult(
                false,
                "Customer is not logged in."
            )

            return
        }


        if (orderId.isBlank()) {

            onResult(
                false,
                "Order ID is missing."
            )

            return
        }


        var orderDocument =
            database
                .collection("orders")
                .document(orderId)


        orderDocument
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
                        order.customerId !=
                        customer.uid
                    ) {

                        onResult(
                            false,
                            "This order does not belong to you."
                        )

                    } else if (
                        order.status !=
                        "Pending"
                    ) {

                        onResult(
                            false,
                            "Only pending orders can be cancelled."
                        )

                    } else {

                        updateOrderToCancelled(
                            orderDocument,
                            onResult
                        )
                    }
                }
            }
            .addOnFailureListener { error ->

                var message =
                    "Failed to check order."


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


    private fun updateOrderToCancelled(
        orderDocument:
        com.google.firebase.firestore.DocumentReference,
        onResult: (Boolean, String) -> Unit
    ) {

        orderDocument
            .update(
                "status",
                "Cancelled"
            )
            .addOnSuccessListener {

                onResult(
                    true,
                    "Order cancelled successfully."
                )
            }
            .addOnFailureListener { error ->

                var message =
                    "Failed to cancel order."


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
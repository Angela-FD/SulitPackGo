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
            location.pickUpLatitude == 0.0 ||
            location.pickUpLongitude == 0.0
        ) {
            onResult(
                false,
                "Pickup location is missing.",
                ""
            )
            return
        }

        if (
            location.dropLatitude == 0.0 ||
            location.dropLongitude == 0.0
        ) {
            onResult(
                false,
                "Drop-off location is missing.",
                ""
            )
            return
        }

        if (packageInfo.description.isBlank()) {
            onResult(
                false,
                "Package description is missing.",
                ""
            )
            return
        }

        if (packageInfo.weight <= 0) {
            onResult(
                false,
                "Package weight must be greater than 0.",
                ""
            )
            return
        }

        if (packageInfo.pieces <= 0) {
            onResult(
                false,
                "Number of pieces must be greater than 0.",
                ""
            )
            return
        }

        if (vehicle.vehicleType.isBlank()) {
            onResult(
                false,
                "Please select a vehicle.",
                ""
            )
            return
        }

        if (!vehicle.checkWeight(packageInfo.weight)) {
            onResult(
                false,
                "Package is too heavy for the selected vehicle.",
                ""
            )
            return
        }

        routeDistance.getRoadDistance(
            location
        ) { distanceSuccess, roadDistanceKm, distanceMessage ->

            if (!distanceSuccess) {
                onResult(
                    false,
                    distanceMessage,
                    ""
                )
                return@getRoadDistance
            }

            vehicleFeeDatabase.getVehicleFee(
                vehicle.vehicleType
            ) { feeSuccess, feeMessage, vehicleFee ->

                if (!feeSuccess || vehicleFee == null) {
                    onResult(
                        false,
                        feeMessage,
                        ""
                    )
                    return@getVehicleFee
                }

                var calculator =
                    FeeCalculator(
                        devLocation = location,
                        feeVehicle = vehicle,
                        vehicleFee = vehicleFee,
                        roadDistanceKm = roadDistanceKm
                    )

                var totalFee =
                    calculator.calculateTotalFee()

                var orderDocument =
                    database
                        .collection("orders")
                        .document()

                var orderId =
                    orderDocument.id

                var createdAt =
                    System.currentTimeMillis()

                var orderData =
                    hashMapOf(
                        "orderId" to orderId,
                        "customerId" to customer.uid,
                        "riderId" to "",

                        "pickupAddress" to location.address,
                        "pickupAddressDetails" to location.addressDetails,
                        "pickupLatitude" to location.pickUpLatitude,
                        "pickupLongitude" to location.pickUpLongitude,

                        "dropAddress" to location.dropAddress,
                        "dropAddressDetails" to location.dropAddressDetails,
                        "dropLatitude" to location.dropLatitude,
                        "dropLongitude" to location.dropLongitude,

                        "description" to packageInfo.description,
                        "weight" to packageInfo.weight,
                        "pieces" to packageInfo.pieces,
                        "instructions" to packageInfo.instructions,

                        "vehicleType" to vehicle.vehicleType,

                        "distance" to roadDistanceKm,
                        "fee" to totalFee,

                        "status" to "Pending",
                        "createdAt" to createdAt
                    )

                orderDocument
                    .set(orderData)
                    .addOnSuccessListener {

                        onResult(
                            true,
                            "Order created successfully.",
                            orderId
                        )
                    }
                    .addOnFailureListener { error ->

                        onResult(
                            false,
                            error.message
                                ?: "Failed to create order.",
                            ""
                        )
                    }
            }
        }
    }

    fun getOrder(
        orderId: String,
        onResult: (Boolean, String, OrderData?) -> Unit
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

                if (!document.exists()) {
                    onResult(
                        false,
                        "Order not found.",
                        null
                    )
                    return@addOnSuccessListener
                }

                var order =
                    OrderData(
                        orderId =
                            document.getString("orderId") ?: "",

                        customerId =
                            document.getString("customerId") ?: "",

                        riderId =
                            document.getString("riderId") ?: "",

                        pickupAddress =
                            document.getString("pickupAddress") ?: "",

                        pickupAddressDetails =
                            document.getString(
                                "pickupAddressDetails"
                            ) ?: "",

                        dropAddress =
                            document.getString("dropAddress") ?: "",

                        dropAddressDetails =
                            document.getString(
                                "dropAddressDetails"
                            ) ?: "",

                        pickupLatitude =
                            document.getDouble(
                                "pickupLatitude"
                            ) ?: 0.0,

                        pickupLongitude =
                            document.getDouble(
                                "pickupLongitude"
                            ) ?: 0.0,

                        dropLatitude =
                            document.getDouble(
                                "dropLatitude"
                            ) ?: 0.0,

                        dropLongitude =
                            document.getDouble(
                                "dropLongitude"
                            ) ?: 0.0,

                        description =
                            document.getString(
                                "description"
                            ) ?: "",

                        weight =
                            document.getDouble(
                                "weight"
                            ) ?: 0.0,

                        pieces =
                            document.getLong(
                                "pieces"
                            )?.toInt() ?: 0,

                        instructions =
                            document.getString(
                                "instructions"
                            ) ?: "",

                        vehicleType =
                            document.getString(
                                "vehicleType"
                            ) ?: "",

                        distance =
                            document.getDouble(
                                "distance"
                            ) ?: 0.0,

                        fee =
                            document.getDouble(
                                "fee"
                            ) ?: 0.0,

                        status =
                            document.getString(
                                "status"
                            ) ?: "",

                        createdAt =
                            document.getLong(
                                "createdAt"
                            ) ?: 0L
                    )

                onResult(
                    true,
                    "Order loaded successfully.",
                    order
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message
                        ?: "Failed to load order.",
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

                for (document in result.documents) {

                    var order =
                        OrderData(
                            orderId =
                                document.getString("orderId") ?: "",

                            customerId =
                                document.getString("customerId") ?: "",

                            riderId =
                                document.getString("riderId") ?: "",

                            pickupAddress =
                                document.getString("pickupAddress") ?: "",

                            pickupAddressDetails =
                                document.getString(
                                    "pickupAddressDetails"
                                ) ?: "",

                            dropAddress =
                                document.getString("dropAddress") ?: "",

                            dropAddressDetails =
                                document.getString(
                                    "dropAddressDetails"
                                ) ?: "",

                            pickupLatitude =
                                document.getDouble(
                                    "pickupLatitude"
                                ) ?: 0.0,

                            pickupLongitude =
                                document.getDouble(
                                    "pickupLongitude"
                                ) ?: 0.0,

                            dropLatitude =
                                document.getDouble(
                                    "dropLatitude"
                                ) ?: 0.0,

                            dropLongitude =
                                document.getDouble(
                                    "dropLongitude"
                                ) ?: 0.0,

                            description =
                                document.getString(
                                    "description"
                                ) ?: "",

                            weight =
                                document.getDouble(
                                    "weight"
                                ) ?: 0.0,

                            pieces =
                                document.getLong(
                                    "pieces"
                                )?.toInt() ?: 0,

                            instructions =
                                document.getString(
                                    "instructions"
                                ) ?: "",

                            vehicleType =
                                document.getString(
                                    "vehicleType"
                                ) ?: "",

                            distance =
                                document.getDouble(
                                    "distance"
                                ) ?: 0.0,

                            fee =
                                document.getDouble(
                                    "fee"
                                ) ?: 0.0,

                            status =
                                document.getString(
                                    "status"
                                ) ?: "",

                            createdAt =
                                document.getLong(
                                    "createdAt"
                                ) ?: 0L
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
                    error.message
                        ?: "Failed to load orders.",
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

                if (!document.exists()) {
                    onResult(
                        false,
                        "Order not found."
                    )
                    return@addOnSuccessListener
                }

                var customerId =
                    document.getString(
                        "customerId"
                    ) ?: ""

                var status =
                    document.getString(
                        "status"
                    ) ?: ""

                if (customerId != customer.uid) {
                    onResult(
                        false,
                        "This order does not belong to you."
                    )
                    return@addOnSuccessListener
                }

                if (status != "Pending") {
                    onResult(
                        false,
                        "Only pending orders can be cancelled."
                    )
                    return@addOnSuccessListener
                }

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

                        onResult(
                            false,
                            error.message
                                ?: "Failed to cancel order."
                        )
                    }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message
                        ?: "Failed to check order."
                )
            }
    }
}
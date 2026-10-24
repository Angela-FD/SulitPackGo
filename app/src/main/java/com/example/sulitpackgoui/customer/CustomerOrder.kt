package com.example.sulitpackgoui.customer

import com.example.sulitpackgoui.DeliveryLocation
import com.example.sulitpackgoui.FeeCalculator
import com.example.sulitpackgoui.PackageInfo
import com.example.sulitpackgoui.Vehicle
import com.google.firebase.firestore.FirebaseFirestore
import com.example.sulitpackgoui.order.OrderData

class CustomerOrder {

    private var database = FirebaseFirestore.getInstance()


    fun createOrder(
        customer: Customer,
        location: DeliveryLocation,
        packageInfo: PackageInfo,
        vehicle: Vehicle,
        onResult: (Boolean, String) -> Unit
    ) {

        if (customer.uid.isBlank()) {
            onResult(false, "Customer is not logged in.")
            return
        }


        if (vehicle.checkWeight(packageInfo.weight) == false) {
            onResult(
                false,
                "Package is too heavy for the selected vehicle."
            )
            return
        }


        var distance =
            location.distanceCalculate()


        var calculator = FeeCalculator(
            devLocation = location,
            feeVehicle = vehicle
        )


        var fee =
            calculator.calculateTotalFee()


        var orderDocument =
            database
                .collection("orders")
                .document()


        var orderId =
            orderDocument.id


        var createdAt =
            System.currentTimeMillis()


        var orderData = hashMapOf(
            "orderId" to orderId,

            "customerId" to customer.uid,
            "riderId" to "",

            "pickupAddress" to location.address,
            "pickupAddressDetails" to location.addressDetails,

            "pickupLatitude" to location.pickUpLatitude,
            "pickupLongitude" to location.pickUpLongitude,

            "dropLatitude" to location.dropLatitude,
            "dropLongitude" to location.dropLongitude,

            "description" to packageInfo.description,
            "weight" to packageInfo.weight,

            "vehicleType" to vehicle.vehicleType,

            "distance" to distance,
            "fee" to fee,

            "status" to "Pending",

            "createdAt" to createdAt
        )


        orderDocument
            .set(orderData)
            .addOnSuccessListener {

                onResult(
                    true,
                    "Order created successfully."
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to create order."
                )
            }
    }

    fun getMyOrders(
        customer: Customer,
        onResult: (Boolean, String, List<OrderData>) -> Unit
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
            .whereEqualTo("customerId", customer.uid)
            .get()
            .addOnSuccessListener { documents ->

                var customerOrders =
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


                    customerOrders.add(order)
                }


                onResult(
                    true,
                    "Customer orders found.",
                    customerOrders
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to get customer orders.",
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
            onResult(false, "Customer is not logged in.")
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


        database.runTransaction { transaction ->

            var orderDocument =
                transaction.get(orderReference)


            if (orderDocument.exists() == false) {
                throw Exception("Order not found.")
            }


            var orderCustomerId =
                orderDocument.getString("customerId") ?: ""

            if (orderCustomerId != customer.uid) {
                throw Exception("This order does not belong to this customer.")
            }


            var currentStatus =
                orderDocument.getString("status") ?: ""

            if (currentStatus != "Pending") {
                throw Exception(
                    "Only pending orders can be cancelled."
                )
            }


            transaction.update(
                orderReference,
                "status",
                "Cancelled"
            )


            true
        }
            .addOnSuccessListener {

                onResult(
                    true,
                    "Order cancelled successfully."
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to cancel order."
                )
            }
    }
}
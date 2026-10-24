package com.example.sulitpackgoui

import com.google.firebase.firestore.FirebaseFirestore

class DeliveryOrder(
    var user: User,
    var location: DeliveryLocation,
    var packageInfo: PackageInfo,
    var vehicle: Vehicle,

    var orderId: String = "",
    var riderId: String = "",

    var distance: Double = 0.0,
    var fee: Double = 0.0,
    var status: String = "Pending",

    var createdAt: Long = 0L
) {

    private var database = FirebaseFirestore.getInstance()


    fun processOrder(): Boolean {

        if (vehicle.checkWeight(packageInfo.weight) == false) {
            return false
        }

        distance = location.distanceCalculate()

        var calculator = FeeCalculator(
            devLocation = location,
            feeVehicle = vehicle
        )

        fee = calculator.calculateTotalFee()

        status = "Pending"

        return true
    }


    fun saveOrder(
        onResult: (Boolean, String) -> Unit
    ) {

        if (user.uid.isBlank()) {
            onResult(
                false,
                "User is not logged in."
            )
            return
        }


        if (processOrder() == false) {
            onResult(
                false,
                "Package is too heavy for the selected vehicle."
            )
            return
        }


        var orderDocument =
            database
                .collection("orders")
                .document()


        this.orderId = orderDocument.id
        this.createdAt = System.currentTimeMillis()


        var orderData = hashMapOf(
            "orderId" to orderId,

            "customerId" to user.uid,
            "riderId" to riderId,

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

            "status" to status,

            "createdAt" to createdAt
        )


        orderDocument
            .set(orderData)
            .addOnSuccessListener {

                onResult(
                    true,
                    "Order saved successfully."
                )
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Failed to save order."
                )
            }
    }
}
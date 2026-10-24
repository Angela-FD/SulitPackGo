package com.example.sulitpackgoui

class DeliveryOrder(
    var user: User,
    var location: DeliveryLocation,
    var packageInfo: PackageInfo,
    var vehicle: Vehicle,
    var orderId: String = "",
    var distance: Double = 0.0,
    var fee: Double = 0.0,
    var status: String = "Pending"
) {

    fun processOrder(): Boolean {

        // Check if package can fit the vehicle
        if (vehicle.checkWeight(packageInfo.weight) == false) {
            return false
        }

        // Calculate distance
        distance = location.distanceCalculate()


        // Calculate fee
        var calculator = FeeCalculator(
            devLocation = location,
            feeVehicle = vehicle
        )

        fee = calculator.calculateTotalFee()


        // Create order ID
        if (orderId.isBlank()) {
            orderId = "ORD-" + System.currentTimeMillis()
        }


        status = "Pending"

        return true
    }


    fun updateStatus(newStatus: String): Boolean {

        if (
            newStatus == "Pending" ||
            newStatus == "Accepted" ||
            newStatus == "Picked Up" ||
            newStatus == "In Transit" ||
            newStatus == "Delivered" ||
            newStatus == "Cancelled"
        ) {

            status = newStatus

            return true
        }

        return false
    }
}
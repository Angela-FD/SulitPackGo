package com.example.sulitpackgoui

class DeliveryOrder(
    var user: User,
    var location: DeliveryLocation,
    var packageInfo: PackageInfo,
    var vehicle: Vehicle,
    var distance: Double = 0.0,
    var fee: Double = 0.0,
    var status: String = "Pending"
) {
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

        return true
    }
}
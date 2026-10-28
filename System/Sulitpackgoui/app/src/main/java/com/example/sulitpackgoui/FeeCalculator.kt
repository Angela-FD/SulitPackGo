package com.example.sulitpackgoui

import com.example.sulitpackgoui.admin.VehicleFee

class FeeCalculator(
    var devLocation: DeliveryLocation,
    var feeVehicle: Vehicle,
    var vehicleFee: VehicleFee,
    var totalFee: Double = 0.0
) {

    fun calculateBaseFee(): Double {
        return vehicleFee.baseFee
    }

    fun calculateDistanceFee(): Double {
        var distanceKm = devLocation.distanceCalculate()

        return vehicleFee.ratePerKm * distanceKm
    }

    fun calculateTotalFee(): Double {
        var base = calculateBaseFee()
        var distanceFee = calculateDistanceFee()

        totalFee = base + distanceFee

        return totalFee
    }
}
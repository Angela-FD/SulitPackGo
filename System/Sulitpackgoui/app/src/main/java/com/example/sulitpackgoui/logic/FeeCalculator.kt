package com.example.sulitpackgoui.logic

import com.example.sulitpackgoui.admin.VehicleFee

class FeeCalculator(
    var devLocation: DeliveryLocation,
    var feeVehicle: Vehicle,
    var vehicleFee: VehicleFee,
    var roadDistanceKm: Double = 0.0,
    var totalFee: Double = 0.0
) {

    fun calculateBaseFee(): Double {
        return vehicleFee.baseFee
    }

    fun calculateDistanceFee(): Double {
        return vehicleFee.ratePerKm * roadDistanceKm
    }

    fun calculateTotalFee(): Double {
        var base = calculateBaseFee()
        var distanceFee = calculateDistanceFee()

        totalFee = base + distanceFee

        return totalFee
    }
}
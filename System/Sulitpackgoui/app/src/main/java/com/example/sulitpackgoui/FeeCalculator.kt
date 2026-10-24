package com.example.sulitpackgoui

class FeeCalculator(
    var devLocation: DeliveryLocation,
    var feeVehicle: Vehicle,
    var baseFee: Double = 0.0,
    var ratePerKm: Double = 0.0,
    var totalFee: Double = 0.0
) {

    fun calculateBaseFee(): Double {

        when (feeVehicle.vehicleType) {
            "Truck" -> this.baseFee = 1500.0
            "Van" -> this.baseFee = 1000.0
            "Tricycle" -> this.baseFee = 500.0
            "Motor" -> this.baseFee = 50.0
            else -> this.baseFee = 0.0
        }

        return baseFee
    }


    fun calculateDistanceFee(): Double {

        var distanceKm = devLocation.distanceCalculate()

        when (feeVehicle.vehicleType) {
            "Truck" -> this.ratePerKm = 200.0
            "Van" -> this.ratePerKm = 100.0
            "Tricycle" -> this.ratePerKm = 50.0
            "Motor" -> this.ratePerKm = 25.0
            else -> this.ratePerKm = 0.0
        }

        return ratePerKm * distanceKm
    }


    fun calculateTotalFee(): Double {

        var base = calculateBaseFee()
        var distanceFee = calculateDistanceFee()

        this.totalFee = base + distanceFee

        return totalFee
    }
}
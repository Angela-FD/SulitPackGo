package com.example.sulitpackgoui

class Vehicle(
    var vehicleType: String = "",
    var weightLimit: Double = 0.0
) {

    fun selectVehicle(selectedVehicle: String) {

        when (selectedVehicle) {

            "Motor" -> {
                vehicleType = "Motor"
                weightLimit = 40.0
            }

            "Tricycle" -> {
                vehicleType = "Tricycle"
                weightLimit = 80.0
            }

            "Van" -> {
                vehicleType = "Van"
                weightLimit = 300.0
            }

            "Truck" -> {
                vehicleType = "Truck"
                weightLimit = 1000.0
            }

            else -> {
                vehicleType = ""
                weightLimit = 0.0
            }
        }
    }

    fun checkWeight(packageWeight: Double): Boolean {

        if (packageWeight <= weightLimit) {
            return true
        } else {
            return false
        }
    }
}
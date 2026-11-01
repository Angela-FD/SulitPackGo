package com.example.sulitpackgoui.logic

import com.example.sulitpackgoui.admin.VehicleFee

class FeeCalculator(

    var vehicleFee: VehicleFee,

    var roadDistanceKm: Double
) {


    fun calculateBaseFee(): Double {

        return vehicleFee.baseFee
    }


    fun calculateDistanceFee(): Double {

        return vehicleFee.ratePerKm *
                roadDistanceKm
    }


    fun calculateTotalFee(): Double {

        var baseFee =
            calculateBaseFee()


        var distanceFee =
            calculateDistanceFee()


        return baseFee +
                distanceFee
    }
}
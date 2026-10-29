package com.example.sulitpackgoui.logic

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class DeliveryLocation (
    var address: String = "",
    var addressDetails: String = "",

    var pickUpLatitude: Double = 0.0,
    var pickUpLongitude: Double = 0.0,

    var dropLatitude: Double = 0.0,
    var dropLongitude: Double = 0.0,

    var dropAddress: String = "",
    var dropAddressDetails: String = "",
){
    fun distanceCalculate(): Double {

        val earthRadius = 6371.0 // kilometers

        val lat1 = Math.toRadians(pickUpLatitude)
        val lon1 = Math.toRadians(pickUpLongitude)
        val lat2 = Math.toRadians(dropLatitude)
        val lon2 = Math.toRadians(dropLongitude)

        val latDifference = lat2 - lat1
        val lonDifference = lon2 - lon1

        val a =
            sin(latDifference / 2) * sin(latDifference / 2) +
                    cos(lat1) * cos(lat2) *
                    sin(lonDifference / 2) * sin(lonDifference / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }
}
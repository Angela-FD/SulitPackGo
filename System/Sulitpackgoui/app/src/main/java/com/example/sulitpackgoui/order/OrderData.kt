package com.example.sulitpackgoui.order

class OrderData(
    var orderId: String = "",
    var customerId: String = "",
    var riderId: String = "",

    var pickupAddress: String = "",
    var pickupAddressDetails: String = "",

    var pickupLatitude: Double = 0.0,
    var pickupLongitude: Double = 0.0,

    var dropLatitude: Double = 0.0,
    var dropLongitude: Double = 0.0,

    var description: String = "",
    var weight: Double = 0.0,

    var vehicleType: String = "",

    var distance: Double = 0.0,
    var fee: Double = 0.0,

    var status: String = "",
    var createdAt: Long = 0L
)
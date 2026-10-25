package com.example.sulitpackgoui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BackendTest {


    // =========================================
    // VEHICLE TESTS
    // =========================================

    @Test
    fun testMotorVehicle() {

        var vehicle = Vehicle()

        vehicle.selectVehicle("Motor")


        assertEquals(
            "Motor",
            vehicle.vehicleType
        )


        assertEquals(
            40.0,
            vehicle.weightLimit,
            0.0
        )
    }


    @Test
    fun testMotorAllowedWeight() {

        var vehicle = Vehicle()

        vehicle.selectVehicle("Motor")


        var allowed =
            vehicle.checkWeight(20.0)


        assertTrue(allowed)
    }


    @Test
    fun testMotorTooHeavy() {

        var vehicle = Vehicle()

        vehicle.selectVehicle("Motor")


        var allowed =
            vehicle.checkWeight(70.0)


        assertFalse(allowed)
    }


    @Test
    fun testTricycleVehicle() {

        var vehicle = Vehicle()

        vehicle.selectVehicle("Tricycle")


        assertEquals(
            "Tricycle",
            vehicle.vehicleType
        )


        assertEquals(
            80.0,
            vehicle.weightLimit,
            0.0
        )
    }


    @Test
    fun testVanVehicle() {

        var vehicle = Vehicle()

        vehicle.selectVehicle("Van")


        assertEquals(
            "Van",
            vehicle.vehicleType
        )


        assertEquals(
            300.0,
            vehicle.weightLimit,
            0.0
        )
    }


    @Test
    fun testTruckVehicle() {

        var vehicle = Vehicle()

        vehicle.selectVehicle("Truck")


        assertEquals(
            "Truck",
            vehicle.vehicleType
        )


        assertEquals(
            1000.0,
            vehicle.weightLimit,
            0.0
        )
    }


    @Test
    fun testInvalidVehicle() {

        var vehicle = Vehicle()

        vehicle.selectVehicle("Car")


        assertEquals(
            "",
            vehicle.vehicleType
        )


        assertEquals(
            0.0,
            vehicle.weightLimit,
            0.0
        )
    }



    // =========================================
    // PACKAGE INFO TEST
    // =========================================

    @Test
    fun testPackageInfo() {

        var packageInfo = PackageInfo(
            description = "Laptop and charger",
            packageType = "Electronics",
            weight = 4.5,
            pieces = 2,
            instructions = "Handle with care"
        )


        assertEquals(
            "Laptop and charger",
            packageInfo.description
        )


        assertEquals(
            "Electronics",
            packageInfo.packageType
        )


        assertEquals(
            4.5,
            packageInfo.weight,
            0.0
        )


        assertEquals(
            2,
            packageInfo.pieces
        )


        assertEquals(
            "Handle with care",
            packageInfo.instructions
        )
    }



    // =========================================
    // DELIVERY LOCATION TESTS
    // =========================================

    @Test
    fun testDeliveryLocationInformation() {

        var location = DeliveryLocation(
            address = "Nasugbu, Batangas",
            addressDetails = "Near the school",

            pickUpLatitude = 14.0723,
            pickUpLongitude = 120.6298,

            dropLatitude = 14.0800,
            dropLongitude = 120.6350,

            dropAddress = "Lian, Batangas",
            dropAddressDetails = "Near the market"
        )


        assertEquals(
            "Nasugbu, Batangas",
            location.address
        )


        assertEquals(
            "Near the school",
            location.addressDetails
        )


        assertEquals(
            "Lian, Batangas",
            location.dropAddress
        )


        assertEquals(
            "Near the market",
            location.dropAddressDetails
        )
    }


    @Test
    fun testDeliveryDistance() {

        var location = DeliveryLocation(
            pickUpLatitude = 14.0723,
            pickUpLongitude = 120.6298,

            dropLatitude = 14.0800,
            dropLongitude = 120.6350
        )


        var distance =
            location.distanceCalculate()


        assertEquals(
            1.02,
            distance,
            0.05
        )
    }



    // =========================================
    // FEE CALCULATOR TESTS
    // =========================================

    @Test
    fun testMotorBaseFee() {

        var location = DeliveryLocation()


        var vehicle = Vehicle()

        vehicle.selectVehicle("Motor")


        var calculator = FeeCalculator(
            devLocation = location,
            feeVehicle = vehicle
        )


        var baseFee =
            calculator.calculateBaseFee()


        assertEquals(
            50.0,
            baseFee,
            0.0
        )
    }


    @Test
    fun testTricycleBaseFee() {

        var location = DeliveryLocation()


        var vehicle = Vehicle()

        vehicle.selectVehicle("Tricycle")


        var calculator = FeeCalculator(
            devLocation = location,
            feeVehicle = vehicle
        )


        var baseFee =
            calculator.calculateBaseFee()


        assertEquals(
            500.0,
            baseFee,
            0.0
        )
    }


    @Test
    fun testVanBaseFee() {

        var location = DeliveryLocation()


        var vehicle = Vehicle()

        vehicle.selectVehicle("Van")


        var calculator = FeeCalculator(
            devLocation = location,
            feeVehicle = vehicle
        )


        var baseFee =
            calculator.calculateBaseFee()


        assertEquals(
            1000.0,
            baseFee,
            0.0
        )
    }


    @Test
    fun testTruckBaseFee() {

        var location = DeliveryLocation()


        var vehicle = Vehicle()

        vehicle.selectVehicle("Truck")


        var calculator = FeeCalculator(
            devLocation = location,
            feeVehicle = vehicle
        )


        var baseFee =
            calculator.calculateBaseFee()


        assertEquals(
            1500.0,
            baseFee,
            0.0
        )
    }


    @Test
    fun testMotorDistanceFee() {

        var location = DeliveryLocation(
            pickUpLatitude = 14.0723,
            pickUpLongitude = 120.6298,

            dropLatitude = 14.0800,
            dropLongitude = 120.6350
        )


        var vehicle = Vehicle()

        vehicle.selectVehicle("Motor")


        var calculator = FeeCalculator(
            devLocation = location,
            feeVehicle = vehicle
        )


        var distanceFee =
            calculator.calculateDistanceFee()


        assertEquals(
            25.59,
            distanceFee,
            0.10
        )
    }


    @Test
    fun testMotorTotalFee() {

        var location = DeliveryLocation(
            pickUpLatitude = 14.0723,
            pickUpLongitude = 120.6298,

            dropLatitude = 14.0800,
            dropLongitude = 120.6350
        )


        var vehicle = Vehicle()

        vehicle.selectVehicle("Motor")


        var calculator = FeeCalculator(
            devLocation = location,
            feeVehicle = vehicle
        )


        var totalFee =
            calculator.calculateTotalFee()


        assertEquals(
            75.59,
            totalFee,
            0.10
        )
    }
}
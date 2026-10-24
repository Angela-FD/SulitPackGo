package com.example.sulitpackgoui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BackendTest {

    // This runs before every test.
    // It makes sure old test data does not affect the next test.
    @Before
    fun resetData() {

        TemporaryStorage.users.clear()
        Session.logout()
    }


    // =========================================
    // USER TESTS
    // =========================================

    @Test
    fun testUserRegister() {

        var user = User()

        var result = user.register(
            userName = "Test User",
            userEmail = "test@email.com",
            userPassword = "password123",
            confirmPassword = "password123"
        )

        assertEquals(
            "Registration successful.",
            result
        )

        assertEquals(
            "Test User",
            user.name
        )

        assertEquals(
            "test@email.com",
            user.email
        )

        assertEquals(
            1,
            TemporaryStorage.users.size
        )
    }


    @Test
    fun testUserWrongPasswordConfirmation() {

        var user = User()

        var result = user.register(
            userName = "Test User",
            userEmail = "test@email.com",
            userPassword = "password123",
            confirmPassword = "wrongPassword"
        )

        assertEquals(
            "Passwords do not match.",
            result
        )

        assertEquals(
            0,
            TemporaryStorage.users.size
        )
    }


    @Test
    fun testDuplicateEmail() {

        var user1 = User()

        user1.register(
            userName = "User One",
            userEmail = "test@email.com",
            userPassword = "password123",
            confirmPassword = "password123"
        )


        var user2 = User()

        var result = user2.register(
            userName = "User Two",
            userEmail = "test@email.com",
            userPassword = "password456",
            confirmPassword = "password456"
        )

        assertEquals(
            "Email is already registered.",
            result
        )

        assertEquals(
            1,
            TemporaryStorage.users.size
        )
    }


    @Test
    fun testUserLogin() {

        var user = User()

        user.register(
            userName = "Test User",
            userEmail = "test@email.com",
            userPassword = "password123",
            confirmPassword = "password123"
        )


        var loginUser = User()

        var loginResult = loginUser.logIn(
            userEmail = "test@email.com",
            userPassword = "password123"
        )

        assertTrue(loginResult)

        assertTrue(Session.isLoggedIn)

        assertEquals(
            "Test User",
            Session.name
        )

        assertEquals(
            "test@email.com",
            Session.email
        )
    }


    @Test
    fun testWrongLogin() {

        var user = User()

        user.register(
            userName = "Test User",
            userEmail = "test@email.com",
            userPassword = "password123",
            confirmPassword = "password123"
        )


        var loginUser = User()

        var loginResult = loginUser.logIn(
            userEmail = "test@email.com",
            userPassword = "wrongPassword"
        )

        assertFalse(loginResult)

        assertFalse(Session.isLoggedIn)
    }


    // =========================================
    // SESSION TEST
    // =========================================

    @Test
    fun testSessionLogout() {

        Session.isLoggedIn = true
        Session.name = "Test User"
        Session.email = "test@email.com"

        Session.logout()

        assertFalse(Session.isLoggedIn)

        assertEquals(
            "",
            Session.name
        )

        assertEquals(
            "",
            Session.email
        )
    }


    // =========================================
    // TEMPORARY STORAGE TEST
    // =========================================

    @Test
    fun testTemporaryStorage() {

        var user = User()

        user.register(
            userName = "Test User",
            userEmail = "test@email.com",
            userPassword = "password123",
            confirmPassword = "password123"
        )

        assertEquals(
            1,
            TemporaryStorage.users.size
        )

        assertEquals(
            "test@email.com",
            TemporaryStorage.users[0].email
        )
    }


    // =========================================
    // VEHICLE TESTS
    // =========================================

    @Test
    fun testVehicle() {

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


        var allowed =
            vehicle.checkWeight(20.0)

        assertTrue(allowed)
    }


    @Test
    fun testVehicleTooHeavy() {

        var vehicle = Vehicle()

        vehicle.selectVehicle("Motor")

        var allowed =
            vehicle.checkWeight(70.0)

        assertFalse(allowed)
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


    // =========================================
    // PACKAGE INFO TEST
    // =========================================

    @Test
    fun testPackageInfo() {

        var packageInfo = PackageInfo(
            description = "Box of clothes",
            weight = 20.0
        )

        assertEquals(
            "Box of clothes",
            packageInfo.description
        )

        assertEquals(
            20.0,
            packageInfo.weight,
            0.0
        )
    }


    // =========================================
    // DELIVERY LOCATION TEST
    // =========================================

    @Test
    fun testDeliveryLocation() {

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


        var baseFee =
            calculator.calculateBaseFee()


        assertEquals(
            50.0,
            baseFee,
            0.0
        )
    }


    @Test
    fun testDistanceFee() {

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
    fun testTotalFee() {

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


        var total =
            calculator.calculateTotalFee()


        assertEquals(
            75.59,
            total,
            0.10
        )
    }


    // =========================================
    // DELIVERY ORDER TESTS
    // =========================================

    @Test
    fun testDeliveryOrder() {

        var user = User(
            name = "Test User",
            email = "test@email.com"
        )


        var location = DeliveryLocation(
            pickUpLatitude = 14.0723,
            pickUpLongitude = 120.6298,
            dropLatitude = 14.0800,
            dropLongitude = 120.6350
        )


        var packageInfo = PackageInfo(
            description = "Box of clothes",
            weight = 20.0
        )


        var vehicle = Vehicle()

        vehicle.selectVehicle("Motor")


        var order = DeliveryOrder(
            user = user,
            location = location,
            packageInfo = packageInfo,
            vehicle = vehicle
        )


        var result =
            order.processOrder()


        assertTrue(result)

        assertTrue(
            order.distance > 0.0
        )

        assertTrue(
            order.fee > 0.0
        )

        assertEquals(
            "Pending",
            order.status
        )
    }


    @Test
    fun testDeliveryOrderTooHeavy() {

        var user = User(
            name = "Test User",
            email = "test@email.com"
        )


        var location = DeliveryLocation(
            pickUpLatitude = 14.0723,
            pickUpLongitude = 120.6298,
            dropLatitude = 14.0800,
            dropLongitude = 120.6350
        )


        // 70 kg is too heavy for Motor
        var packageInfo = PackageInfo(
            description = "Heavy package",
            weight = 70.0
        )


        var vehicle = Vehicle()

        vehicle.selectVehicle("Motor")


        var order = DeliveryOrder(
            user = user,
            location = location,
            packageInfo = packageInfo,
            vehicle = vehicle
        )


        var result =
            order.processOrder()


        assertFalse(result)
    }
}
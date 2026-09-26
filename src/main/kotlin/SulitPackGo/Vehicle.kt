package SulitPackGo

class Vehicle(
    var vehicleType: String = "",
    var weightLimit: Double = 0.0
) {

    val listOfVehicles: List<String> =
        listOf("Motor", "Tricycle", "Van", "Truck")
    fun selectVehicle() {
        var tempVehicleType: String
        do {
            print("Enter vehicle: ")
            tempVehicleType = readln()
        } while (tempVehicleType !in listOfVehicles)
        this.vehicleType = tempVehicleType

        when (tempVehicleType) {
            "Motor" -> this.weightLimit = 40.0
            "Tricycle" -> this.weightLimit = 80.0
            "Van" -> this.weightLimit = 300.0
            "Truck" -> this.weightLimit = 1000.0
        }
    }
}
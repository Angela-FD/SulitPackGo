package SulitPackGo

class DeliveryLocation (
    var address: String = "",
    var addressDetails: String = "",
    var pickUpLatitude: Double = 0.0,
    var pickUpLongitude: Double = 0.0,
    var dropLatitude: Double = 0.0,
    var dropLongitude: Double = 0.0,
){
    fun searchLocation(address: String, latitude: Double, longitude: Double) {

    }
}
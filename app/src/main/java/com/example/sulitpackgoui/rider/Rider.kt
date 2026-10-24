package com.example.sulitpackgoui.rider

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Rider(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var vehicleType: String = "",
    var isAvailable: Boolean = true,
    var currentOrderId: String = ""
)

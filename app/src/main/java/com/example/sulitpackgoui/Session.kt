package com.example.sulitpackgoui

object Session {

    var isLoggedIn = false
    var name = ""
    var email = ""

    fun logout() {
        isLoggedIn = false
        name = ""
        email = ""
    }
}
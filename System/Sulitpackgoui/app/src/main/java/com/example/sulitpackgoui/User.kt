package com.example.sulitpackgoui

class User(
    var name: String = "",
    var email: String = "",
    var password: String = ""
) {

    fun register(
        userName: String,
        userEmail: String,
        userPassword: String,
        confirmPassword: String
    ): String {

        if (userName.isBlank()) {
            return "Please enter your name."
        }

        if (userEmail.isBlank()) {
            return "Please enter your email."
        }

        if (userPassword.length < 8) {
            return "Password should have at least 8 characters."
        }

        if (userPassword != confirmPassword) {
            return "Passwords do not match."
        }


        // Check if email already exists
        for (user in TemporaryStorage.users) {

            if (user.email == userEmail) {
                return "Email is already registered."
            }
        }


        // Save information to this User object
        this.name = userName
        this.email = userEmail
        this.password = userPassword


        // Temporarily save account
        TemporaryStorage.users.add(this)

        return "Registration successful."
    }


    fun logIn(
        userEmail: String,
        userPassword: String
    ): Boolean {

        for (user in TemporaryStorage.users) {

            if (
                user.email == userEmail &&
                user.password == userPassword
            ) {

                Session.isLoggedIn = true
                Session.name = user.name
                Session.email = user.email

                return true
            }
        }

        return false
    }
}
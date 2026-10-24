package com.example.sulitpackgoui

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class User(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var role: String = "customer"
) {

    private var auth = FirebaseAuth.getInstance()
    private var database = FirebaseFirestore.getInstance()


    fun register(
        userName: String,
        userEmail: String,
        userPassword: String,
        confirmPassword: String,
        onResult: (Boolean, String) -> Unit
    ) {

        if (userName.isBlank()) {
            onResult(false, "Please enter your name.")
            return
        }

        if (userEmail.isBlank()) {
            onResult(false, "Please enter your email.")
            return
        }

        if (userPassword.length < 8) {
            onResult(false, "Password should have at least 8 characters.")
            return
        }

        if (userPassword != confirmPassword) {
            onResult(false, "Passwords do not match.")
            return
        }


        auth.createUserWithEmailAndPassword(
            userEmail,
            userPassword
        )
            .addOnSuccessListener {

                var firebaseUser = auth.currentUser

                if (firebaseUser == null) {
                    onResult(false, "Account creation failed.")
                    return@addOnSuccessListener
                }


                this.uid = firebaseUser.uid
                this.name = userName
                this.email = userEmail
                this.role = "customer"


                var userData = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "role" to role
                )


                database
                    .collection("users")
                    .document(uid)
                    .set(userData)
                    .addOnSuccessListener {

                        onResult(
                            true,
                            "Registration successful."
                        )
                    }
                    .addOnFailureListener { error ->

                        onResult(
                            false,
                            error.message ?: "Failed to save user."
                        )
                    }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Registration failed."
                )
            }
    }

    fun logIn(
        userEmail: String,
        userPassword: String,
        onResult: (Boolean, String) -> Unit
    ) {

        if (userEmail.isBlank()) {
            onResult(false, "Please enter your email.")
            return
        }

        if (userPassword.isBlank()) {
            onResult(false, "Please enter your password.")
            return
        }

        auth.signInWithEmailAndPassword(
            userEmail,
            userPassword
        )
            .addOnSuccessListener {

                var firebaseUser = auth.currentUser

                if (firebaseUser == null) {
                    onResult(false, "Login failed.")
                    return@addOnSuccessListener
                }

                database
                    .collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { document ->

                        if (document.exists()) {

                            this.uid = firebaseUser.uid
                            this.name = document.getString("name") ?: ""
                            this.email = document.getString("email") ?: userEmail
                            this.role = document.getString("role") ?: "customer"

                            onResult(
                                true,
                                "Login successful."
                            )

                        } else {

                            onResult(
                                false,
                                "User profile not found."
                            )
                        }
                    }
                    .addOnFailureListener { error ->

                        onResult(
                            false,
                            error.message ?: "Failed to get user data."
                        )
                    }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Login failed."
                )
            }
    }

    fun logOut() {

        auth.signOut()

        this.uid = ""
        this.name = ""
        this.email = ""
        this.role = "customer"
    }


    fun isLoggedIn(): Boolean {

        if (auth.currentUser != null) {
            return true
        } else {
            return false
        }
    }
}
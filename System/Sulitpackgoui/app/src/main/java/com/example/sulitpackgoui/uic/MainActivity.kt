package com.example.sulitpackgoui.uic

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.customer.Customer
import com.example.sulitpackgoui.uic.customer.CustomerHomeActivity
import com.example.sulitpackgoui.uic.rider.RiderHomeActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var database = FirebaseFirestore.getInstance()
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        setContentView(
            R.layout.customer_login
        )


        setupViews()

        setupButtons()
    }


    private fun setupViews() {

        etEmail =
            findViewById(
                R.id.etEmail
            )


        etPassword =
            findViewById(
                R.id.etPassword
            )
    }


    private fun setupButtons() {

        var btnLogin =
            findViewById<MaterialButton>(
                R.id.btnLogin
            )


        var tvSignUp =
            findViewById<TextView>(
                R.id.tvSignUp
            )


        var tvForgotPassword =
            findViewById<TextView>(
                R.id.tvForgotPassword
            )


        btnLogin.setOnClickListener {

            login()
        }


        tvForgotPassword.setOnClickListener {

            forgotPassword()
        }


        tvSignUp.setOnClickListener {

            var intent =
                Intent(
                    this,
                    SignUpActivity::class.java
                )


            startActivity(
                intent
            )
        }
    }


    private fun login() {

        var email =
            etEmail
                .text
                .toString()
                .trim()


        var password =
            etPassword
                .text
                .toString()


        if (email.isBlank()) {

            showMessage(
                "Enter your email."
            )


            etEmail.requestFocus()

            return
        }


        if (password.isBlank()) {

            showMessage(
                "Enter your password."
            )


            etPassword.requestFocus()

            return
        }


        firebaseAuth
            .signInWithEmailAndPassword(
                email,
                password
            )
            .addOnSuccessListener {

                var currentUser =
                    firebaseAuth.currentUser


                if (currentUser == null) {

                    showMessage(
                        "Login failed."
                    )

                } else {

                    checkUserType(
                        currentUser.uid
                    )
                }
            }
            .addOnFailureListener { error ->

                var message =
                    "Login failed."


                if (error.message != null) {

                    message =
                        error.message.toString()
                }


                showMessage(
                    message
                )
            }
    }


    private fun checkUserType(
        uid: String
    ) {

        database
            .collection("riders")
            .document(uid)
            .get()
            .addOnSuccessListener { riderDocument ->


                if (riderDocument.exists()) {

                    openRiderHome()

                } else {

                    checkUserCollection(
                        uid
                    )
                }
            }
            .addOnFailureListener {

                showMessage(
                    "Failed to check account."
                )
            }
    }


    private fun checkUserCollection(
        uid: String
    ) {

        database
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { userDocument ->


                var customer =
                    userDocument.toObject(
                        Customer::class.java
                    )


                if (customer == null) {

                    firebaseAuth.signOut()


                    showMessage(
                        "Account profile not found."
                    )

                } else if (
                    customer.role == "customer"
                ) {

                    openCustomerHome()

                } else if (
                    customer.role == "admin"
                ) {

                    firebaseAuth.signOut()


                    showMessage(
                        "Admin side is not finished yet."
                    )

                } else {

                    firebaseAuth.signOut()


                    showMessage(
                        "Unknown account type."
                    )
                }
            }
            .addOnFailureListener {

                showMessage(
                    "Failed to load account information."
                )
            }
    }


    private fun openCustomerHome() {

        var intent =
            Intent(
                this,
                CustomerHomeActivity::class.java
            )


        startActivity(
            intent
        )


        finish()
    }


    private fun openRiderHome() {

        var intent =
            Intent(
                this,
                RiderHomeActivity::class.java
            )


        startActivity(
            intent
        )


        finish()
    }


    private fun forgotPassword() {

        var email =
            etEmail
                .text
                .toString()
                .trim()


        if (email.isBlank()) {

            showMessage(
                "Enter your email first."
            )


            etEmail.requestFocus()

            return
        }


        firebaseAuth
            .sendPasswordResetEmail(
                email
            )
            .addOnSuccessListener {

                showMessage(
                    "Password reset email sent. Check your email."
                )
            }
            .addOnFailureListener { error ->

                var message =
                    "Failed to send password reset email."


                if (error.message != null) {

                    message =
                        error.message.toString()
                }


                showMessage(
                    message
                )
            }
    }


    private fun showMessage(
        message: String
    ) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}
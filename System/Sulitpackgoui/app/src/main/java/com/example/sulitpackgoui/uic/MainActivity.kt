package com.example.sulitpackgoui.uic

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.uic.customer.CustomerHomeActivity
import com.example.sulitpackgoui.uic.rider.RiderHomeActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private var firebaseAuth =
        FirebaseAuth.getInstance()

    private var database =
        FirebaseFirestore.getInstance()

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
        setupKeyboard()
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


    private fun setupKeyboard() {

        etEmail.setOnClickListener {

            etEmail.requestFocus()

            showKeyboard(
                etEmail
            )
        }


        etPassword.setOnClickListener {

            etPassword.requestFocus()

            showKeyboard(
                etPassword
            )
        }
    }


    private fun setupButtons() {

        val btnLogin =
            findViewById<MaterialButton>(
                R.id.btnLogin
            )

        val tvSignUp =
            findViewById<TextView>(
                R.id.tvSignUp
            )

        val tvForgotPassword =
            findViewById<TextView>(
                R.id.tvForgotPassword
            )


        btnLogin.setOnClickListener {

            login()
        }


        tvForgotPassword.setOnClickListener {

            forgotPassword()
        }


        // Sign Up is still for CUSTOMER only.
        // Rider accounts will be created by Admin later.
        tvSignUp.setOnClickListener {

            val intent =
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

        val email =
            etEmail.text
                .toString()
                .trim()

        val password =
            etPassword.text
                .toString()


        if (email.isBlank()) {

            showMessage(
                "Enter your email."
            )

            etEmail.requestFocus()

            showKeyboard(
                etEmail
            )

            return
        }


        if (password.isBlank()) {

            showMessage(
                "Enter your password."
            )

            etPassword.requestFocus()

            showKeyboard(
                etPassword
            )

            return
        }


        // First, login using Firebase Authentication.
        //
        // Customer, Rider and Admin will all use
        // the same Firebase login.
        firebaseAuth
            .signInWithEmailAndPassword(
                email,
                password
            )
            .addOnSuccessListener {


                val currentUser =
                    firebaseAuth.currentUser


                if (currentUser == null) {

                    showMessage(
                        "Login failed."
                    )

                    return@addOnSuccessListener
                }


                val uid =
                    currentUser.uid


                // After login, check what type
                // of account this UID belongs to.
                checkUserType(
                    uid
                )
            }
            .addOnFailureListener { error ->

                showMessage(
                    error.message
                        ?: "Login failed."
                )
            }
    }


    private fun checkUserType(
        uid: String
    ) {

        // FIRST:
        // Check if this account is a Rider.
        database
            .collection("riders")
            .document(uid)
            .get()
            .addOnSuccessListener { riderDocument ->


                if (riderDocument.exists()) {

                    openRiderHome()

                } else {

                    // If it is not a Rider,
                    // check the users collection.
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


                if (userDocument.exists() == false) {

                    firebaseAuth.signOut()

                    showMessage(
                        "Account profile not found."
                    )

                    return@addOnSuccessListener
                }


                val role =
                    userDocument
                        .getString("role")
                        ?: ""


                if (role == "customer") {

                    val customerName =
                        userDocument
                            .getString("name")
                            ?: ""


                    openCustomerHome(
                        customerName
                    )

                } else if (role == "admin") {

                    // We will connect the Admin
                    // dashboard later.

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


    private fun openCustomerHome(
        customerName: String
    ) {

        val intent =
            Intent(
                this,
                CustomerHomeActivity::class.java
            )


        intent.putExtra(
            "customerName",
            customerName
        )


        startActivity(
            intent
        )

        finish()
    }


    private fun openRiderHome() {

        val intent =
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

        val email =
            etEmail.text
                .toString()
                .trim()


        if (email.isBlank()) {

            showMessage(
                "Enter your email first."
            )

            etEmail.requestFocus()

            showKeyboard(
                etEmail
            )

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

                showMessage(
                    error.message
                        ?: "Failed to send password reset email."
                )
            }
    }


    private fun showKeyboard(
        editText: EditText
    ) {

        val keyboard =
            getSystemService(
                INPUT_METHOD_SERVICE
            ) as InputMethodManager


        keyboard.showSoftInput(
            editText,
            InputMethodManager.SHOW_IMPLICIT
        )
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
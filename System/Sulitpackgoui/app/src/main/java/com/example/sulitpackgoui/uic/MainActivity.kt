package com.example.sulitpackgoui.uic

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.customer.CustomerAuth
import com.example.sulitpackgoui.uic.customer.CustomerHomeActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private var customerAuth =
        CustomerAuth()

    private var firebaseAuth =
        FirebaseAuth.getInstance()

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

        customerAuth.logIn(
            customerEmail = email,
            customerPassword = password
        ) { success, message, customer ->

            if (
                success &&
                customer != null
            ) {

                val intent =
                    Intent(
                        this,
                        CustomerHomeActivity::class.java
                    )

                intent.putExtra(
                    "customerName",
                    customer.name
                )

                startActivity(
                    intent
                )

                finish()

            } else {

                showMessage(
                    message
                )
            }
        }
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
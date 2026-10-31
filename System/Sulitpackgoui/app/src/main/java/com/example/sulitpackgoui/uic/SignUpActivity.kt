package com.example.sulitpackgoui.uic

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.customer.CustomerAuth
import com.google.android.material.button.MaterialButton

class SignUpActivity : ComponentActivity() {

    private var customerAuth = CustomerAuth()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.customer_signup)

        var etName =
            findViewById<EditText>(R.id.etName)

        var etEmail =
            findViewById<EditText>(R.id.etEmail)

        var etPassword =
            findViewById<EditText>(R.id.etPassword)

        var etConfirmPassword =
            findViewById<EditText>(R.id.etConfirmPassword)

        var btnSignUp =
            findViewById<MaterialButton>(R.id.btnSignUp)

        var btnBack =
            findViewById<TextView>(R.id.btnBack)

        var tvLogin =
            findViewById<TextView>(R.id.tvLogin)

        btnSignUp.setOnClickListener {

            var name =
                etName.text.toString().trim()

            var email =
                etEmail.text.toString().trim()

            var password =
                etPassword.text.toString()

            var confirmPassword =
                etConfirmPassword.text.toString()

            customerAuth.register(
                customerName = name,
                customerEmail = email,
                customerPassword = password,
                confirmPassword = confirmPassword
            ) { success, message ->

                if (success) {

                    Toast.makeText(
                        this,
                        "Account created successfully.",
                        Toast.LENGTH_SHORT
                    ).show()

                    var intent =
                        Intent(this, MainActivity::class.java)

                    startActivity(intent)

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        btnBack.setOnClickListener {

            finish()
        }

        tvLogin.setOnClickListener {

            finish()
        }
    }
}
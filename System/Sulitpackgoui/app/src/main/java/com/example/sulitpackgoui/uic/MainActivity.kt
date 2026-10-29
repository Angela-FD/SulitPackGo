package com.example.sulitpackgoui.uic

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.customer.CustomerAuth

class MainActivity : ComponentActivity() {

    private var customerAuth = CustomerAuth()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        var etEmail =
            findViewById<EditText>(R.id.etEmail)

        var etPassword =
            findViewById<EditText>(R.id.etPassword)

        var btnLogin =
            findViewById<Button>(R.id.btnLogin)

        var tvSignUp =
            findViewById<TextView>(R.id.tvSignUp)

        btnLogin.setOnClickListener {

            var email =
                etEmail.text.toString().trim()

            var password =
                etPassword.text.toString()

            customerAuth.logIn(
                customerEmail = email,
                customerPassword = password
            ) { success, message, customer ->

                if (success && customer != null) {

                    Toast.makeText(
                        this,
                        "Login successful.",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        tvSignUp.setOnClickListener {

            var intent =
                Intent(this, SignUpActivity::class.java)

            startActivity(intent)
        }
    }
}
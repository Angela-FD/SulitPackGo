package com.example.sulitpackgoui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)


        // CONNECT XML TO KOTLIN
        var emailInput = findViewById<EditText>(R.id.etEmail)

        var passwordInput = findViewById<EditText>(R.id.etPassword)

        var loginButton = findViewById<Button>(R.id.btnLogin)


        // TEMPORARY TEST ACCOUNT
        if (TemporaryStorage.users.isEmpty()) {

            var testUser = User()

            testUser.register(
                userName = "Test User",
                userEmail = "test@email.com",
                userPassword = "password123",
                confirmPassword = "password123"
            )
        }


        // LOGIN BUTTON
        loginButton.setOnClickListener {


            var email = emailInput.text.toString().trim()

            var password =
                passwordInput.text.toString()


            var user = User()

            var loginSuccessful =
                user.logIn(
                    userEmail = email,
                    userPassword = password
                )


            if (loginSuccessful == true) {

                Toast.makeText(
                    this,
                    "Login successful. Welcome ${Session.name}",
                    Toast.LENGTH_LONG
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "Incorrect email or password.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
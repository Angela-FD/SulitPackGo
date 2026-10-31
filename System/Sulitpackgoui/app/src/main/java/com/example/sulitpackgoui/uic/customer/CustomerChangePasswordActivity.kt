package com.example.sulitpackgoui.uic.customer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class CustomerChangePasswordActivity : ComponentActivity() {

    private var auth =
        FirebaseAuth.getInstance()

    private lateinit var etCurrentPassword: TextInputEditText
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.customer_change_password
        )

        setupViews()
        setupButtons()
    }

    private fun setupViews() {

        etCurrentPassword =
            findViewById(R.id.etCurrentPassword)

        etNewPassword =
            findViewById(R.id.etNewPassword)

        etConfirmPassword =
            findViewById(R.id.etConfirmPassword)
    }

    private fun setupButtons() {

        var btnSave =
            findViewById<MaterialButton>(
                R.id.btnSavePassword
            )

        var btnCancel =
            findViewById<MaterialButton>(
                R.id.btnCancelPassword
            )

        btnSave.setOnClickListener {
            changePassword()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun changePassword() {

        var currentPassword =
            etCurrentPassword.text
                .toString()
                .trim()

        var newPassword =
            etNewPassword.text
                .toString()
                .trim()

        var confirmPassword =
            etConfirmPassword.text
                .toString()
                .trim()

        if (currentPassword.isBlank()) {
            showMessage(
                "Enter your current password."
            )
            return
        }

        if (newPassword.length < 8) {
            showMessage(
                "New password must be at least 8 characters."
            )
            return
        }

        if (newPassword != confirmPassword) {
            showMessage(
                "New passwords do not match."
            )
            return
        }

        var user =
            auth.currentUser

        if (user == null) {
            showMessage(
                "Please log in again."
            )
            return
        }

        var email =
            user.email

        if (email.isNullOrBlank()) {
            showMessage(
                "Account email was not found."
            )
            return
        }

        var credential =
            EmailAuthProvider.getCredential(
                email,
                currentPassword
            )

        user.reauthenticate(credential)
            .addOnSuccessListener {

                updatePassword(
                    newPassword
                )
            }
            .addOnFailureListener {

                showMessage(
                    "Current password is incorrect."
                )
            }
    }

    private fun updatePassword(
        newPassword: String
    ) {

        var user =
            auth.currentUser
                ?: return

        user.updatePassword(
            newPassword
        )
            .addOnSuccessListener {

                showMessage(
                    "Password changed successfully."
                )

                finish()
            }
            .addOnFailureListener { error ->

                showMessage(
                    error.message
                        ?: "Failed to change password."
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
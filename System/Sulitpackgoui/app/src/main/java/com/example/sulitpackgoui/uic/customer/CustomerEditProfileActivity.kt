package com.example.sulitpackgoui.uic.customer

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.customer.CustomerDatabase
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CustomerEditProfileActivity : ComponentActivity() {

    private var auth =
        FirebaseAuth.getInstance()


    private var database =
        FirebaseFirestore.getInstance()


    private var customerDatabase =
        CustomerDatabase()


    private lateinit var etName:
            EditText

    private lateinit var etEmail:
            EditText

    private lateinit var etPhoneNumber:
            EditText

    private lateinit var btnSave:
            MaterialButton


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        setContentView(
            R.layout.customer_edit_profile
        )


        setupViews()

        setupButtons()

        loadProfile()
    }


    private fun setupViews() {

        etName =
            findViewById(
                R.id.etName
            )


        etEmail =
            findViewById(
                R.id.etEmail
            )


        etPhoneNumber =
            findViewById(
                R.id.etPhoneNumber
            )


        btnSave =
            findViewById(
                R.id.btnSave
            )
    }


    private fun setupButtons() {

        var btnBack =
            findViewById<TextView>(
                R.id.btnBack
            )


        btnBack.setOnClickListener {

            finish()
        }


        btnSave.setOnClickListener {

            saveProfile()
        }
    }


    private fun loadProfile() {

        var user =
            auth.currentUser


        if (user == null) {

            showMessage(
                "Please log in again."
            )


            finish()

            return
        }


        customerDatabase.getCustomer(
            user.uid
        ) { success, message, customer ->


            if (success == false) {

                showMessage(
                    message
                )

            } else if (
                customer == null
            ) {

                showMessage(
                    "Customer profile not found."
                )

            } else {

                etName.setText(
                    customer.name
                )


                if (
                    customer.email.isBlank()
                ) {

                    if (user.email != null) {

                        etEmail.setText(
                            user.email.toString()
                        )

                    } else {

                        etEmail.setText(
                            ""
                        )
                    }

                } else {

                    etEmail.setText(
                        customer.email
                    )
                }


                etPhoneNumber.setText(
                    customer.phoneNumber
                )
            }
        }
    }


    private fun saveProfile() {

        var user =
            auth.currentUser


        if (user == null) {

            showMessage(
                "Please log in again."
            )

            return
        }


        var name =
            etName
                .text
                .toString()
                .trim()


        var phoneNumber =
            etPhoneNumber
                .text
                .toString()
                .trim()


        if (name.isBlank()) {

            showMessage(
                "Please enter your name."
            )

            return
        }


        if (phoneNumber.isBlank()) {

            showMessage(
                "Please enter your phone number."
            )

            return
        }


        if (phoneNumber.length != 11) {

            showMessage(
                "Phone number must be 11 digits."
            )

            return
        }


        if (
            phoneNumber.startsWith("09") == false
        ) {

            showMessage(
                "Phone number must start with 09."
            )

            return
        }


        for (character in phoneNumber) {

            if (
                character.isDigit() == false
            ) {

                showMessage(
                    "Phone number must contain numbers only."
                )

                return
            }
        }


        btnSave.isEnabled =
            false


        database
            .collection("users")
            .document(user.uid)
            .update(
                "name",
                name,
                "phoneNumber",
                phoneNumber
            )
            .addOnSuccessListener {

                showMessage(
                    "Profile updated successfully."
                )


                finish()
            }
            .addOnFailureListener { error ->

                btnSave.isEnabled =
                    true


                var message =
                    "Failed to update profile."


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
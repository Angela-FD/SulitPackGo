package com.example.sulitpackgoui.uic.customer

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.customer.CustomerDatabase
import com.example.sulitpackgoui.uic.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class CustomerProfileActivity : ComponentActivity() {

    private var auth =
        FirebaseAuth.getInstance()


    private var customerDatabase =
        CustomerDatabase()


    private lateinit var tvProfileName:
            TextView

    private lateinit var tvProfileEmail:
            TextView

    private lateinit var tvProfilePhone:
            TextView


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        setContentView(
            R.layout.customer_profile
        )


        setupViews()

        setupButtons()

        setupNavigation()
    }


    override fun onResume() {

        super.onResume()


        loadProfile()
    }


    private fun setupViews() {

        tvProfileName =
            findViewById(
                R.id.tvProfileName
            )


        tvProfileEmail =
            findViewById(
                R.id.tvProfileEmail
            )


        tvProfilePhone =
            findViewById(
                R.id.tvProfilePhone
            )
    }


    private fun loadProfile() {

        var user =
            auth.currentUser


        if (user == null) {

            showMessage(
                "Please log in again."
            )

            return
        }


        customerDatabase.getCustomer(
            user.uid
        ) { success, message, customer ->


            if (success) {

                if (customer != null) {

                    tvProfileName.text =
                        customer.name


                    if (
                        customer.email.isBlank()
                    ) {

                        if (user.email != null) {

                            tvProfileEmail.text =
                                user.email.toString()

                        } else {

                            tvProfileEmail.text =
                                ""
                        }

                    } else {

                        tvProfileEmail.text =
                            customer.email
                    }


                    if (
                        customer.phoneNumber.isBlank()
                    ) {

                        tvProfilePhone.text =
                            "Not provided"

                    } else {

                        tvProfilePhone.text =
                            customer.phoneNumber
                    }

                } else {

                    showMessage(
                        "Customer profile not found."
                    )
                }

            } else {

                showMessage(
                    message
                )
            }
        }
    }


    private fun setupButtons() {

        var btnEditProfile =
            findViewById<MaterialButton>(
                R.id.btnEditProfile
            )


        var btnChangePassword =
            findViewById<TextView>(
                R.id.btnChangePassword
            )


        var btnLogout =
            findViewById<MaterialButton>(
                R.id.btnLogout
            )


        btnEditProfile.setOnClickListener {

            var intent =
                Intent(
                    this,
                    CustomerEditProfileActivity::class.java
                )


            startActivity(
                intent
            )
        }


        btnChangePassword.setOnClickListener {

            var intent =
                Intent(
                    this,
                    CustomerChangePasswordActivity::class.java
                )


            startActivity(
                intent
            )
        }


        btnLogout.setOnClickListener {

            logout()
        }
    }


    private fun setupNavigation() {

        var navOrders =
            findViewById<LinearLayout>(
                R.id.navOrders
            )


        var navHome =
            findViewById<LinearLayout>(
                R.id.navHome
            )


        var navProfile =
            findViewById<LinearLayout>(
                R.id.navProfile
            )


        navOrders.setOnClickListener {

            var intent =
                Intent(
                    this,
                    CustomerOrdersActivity::class.java
                )


            startActivity(
                intent
            )


            finish()
        }


        navHome.setOnClickListener {

            var intent =
                Intent(
                    this,
                    CustomerHomeActivity::class.java
                )


            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
            )


            intent.addFlags(
                Intent.FLAG_ACTIVITY_SINGLE_TOP
            )


            startActivity(
                intent
            )


            finish()
        }


        navProfile.setOnClickListener {

            loadProfile()
        }
    }


    private fun logout() {

        auth.signOut()


        var intent =
            Intent(
                this,
                MainActivity::class.java
            )


        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
        )


        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TASK
        )


        startActivity(
            intent
        )


        finish()
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
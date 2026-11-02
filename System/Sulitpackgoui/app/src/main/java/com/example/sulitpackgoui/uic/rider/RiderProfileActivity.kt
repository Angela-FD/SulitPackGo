package com.example.sulitpackgoui.uic.rider

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.rider.Rider
import com.example.sulitpackgoui.rider.RiderAuth
import com.example.sulitpackgoui.rider.RiderDatabase
import com.example.sulitpackgoui.uic.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class RiderProfileActivity : ComponentActivity() {

    private var firebaseAuth =
        FirebaseAuth.getInstance()


    private var riderDatabase =
        RiderDatabase()


    private var riderAuth =
        RiderAuth()


    private lateinit var tvAvatarInitials:
            TextView

    private lateinit var tvProfileName:
            TextView

    private lateinit var tvEmail:
            TextView

    private lateinit var tvPhone:
            TextView

    private lateinit var tvVehicle:
            TextView

    private lateinit var btnLogout:
            MaterialButton


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        setContentView(
            R.layout.rider_profile
        )


        setupViews()

        setupNavigation()

        setupButtons()
    }


    override fun onResume() {

        super.onResume()


        loadRider()
    }


    private fun setupViews() {

        tvAvatarInitials =
            findViewById(
                R.id.tvAvatarInitials
            )


        tvProfileName =
            findViewById(
                R.id.tvProfileName
            )


        tvEmail =
            findViewById(
                R.id.tvEmail
            )


        tvPhone =
            findViewById(
                R.id.tvPhone
            )


        tvVehicle =
            findViewById(
                R.id.tvVehicle
            )


        btnLogout =
            findViewById(
                R.id.btnLogout
            )
    }


    private fun setupNavigation() {

        var navSettings =
            findViewById<TextView>(
                R.id.navSettings
            )


        var navHome =
            findViewById<TextView>(
                R.id.navHome
            )


        var navHistory =
            findViewById<TextView>(
                R.id.navHistory
            )


        navSettings.setOnClickListener {

            loadRider()
        }


        navHome.setOnClickListener {

            var intent =
                Intent(
                    this,
                    RiderHomeActivity::class.java
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


        navHistory.setOnClickListener {

            var intent =
                Intent(
                    this,
                    RiderHistoryActivity::class.java
                )


            startActivity(
                intent
            )
        }
    }


    private fun setupButtons() {

        btnLogout.setOnClickListener {

            logoutRider()
        }
    }


    private fun loadRider() {

        var currentUser =
            firebaseAuth.currentUser


        if (currentUser == null) {

            showMessage(
                "Rider is not logged in."
            )


            returnToLogin()

            return
        }


        riderDatabase.getRider(
            currentUser.uid
        ) { success, message, rider ->


            if (success == false) {

                showMessage(
                    message
                )

            } else if (
                rider == null
            ) {

                showMessage(
                    "Rider profile not found."
                )

            } else {

                showRiderInformation(
                    rider
                )
            }
        }
    }


    private fun showRiderInformation(
        rider: Rider
    ) {

        tvProfileName.text =
            rider.name


        tvEmail.text =
            rider.email


        if (
            rider.phoneNumber.isBlank()
        ) {

            tvPhone.text =
                "Not provided"

        } else {

            tvPhone.text =
                rider.phoneNumber
        }


        tvVehicle.text =
            rider.vehicleType


        tvAvatarInitials.text =
            getInitials(
                rider.name
            )
    }


    private fun getInitials(
        name: String
    ): String {

        var parts =
            name
                .trim()
                .split(" ")


        var cleanParts =
            mutableListOf<String>()


        for (part in parts) {

            if (part.isNotBlank()) {

                cleanParts.add(
                    part
                )
            }
        }


        if (cleanParts.isEmpty()) {

            return "R"
        }


        if (cleanParts.size == 1) {

            return cleanParts[0]
                .first()
                .uppercaseChar()
                .toString()
        }


        var firstInitial =
            cleanParts[0]
                .first()
                .uppercaseChar()
                .toString()


        var secondInitial =
            cleanParts[1]
                .first()
                .uppercaseChar()
                .toString()


        return firstInitial +
                secondInitial
    }


    private fun logoutRider() {

        riderAuth.logout()


        returnToLogin()
    }


    private fun returnToLogin() {

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
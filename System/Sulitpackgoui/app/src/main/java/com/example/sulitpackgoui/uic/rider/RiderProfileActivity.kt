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


    private lateinit var tvAvatarInitials: TextView
    private lateinit var tvProfileName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvVehicle: TextView

    private lateinit var btnLogout: MaterialButton


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
            findViewById(R.id.tvAvatarInitials)

        tvProfileName =
            findViewById(R.id.tvProfileName)

        tvEmail =
            findViewById(R.id.tvEmail)

        tvPhone =
            findViewById(R.id.tvPhone)

        tvVehicle =
            findViewById(R.id.tvVehicle)

        btnLogout =
            findViewById(R.id.btnLogout)
    }


    private fun setupNavigation() {

        val navSettings =
            findViewById<TextView>(
                R.id.navSettings
            )

        val navHome =
            findViewById<TextView>(
                R.id.navHome
            )

        val navHistory =
            findViewById<TextView>(
                R.id.navHistory
            )


        // SETTINGS
        navSettings.setOnClickListener {

            loadRider()
        }


        // HOME
        navHome.setOnClickListener {

            val intent =
                Intent(
                    this,
                    RiderHomeActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP


            startActivity(
                intent
            )


            finish()
        }


        // HISTORY
        navHistory.setOnClickListener {

            val intent =
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

        val currentUser =
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


            if (success && rider != null) {

                showRiderInformation(
                    rider
                )

            } else {

                showMessage(
                    message
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


        tvPhone.text =
            if (rider.phoneNumber.isBlank()) {

                "Not provided"

            } else {

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

        val parts =
            name.trim()
                .split(" ")
                .filter {
                    it.isNotBlank()
                }


        if (parts.isEmpty()) {

            return "R"
        }


        if (parts.size == 1) {

            return parts[0]
                .first()
                .uppercaseChar()
                .toString()
        }


        return "${parts[0].first().uppercaseChar()}" +
                "${parts[1].first().uppercaseChar()}"
    }


    private fun logoutRider() {

        riderAuth.logout()


        val intent =
            Intent(
                this,
                MainActivity::class.java
            )


        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK


        startActivity(
            intent
        )


        finish()
    }


    private fun returnToLogin() {

        val intent =
            Intent(
                this,
                MainActivity::class.java
            )


        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK


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
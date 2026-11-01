package com.example.sulitpackgoui.uic.rider

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.rider.Rider
import com.example.sulitpackgoui.rider.RiderDatabase
import com.example.sulitpackgoui.rider.RiderOrder
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class RiderHomeActivity : ComponentActivity() {

    private var firebaseAuth =
        FirebaseAuth.getInstance()


    private var riderDatabase =
        RiderDatabase()


    private var riderOrder =
        RiderOrder()


    private var currentRider =
        Rider()


    private var riderLoaded =
        false


    private lateinit var tvRiderName:
            TextView

    private lateinit var tvStatus:
            TextView

    private lateinit var tvCurrentTitle:
            TextView

    private lateinit var tvCurrentSubtitle:
            TextView

    private lateinit var switchOnline:
            SwitchCompat

    private lateinit var btnViewAvailableOrders:
            MaterialButton


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        setContentView(
            R.layout.rider_home
        )


        setupViews()

        setupButtons()

        setupNavigation()
    }


    override fun onResume() {

        super.onResume()


        loadRider()
    }


    private fun setupViews() {

        tvRiderName =
            findViewById(
                R.id.tvRiderName
            )


        tvStatus =
            findViewById(
                R.id.tvStatus
            )


        tvCurrentTitle =
            findViewById(
                R.id.tvCurrentTitle
            )


        tvCurrentSubtitle =
            findViewById(
                R.id.tvCurrentSubtitle
            )


        switchOnline =
            findViewById(
                R.id.switchOnline
            )


        btnViewAvailableOrders =
            findViewById(
                R.id.btnViewAvailableOrders
            )
    }


    private fun setupButtons() {

        btnViewAvailableOrders.setOnClickListener {

            openDeliveryScreen()
        }


        switchOnline.setOnClickListener {

            if (riderLoaded) {

                changeAvailability(
                    switchOnline.isChecked
                )
            }
        }
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

            var intent =
                Intent(
                    this,
                    RiderProfileActivity::class.java
                )


            startActivity(
                intent
            )
        }


        navHome.setOnClickListener {

            loadRider()
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


    private fun openDeliveryScreen() {

        if (riderLoaded == false) {

            showMessage(
                "Rider information is still loading."
            )

            return
        }


        if (
            currentRider.currentOrderId.isNotBlank()
        ) {

            var intent =
                Intent(
                    this,
                    RiderActiveDeliveryActivity::class.java
                )


            startActivity(
                intent
            )


            return
        }


        if (
            currentRider.isAvailable == false
        ) {

            showMessage(
                "Turn your availability ON first."
            )

            return
        }


        var intent =
            Intent(
                this,
                RiderAvailableOrdersActivity::class.java
            )


        startActivity(
            intent
        )
    }


    private fun loadRider() {

        var currentUser =
            firebaseAuth.currentUser


        if (currentUser == null) {

            showMessage(
                "Rider is not logged in."
            )


            finish()

            return
        }


        riderLoaded =
            false


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

                currentRider =
                    rider


                riderLoaded =
                    true


                showRiderInformation()

                loadCurrentDelivery()
            }
        }
    }


    private fun showRiderInformation() {

        tvRiderName.text =
            currentRider.name


        switchOnline.isChecked =
            currentRider.isAvailable


        if (
            currentRider.currentOrderId.isNotBlank()
        ) {

            tvStatus.text =
                "You have an active delivery."


            switchOnline.isEnabled =
                false

        } else {

            switchOnline.isEnabled =
                true


            if (
                currentRider.isAvailable
            ) {

                tvStatus.text =
                    "Available for delivery requests."

            } else {

                tvStatus.text =
                    "You are currently unavailable."
            }
        }
    }


    private fun changeAvailability(
        isAvailable: Boolean
    ) {

        riderDatabase.updateAvailability(
            currentRider,
            isAvailable
        ) { success, message ->


            if (success) {

                showRiderInformation()

            } else {

                switchOnline.isChecked =
                    currentRider.isAvailable
            }


            showMessage(
                message
            )
        }
    }


    private fun loadCurrentDelivery() {

        if (
            currentRider.currentOrderId.isBlank()
        ) {

            tvCurrentTitle.text =
                "No active delivery"


            tvCurrentSubtitle.text =
                "Browse available orders and accept one when you are ready."


            btnViewAvailableOrders.text =
                "View available orders"


            return
        }


        riderOrder.getCurrentOrder(
            currentRider
        ) { success, message, order ->


            if (success == false) {

                showCurrentOrderError(
                    message
                )

            } else if (
                order == null
            ) {

                showCurrentOrderError(
                    "Unable to load delivery."
                )

            } else {

                tvCurrentTitle.text =
                    order.status


                tvCurrentSubtitle.text =
                    order.pickupAddress +
                            " → " +
                            order.dropAddress


                btnViewAvailableOrders.text =
                    "Open current delivery"
            }
        }
    }


    private fun showCurrentOrderError(
        message: String
    ) {

        tvCurrentTitle.text =
            "Unable to load delivery"


        tvCurrentSubtitle.text =
            message


        btnViewAvailableOrders.text =
            "Open current delivery"
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
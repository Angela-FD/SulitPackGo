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

    private var currentRider: Rider? =
        null

    private var isLoadingRider =
        true


    private lateinit var tvRiderName: TextView
    private lateinit var tvStatus: TextView

    private lateinit var tvCurrentTitle: TextView
    private lateinit var tvCurrentSubtitle: TextView

    private lateinit var switchOnline: SwitchCompat

    private lateinit var btnViewAvailableOrders: MaterialButton


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
        loadRider()
    }


    override fun onResume() {
        super.onResume()

        if (::tvRiderName.isInitialized) {
            loadRider()
        }
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

            val rider =
                currentRider


            if (rider == null) {

                showMessage(
                    "Rider information is still loading."
                )

                return@setOnClickListener
            }


            if (rider.currentOrderId.isNotBlank()) {

                val intent =
                    Intent(
                        this,
                        RiderActiveDeliveryActivity::class.java
                    )

                startActivity(
                    intent
                )

                return@setOnClickListener
            }


            if (rider.isAvailable == false) {

                showMessage(
                    "Turn your availability ON first."
                )

                return@setOnClickListener
            }


            val intent =
                Intent(
                    this,
                    RiderAvailableOrdersActivity::class.java
                )

            startActivity(
                intent
            )
        }


        switchOnline.setOnCheckedChangeListener {

                _, isChecked ->


            if (isLoadingRider) {

                return@setOnCheckedChangeListener
            }


            changeAvailability(
                isChecked
            )
        }
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

            val intent =
                Intent(
                    this,
                    RiderProfileActivity::class.java
                )

            startActivity(
                intent
            )
        }


        // HOME
        navHome.setOnClickListener {

            loadRider()
        }


        // HISTORY
        navHistory.setOnClickListener {

            openHistory()
        }
    }


    private fun openHistory() {

        val intent =
            Intent(
                this,
                RiderHistoryActivity::class.java
            )

        startActivity(
            intent
        )
    }


    private fun loadRider() {

        val currentUser =
            firebaseAuth.currentUser


        if (currentUser == null) {

            showMessage(
                "Rider is not logged in."
            )

            finish()

            return
        }


        isLoadingRider =
            true


        riderDatabase.getRider(
            currentUser.uid
        ) { success, message, rider ->


            if (success && rider != null) {

                currentRider =
                    rider


                showRiderInformation(
                    rider
                )


                loadCurrentDelivery(
                    rider
                )

            } else {

                showMessage(
                    message
                )
            }


            isLoadingRider =
                false
        }
    }


    private fun showRiderInformation(
        rider: Rider
    ) {

        tvRiderName.text =
            rider.name


        isLoadingRider =
            true


        switchOnline.isChecked =
            rider.isAvailable


        isLoadingRider =
            false


        if (rider.currentOrderId.isNotBlank()) {

            tvStatus.text =
                "You have an active delivery."


            switchOnline.isEnabled =
                false

        } else {

            switchOnline.isEnabled =
                true


            if (rider.isAvailable) {

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

        val rider =
            currentRider
                ?: return


        riderDatabase.updateAvailability(
            rider,
            isAvailable
        ) { success, message ->


            if (success) {

                showRiderInformation(
                    rider
                )

            } else {

                isLoadingRider =
                    true


                switchOnline.isChecked =
                    rider.isAvailable


                isLoadingRider =
                    false
            }


            showMessage(
                message
            )
        }
    }


    private fun loadCurrentDelivery(
        rider: Rider
    ) {

        if (rider.currentOrderId.isBlank()) {

            tvCurrentTitle.text =
                "No active delivery"


            tvCurrentSubtitle.text =
                "Browse available orders and accept one when you are ready."


            btnViewAvailableOrders.text =
                "View available orders"


            return
        }


        riderOrder.getCurrentOrder(
            rider
        ) { success, message, order ->


            if (success && order != null) {

                tvCurrentTitle.text =
                    order.status


                tvCurrentSubtitle.text =
                    "${order.pickupAddress} → ${order.dropAddress}"


                btnViewAvailableOrders.text =
                    "Open current delivery"

            } else {

                tvCurrentTitle.text =
                    "Unable to load delivery"


                tvCurrentSubtitle.text =
                    message


                btnViewAvailableOrders.text =
                    "Open current delivery"
            }
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
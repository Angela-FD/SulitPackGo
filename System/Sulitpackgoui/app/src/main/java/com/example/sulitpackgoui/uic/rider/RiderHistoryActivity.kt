package com.example.sulitpackgoui.uic.rider

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.order.OrderData
import com.example.sulitpackgoui.rider.Rider
import com.example.sulitpackgoui.rider.RiderDatabase
import com.example.sulitpackgoui.rider.RiderOrder
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RiderHistoryActivity : ComponentActivity() {

    private var firebaseAuth =
        FirebaseAuth.getInstance()

    private var riderDatabase =
        RiderDatabase()

    private var riderOrder =
        RiderOrder()


    private lateinit var tvNoHistory: TextView

    private lateinit var cardHistory1: LinearLayout
    private lateinit var cardHistory2: LinearLayout
    private lateinit var cardHistory3: LinearLayout
    private lateinit var cardHistory4: LinearLayout

    private lateinit var tvHistoryId1: TextView
    private lateinit var tvHistoryId2: TextView
    private lateinit var tvHistoryId3: TextView
    private lateinit var tvHistoryId4: TextView

    private lateinit var tvHistoryRoute1: TextView
    private lateinit var tvHistoryRoute2: TextView
    private lateinit var tvHistoryRoute3: TextView
    private lateinit var tvHistoryRoute4: TextView

    private lateinit var tvHistoryFee1: TextView
    private lateinit var tvHistoryFee2: TextView
    private lateinit var tvHistoryFee3: TextView
    private lateinit var tvHistoryFee4: TextView

    private lateinit var tvHistoryStatus1: TextView
    private lateinit var tvHistoryStatus2: TextView
    private lateinit var tvHistoryStatus3: TextView
    private lateinit var tvHistoryStatus4: TextView


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.rider_history
        )

        setupViews()

        setupNavigation()
    }


    override fun onResume() {

        super.onResume()

        loadRider()
    }


    private fun setupViews() {

        tvNoHistory =
            findViewById(
                R.id.tvNoHistory
            )


        cardHistory1 =
            findViewById(
                R.id.cardHistory1
            )

        cardHistory2 =
            findViewById(
                R.id.cardHistory2
            )

        cardHistory3 =
            findViewById(
                R.id.cardHistory3
            )

        cardHistory4 =
            findViewById(
                R.id.cardHistory4
            )


        tvHistoryId1 =
            findViewById(
                R.id.tvHistoryId1
            )

        tvHistoryId2 =
            findViewById(
                R.id.tvHistoryId2
            )

        tvHistoryId3 =
            findViewById(
                R.id.tvHistoryId3
            )

        tvHistoryId4 =
            findViewById(
                R.id.tvHistoryId4
            )


        tvHistoryRoute1 =
            findViewById(
                R.id.tvHistoryRoute1
            )

        tvHistoryRoute2 =
            findViewById(
                R.id.tvHistoryRoute2
            )

        tvHistoryRoute3 =
            findViewById(
                R.id.tvHistoryRoute3
            )

        tvHistoryRoute4 =
            findViewById(
                R.id.tvHistoryRoute4
            )


        tvHistoryFee1 =
            findViewById(
                R.id.tvHistoryFee1
            )

        tvHistoryFee2 =
            findViewById(
                R.id.tvHistoryFee2
            )

        tvHistoryFee3 =
            findViewById(
                R.id.tvHistoryFee3
            )

        tvHistoryFee4 =
            findViewById(
                R.id.tvHistoryFee4
            )


        tvHistoryStatus1 =
            findViewById(
                R.id.tvHistoryStatus1
            )

        tvHistoryStatus2 =
            findViewById(
                R.id.tvHistoryStatus2
            )

        tvHistoryStatus3 =
            findViewById(
                R.id.tvHistoryStatus3
            )

        tvHistoryStatus4 =
            findViewById(
                R.id.tvHistoryStatus4
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

            loadRider()
        }
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

                loadHistory(
                    rider
                )
            }
        }
    }


    private fun loadHistory(
        rider: Rider
    ) {

        riderOrder.getDeliveryHistory(
            rider
        ) { success, message, orders ->


            if (success) {

                showHistory(
                    orders
                )

            } else {

                hideAllHistoryCards()


                tvNoHistory.visibility =
                    View.VISIBLE


                tvNoHistory.text =
                    message
            }
        }
    }


    private fun showHistory(
        orders: List<OrderData>
    ) {

        hideAllHistoryCards()


        if (orders.isEmpty()) {

            tvNoHistory.visibility =
                View.VISIBLE


            tvNoHistory.text =
                "No completed deliveries yet."


            return
        }


        tvNoHistory.visibility =
            View.GONE


        if (orders.size >= 1) {

            showOrder(
                orders[0],
                cardHistory1,
                tvHistoryId1,
                tvHistoryRoute1,
                tvHistoryFee1,
                tvHistoryStatus1
            )
        }


        if (orders.size >= 2) {

            showOrder(
                orders[1],
                cardHistory2,
                tvHistoryId2,
                tvHistoryRoute2,
                tvHistoryFee2,
                tvHistoryStatus2
            )
        }


        if (orders.size >= 3) {

            showOrder(
                orders[2],
                cardHistory3,
                tvHistoryId3,
                tvHistoryRoute3,
                tvHistoryFee3,
                tvHistoryStatus3
            )
        }


        if (orders.size >= 4) {

            showOrder(
                orders[3],
                cardHistory4,
                tvHistoryId4,
                tvHistoryRoute4,
                tvHistoryFee4,
                tvHistoryStatus4
            )
        }
    }


    private fun showOrder(
        order: OrderData,
        card: LinearLayout,
        tvId: TextView,
        tvRoute: TextView,
        tvFee: TextView,
        tvStatus: TextView
    ) {

        card.visibility =
            View.VISIBLE


        tvId.text =
            "Order #" +
                    order.orderId


        tvRoute.text =
            order.pickupAddress +
                    " → " +
                    order.dropAddress


        tvFee.text =
            "₱" +
                    String.format(
                        "%.2f",
                        order.fee
                    )


        tvStatus.text =
            "Delivered • " +
                    formatDate(
                        order.createdAt
                    )
    }


    private fun hideAllHistoryCards() {

        cardHistory1.visibility =
            View.GONE

        cardHistory2.visibility =
            View.GONE

        cardHistory3.visibility =
            View.GONE

        cardHistory4.visibility =
            View.GONE
    }


    private fun formatDate(
        timestamp: Long
    ): String {

        var formatter =
            SimpleDateFormat(
                "MMM dd, hh:mm a",
                Locale.getDefault()
            )


        var date =
            Date(
                timestamp
            )


        return formatter.format(
            date
        )
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
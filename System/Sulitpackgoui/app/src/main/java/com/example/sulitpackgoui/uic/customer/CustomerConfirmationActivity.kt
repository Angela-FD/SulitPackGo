package com.example.sulitpackgoui.uic.customer

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.google.android.material.button.MaterialButton

class CustomerConfirmationActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        setContentView(
            R.layout.customer_confirmation
        )


        var tvOrderId =
            findViewById<TextView>(
                R.id.tvOrderId
            )


        var tvStatus =
            findViewById<TextView>(
                R.id.tvOrderStatus
            )


        var tvPayment =
            findViewById<TextView>(
                R.id.tvPaymentMethod
            )


        var tvTotalFee =
            findViewById<TextView>(
                R.id.tvTotalFee
            )


        var btnTrack =
            findViewById<MaterialButton>(
                R.id.btnTrackDelivery
            )


        var btnHome =
            findViewById<MaterialButton>(
                R.id.btnBackToHome
            )


        var orderId =
            readTextExtra(
                "orderId"
            )


        var totalFee =
            intent.getDoubleExtra(
                "totalFee",
                0.0
            )


        tvOrderId.text =
            orderId


        tvStatus.text =
            "Pending"


        tvPayment.text =
            "Cash on Delivery"


        tvTotalFee.text =
            formatMoney(
                totalFee
            )


        btnTrack.setOnClickListener {

            openTracking(
                orderId
            )
        }


        btnHome.setOnClickListener {

            openHome()
        }
    }


    private fun openTracking(
        orderId: String
    ) {

        var trackIntent =
            Intent(
                this,
                CustomerTrackActivity::class.java
            )


        trackIntent.putExtra(
            "orderId",
            orderId
        )


        startActivity(
            trackIntent
        )
    }


    private fun openHome() {

        var homeIntent =
            Intent(
                this,
                CustomerHomeActivity::class.java
            )


        homeIntent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP
        )


        homeIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
        )


        startActivity(
            homeIntent
        )


        finish()
    }


    private fun readTextExtra(
        name: String
    ): String {

        var value =
            intent.getStringExtra(
                name
            )


        if (value == null) {

            return ""
        }


        return value
    }


    private fun formatMoney(
        amount: Double
    ): String {

        return "₱" +
                String.format(
                    "%.2f",
                    amount
                )
    }
}
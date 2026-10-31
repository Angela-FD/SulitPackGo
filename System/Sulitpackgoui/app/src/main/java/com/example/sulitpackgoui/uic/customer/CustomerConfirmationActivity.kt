package com.example.sulitpackgoui.uic.customer

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.google.android.material.button.MaterialButton

class CustomerConfirmationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.customer_confirmation)

        var tvOrderId =
            findViewById<TextView>(R.id.tvOrderId)

        var tvStatus =
            findViewById<TextView>(R.id.tvOrderStatus)

        var tvPayment =
            findViewById<TextView>(R.id.tvPaymentMethod)

        var tvTotalFee =
            findViewById<TextView>(R.id.tvTotalFee)

        var btnTrack =
            findViewById<MaterialButton>(R.id.btnTrackDelivery)

        var btnHome =
            findViewById<MaterialButton>(R.id.btnBackToHome)

        var orderId =
            intent.getStringExtra("orderId") ?: ""

        var totalFee =
            intent.getDoubleExtra(
                "totalFee",
                0.0
            )

        var customerName =
            intent.getStringExtra(
                "customerName"
            ) ?: "Customer"

        tvOrderId.text = orderId
        tvStatus.text = "Pending"
        tvPayment.text = "Cash on Delivery"

        tvTotalFee.text =
            "₱%.2f".format(totalFee)

        btnTrack.setOnClickListener {

            var trackIntent =
                Intent(
                    this,
                    CustomerTrackActivity::class.java
                )

            trackIntent.putExtra(
                "orderId",
                orderId
            )

            startActivity(trackIntent)
        }

        btnHome.setOnClickListener {

            var homeIntent =
                Intent(
                    this,
                    CustomerHomeActivity::class.java
                )

            homeIntent.putExtra(
                "customerName",
                customerName
            )

            homeIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(homeIntent)

            finish()
        }
    }
}
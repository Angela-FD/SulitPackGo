package com.example.sulitpackgoui.uic.rider

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
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

class RiderAvailableOrdersActivity : ComponentActivity() {

    private var firebaseAuth =
        FirebaseAuth.getInstance()


    private var riderDatabase =
        RiderDatabase()


    private var riderOrder =
        RiderOrder()


    private lateinit var tvNoOrders:
            TextView


    private lateinit var ordersContainer:
            LinearLayout


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        setContentView(
            R.layout.rider_delivery
        )


        setupViews()

        setupButtons()
    }


    override fun onResume() {

        super.onResume()


        loadRider()
    }


    private fun setupViews() {

        tvNoOrders =
            findViewById(
                R.id.tvNoOrders
            )


        ordersContainer =
            findViewById(
                R.id.ordersContainer
            )
    }


    private fun setupButtons() {

        var btnBack =
            findViewById<ImageButton>(
                R.id.btnBack
            )


        btnBack.setOnClickListener {

            finish()
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

                checkRider(
                    rider
                )
            }
        }
    }


    private fun checkRider(
        rider: Rider
    ) {

        if (
            rider.currentOrderId.isNotBlank()
        ) {

            showMessage(
                "You already have an active delivery."
            )


            finish()

            return
        }


        if (
            rider.isAvailable == false
        ) {

            showMessage(
                "You are currently unavailable."
            )


            finish()

            return
        }


        loadAvailableOrders(
            rider
        )
    }


    private fun loadAvailableOrders(
        rider: Rider
    ) {

        riderOrder.getPendingOrders(
            rider
        ) { success, message, orders ->


            if (success) {

                showOrders(
                    orders
                )

            } else {

                ordersContainer.removeAllViews()


                tvNoOrders.visibility =
                    View.VISIBLE


                showMessage(
                    message
                )
            }
        }
    }


    private fun showOrders(
        orders: List<OrderData>
    ) {

        ordersContainer.removeAllViews()


        if (orders.isEmpty()) {

            tvNoOrders.visibility =
                View.VISIBLE

            return
        }


        tvNoOrders.visibility =
            View.GONE


        for (order in orders) {

            addOrderCard(
                order
            )
        }
    }


    private fun addOrderCard(
        order: OrderData
    ) {

        var card =
            LinearLayout(
                this
            )


        card.orientation =
            LinearLayout.VERTICAL


        card.setPadding(
            dpToPx(16),
            dpToPx(16),
            dpToPx(16),
            dpToPx(16)
        )


        card.setBackgroundResource(
            R.drawable.bg_input_outline
        )


        var cardLayout =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        cardLayout.bottomMargin =
            dpToPx(12)


        card.layoutParams =
            cardLayout


        var tvOrderId =
            createText(
                "Order #" +
                        order.orderId,
                16f,
                true
            )


        card.addView(
            tvOrderId
        )


        var tvRoute =
            createText(
                order.pickupAddress +
                        " → " +
                        order.dropAddress,
                14f,
                false
            )


        card.addView(
            tvRoute
        )


        var tvInfo =
            createText(
                formatNumber(
                    order.distance
                ) +
                        " km • " +
                        formatNumber(
                            order.weight
                        ) +
                        " kg • " +
                        order.vehicleType,
                13f,
                false
            )


        card.addView(
            tvInfo
        )


        var tvFee =
            createText(
                "₱" +
                        formatNumber(
                            order.fee
                        ),
                17f,
                true
            )


        card.addView(
            tvFee
        )


        var tvStatus =
            createText(
                "Pending",
                12f,
                false
            )


        card.addView(
            tvStatus
        )


        card.isClickable =
            true


        card.isFocusable =
            true


        card.setOnClickListener {

            openOrderDetails(
                order.orderId
            )
        }


        ordersContainer.addView(
            card
        )
    }


    private fun createText(
        text: String,
        size: Float,
        bold: Boolean
    ): TextView {

        var textView =
            TextView(
                this
            )


        textView.text =
            text


        textView.textSize =
            size


        textView.setTextColor(
            getColor(
                android.R.color.black
            )
        )


        textView.setPadding(
            0,
            dpToPx(5),
            0,
            dpToPx(5)
        )


        if (bold) {

            textView.setTypeface(
                null,
                Typeface.BOLD
            )
        }


        return textView
    }


    private fun openOrderDetails(
        orderId: String
    ) {

        var intent =
            Intent(
                this,
                RiderOrderDetailsActivity::class.java
            )


        intent.putExtra(
            "orderId",
            orderId
        )


        startActivity(
            intent
        )
    }


    private fun formatNumber(
        number: Double
    ): String {

        return String.format(
            "%.2f",
            number
        )
    }


    private fun dpToPx(
        dp: Int
    ): Int {

        var density =
            resources
                .displayMetrics
                .density


        return (
                dp *
                        density
                ).toInt()
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
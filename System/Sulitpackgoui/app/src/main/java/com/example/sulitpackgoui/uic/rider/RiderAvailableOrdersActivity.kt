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


    private var currentRider: Rider? =
        null


    private lateinit var tvNoOrders: TextView

    private lateinit var ordersContainer: LinearLayout


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

        // Reload the available orders every time
        // the Rider opens or returns to this screen.
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

        val btnBack =
            findViewById<ImageButton>(
                R.id.btnBack
            )


        btnBack.setOnClickListener {

            finish()
        }
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


        riderDatabase.getRider(
            currentUser.uid
        ) { success, message, rider ->


            if (success && rider != null) {

                currentRider =
                    rider


                // Rider already has an order.
                if (rider.currentOrderId.isNotBlank()) {

                    showMessage(
                        "You already have an active delivery."
                    )

                    finish()

                    return@getRider
                }


                // Rider is unavailable.
                if (rider.isAvailable == false) {

                    showMessage(
                        "You are currently unavailable."
                    )

                    finish()

                    return@getRider
                }


                loadAvailableOrders(
                    rider
                )

            } else {

                showMessage(
                    message
                )
            }
        }
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

        // Remove the old cards first.
        //
        // This prevents duplicate cards
        // when the screen refreshes.
        ordersContainer.removeAllViews()


        if (orders.isEmpty()) {

            tvNoOrders.visibility =
                View.VISIBLE

            return
        }


        tvNoOrders.visibility =
            View.GONE


        // Create one card for every order.
        for (order in orders) {

            addOrderCard(
                order
            )
        }
    }


    private fun addOrderCard(
        order: OrderData
    ) {

        // Main card
        val card =
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


        val cardLayout =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        cardLayout.bottomMargin =
            dpToPx(12)


        card.layoutParams =
            cardLayout


        // -------------------------
        // ORDER ID
        // -------------------------

        val tvOrderId =
            TextView(
                this
            )


        tvOrderId.text =
            "Order #${order.orderId}"


        tvOrderId.textSize =
            16f


        tvOrderId.setTextColor(
            getColor(
                android.R.color.black
            )
        )


        tvOrderId.setTypeface(
            null,
            Typeface.BOLD
        )


        card.addView(
            tvOrderId
        )


        // -------------------------
        // ROUTE
        // -------------------------

        val tvRoute =
            TextView(
                this
            )


        tvRoute.text =
            "${order.pickupAddress} → ${order.dropAddress}"


        tvRoute.textSize =
            14f


        tvRoute.setTextColor(
            getColor(
                android.R.color.black
            )
        )


        val routeLayout =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        routeLayout.topMargin =
            dpToPx(8)


        tvRoute.layoutParams =
            routeLayout


        card.addView(
            tvRoute
        )


        // -------------------------
        // ORDER INFORMATION
        // -------------------------

        val tvInfo =
            TextView(
                this
            )


        tvInfo.text =
            "${String.format("%.2f", order.distance)} km" +
                    " • " +
                    "${String.format("%.2f", order.weight)} kg" +
                    " • " +
                    order.vehicleType


        tvInfo.textSize =
            13f


        tvInfo.setTextColor(
            getColor(
                android.R.color.darker_gray
            )
        )


        val infoLayout =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        infoLayout.topMargin =
            dpToPx(6)


        tvInfo.layoutParams =
            infoLayout


        card.addView(
            tvInfo
        )


        // -------------------------
        // FEE
        // -------------------------

        val tvFee =
            TextView(
                this
            )


        tvFee.text =
            "₱${String.format("%.2f", order.fee)}"


        tvFee.textSize =
            17f


        tvFee.setTextColor(
            getColor(
                android.R.color.black
            )
        )


        tvFee.setTypeface(
            null,
            Typeface.BOLD
        )


        val feeLayout =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        feeLayout.topMargin =
            dpToPx(10)


        tvFee.layoutParams =
            feeLayout


        card.addView(
            tvFee
        )


        // -------------------------
        // STATUS
        // -------------------------

        val tvStatus =
            TextView(
                this
            )


        tvStatus.text =
            "Pending"


        tvStatus.textSize =
            12f


        tvStatus.setTextColor(
            getColor(
                android.R.color.darker_gray
            )
        )


        val statusLayout =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        statusLayout.topMargin =
            dpToPx(4)


        tvStatus.layoutParams =
            statusLayout


        card.addView(
            tvStatus
        )


        // -------------------------
        // CLICK ORDER
        // -------------------------

        card.isClickable =
            true

        card.isFocusable =
            true


        card.setOnClickListener {

            openOrderDetails(
                order
            )
        }


        // Add the finished card
        // to the Available Orders list.
        ordersContainer.addView(
            card
        )
    }


    private fun openOrderDetails(
        order: OrderData
    ) {

        val intent =
            Intent(
                this,
                RiderOrderDetailsActivity::class.java
            )


        intent.putExtra(
            "orderId",
            order.orderId
        )


        startActivity(
            intent
        )
    }


    // Converts dp into pixels.
    //
    // Android layouts normally use dp,
    // but views created in Kotlin need pixels.
    private fun dpToPx(
        dp: Int
    ): Int {

        val density =
            resources.displayMetrics.density


        return (
                dp * density
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
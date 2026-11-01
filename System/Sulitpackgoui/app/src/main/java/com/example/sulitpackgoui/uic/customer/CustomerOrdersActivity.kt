package com.example.sulitpackgoui.uic.customer

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.customer.Customer
import com.example.sulitpackgoui.customer.CustomerOrder
import com.example.sulitpackgoui.order.OrderData
import com.google.firebase.auth.FirebaseAuth

class CustomerOrdersActivity : ComponentActivity() {

    private var customerOrder =
        CustomerOrder()


    private var auth =
        FirebaseAuth.getInstance()


    private lateinit var ordersContainer:
            LinearLayout


    private lateinit var tvNoOrders:
            TextView


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        setContentView(
            R.layout.customer_orders
        )


        setupViews()

        setupNavigation()
    }


    override fun onResume() {

        super.onResume()


        loadOrders()
    }


    private fun setupViews() {

        ordersContainer =
            findViewById(
                R.id.ordersContainer
            )


        tvNoOrders =
            findViewById(
                R.id.tvNoOrders
            )
    }


    private fun setupNavigation() {

        var navHome =
            findViewById<LinearLayout>(
                R.id.navHome
            )


        var navOrders =
            findViewById<LinearLayout>(
                R.id.navOrders
            )


        var navProfile =
            findViewById<LinearLayout>(
                R.id.navProfile
            )


        navHome.setOnClickListener {

            finish()
        }


        navOrders.setOnClickListener {

            loadOrders()
        }


        navProfile.setOnClickListener {

            var intent =
                Intent(
                    this,
                    CustomerProfileActivity::class.java
                )


            startActivity(
                intent
            )
        }
    }


    private fun loadOrders() {

        var firebaseUser =
            auth.currentUser


        if (firebaseUser == null) {

            showMessage(
                "Please log in again."
            )

            return
        }


        var customer =
            Customer(
                uid = firebaseUser.uid
            )


        customerOrder.getMyOrders(
            customer
        ) { success, message, orders ->


            if (success == false) {

                showMessage(
                    message
                )

            } else {

                var visibleOrders =
                    removeCancelledOrders(
                        orders
                    )


                showOrders(
                    visibleOrders
                )
            }
        }
    }


    private fun removeCancelledOrders(
        orders: List<OrderData>
    ): List<OrderData> {

        var visibleOrders =
            mutableListOf<OrderData>()


        for (order in orders) {

            if (
                order.status != "Cancelled"
            ) {

                visibleOrders.add(
                    order
                )
            }
        }


        return visibleOrders
    }


    private fun showOrders(
        orders: List<OrderData>
    ) {

        clearOldOrderCards()


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


    private fun clearOldOrderCards() {

        ordersContainer.removeAllViews()


        ordersContainer.addView(
            tvNoOrders
        )
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
            32,
            28,
            32,
            28
        )


        var params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        params.setMargins(
            0,
            0,
            0,
            24
        )


        card.layoutParams =
            params


        card.setBackgroundResource(
            R.drawable.bg_input_outline
        )


        card.isClickable =
            true


        card.isFocusable =
            true


        card.addView(
            createText(
                "Order " +
                        order.orderId,
                true
            )
        )


        card.addView(
            createText(
                "Status: " +
                        order.status,
                true
            )
        )


        card.addView(
            createText(
                "Vehicle: " +
                        order.vehicleType
            )
        )


        card.addView(
            createText(
                "Pickup: " +
                        order.pickupAddress
            )
        )


        card.addView(
            createText(
                "Drop-off: " +
                        order.dropAddress
            )
        )


        card.addView(
            createText(
                "Fee: " +
                        formatMoney(
                            order.fee
                        )
            )
        )


        card.addView(
            createText(
                "Tap to view order"
            )
        )


        card.setOnClickListener {

            openOrder(
                order.orderId
            )
        }


        ordersContainer.addView(
            card
        )
    }


    private fun openOrder(
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


    private fun createText(
        text: String,
        bold: Boolean = false
    ): TextView {

        var textView =
            TextView(
                this
            )


        textView.text =
            text


        textView.textSize =
            15f


        textView.setTextColor(
            getColor(
                android.R.color.black
            )
        )


        textView.setPadding(
            0,
            5,
            0,
            5
        )


        if (bold) {

            textView.setTypeface(
                null,
                Typeface.BOLD
            )
        }


        return textView
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
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

        val navHome =
            findViewById<LinearLayout>(
                R.id.navHome
            )

        val navOrders =
            findViewById<LinearLayout>(
                R.id.navOrders
            )

        val navProfile =
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

            startActivity(
                Intent(
                    this,
                    CustomerProfileActivity::class.java
                )
            )
        }
    }

    private fun loadOrders() {

        val firebaseUser =
            FirebaseAuth
                .getInstance()
                .currentUser

        if (firebaseUser == null) {

            showMessage(
                "Please log in again."
            )

            return
        }

        val customer =
            Customer(
                uid = firebaseUser.uid
            )

        customerOrder.getMyOrders(
            customer
        ) { success, message, orders ->

            if (!success) {

                showMessage(message)
                return@getMyOrders
            }

            val visibleOrders =
                orders.filter {

                    !it.status.equals(
                        "Cancelled",
                        ignoreCase = true
                    )
                }

            runOnUiThread {

                showOrders(
                    visibleOrders
                )
            }
        }
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

        for (
        index in ordersContainer.childCount - 1
                downTo 0
        ) {

            val child =
                ordersContainer.getChildAt(
                    index
                )

            if (child.id != R.id.tvNoOrders) {

                ordersContainer.removeViewAt(
                    index
                )
            }
        }
    }

    private fun addOrderCard(
        order: OrderData
    ) {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            32,
            28,
            32,
            28
        )

        val params =
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
                "Order ${order.orderId}",
                true
            )
        )

        card.addView(
            createText(
                "Status: ${order.status}",
                true
            )
        )

        card.addView(
            createText(
                "Vehicle: ${order.vehicleType}"
            )
        )

        card.addView(
            createText(
                "Pickup: ${order.pickupAddress}"
            )
        )

        card.addView(
            createText(
                "Drop-off: ${order.dropAddress}"
            )
        )

        card.addView(
            createText(
                "Fee: ₱%.2f".format(
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

            val trackIntent =
                Intent(
                    this,
                    CustomerTrackActivity::class.java
                )

            trackIntent.putExtra(
                "orderId",
                order.orderId
            )

            startActivity(
                trackIntent
            )
        }

        ordersContainer.addView(
            card
        )
    }

    private fun createText(
        text: String,
        bold: Boolean = false
    ): TextView {

        val textView =
            TextView(this)

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

    private fun showMessage(
        message: String
    ) {

        runOnUiThread {

            Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
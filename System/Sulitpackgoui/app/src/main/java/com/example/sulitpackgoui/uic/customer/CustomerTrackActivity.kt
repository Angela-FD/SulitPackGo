package com.example.sulitpackgoui.uic.customer

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.customer.Customer
import com.example.sulitpackgoui.customer.CustomerOrder
import com.example.sulitpackgoui.order.OrderData
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class CustomerTrackActivity : ComponentActivity() {
    private var customerOrder = CustomerOrder()
    private var auth = FirebaseAuth.getInstance()
    private var orderId = ""
    private lateinit var tvOrderId: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvVehicle: TextView
    private lateinit var tvPickup: TextView
    private lateinit var tvDropoff: TextView
    private lateinit var tvFee: TextView
    private lateinit var btnCancelOrder:
            MaterialButton


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        setContentView(
            R.layout.customer_track
        )


        setupViews()

        setupButtons()


        orderId =
            readTextExtra(
                "orderId"
            )


        if (orderId.isBlank()) {

            showMessage(
                "Order ID not found."
            )


            finish()

            return
        }
    }


    override fun onResume() {

        super.onResume()


        if (orderId.isNotBlank()) {

            loadOrder()
        }
    }


    private fun setupViews() {

        tvOrderId =
            findViewById(
                R.id.tvOrderId
            )


        tvStatus =
            findViewById(
                R.id.tvStatus
            )


        tvVehicle =
            findViewById(
                R.id.tvVehicle
            )


        tvPickup =
            findViewById(
                R.id.tvPickup
            )


        tvDropoff =
            findViewById(
                R.id.tvDropoff
            )


        tvFee =
            findViewById(
                R.id.tvFee
            )


        btnCancelOrder =
            findViewById(
                R.id.btnCancelOrder
            )
    }


    private fun setupButtons() {

        var btnBack =
            findViewById<MaterialButton>(
                R.id.btnBack
            )


        btnBack.setOnClickListener {

            finish()
        }


        btnCancelOrder.setOnClickListener {

            cancelOrder()
        }
    }


    private fun loadOrder() {

        customerOrder.getOrder(
            orderId
        ) { success, message, order ->


            if (success == false) {

                showMessage(
                    message
                )

            } else if (
                order == null
            ) {

                showMessage(
                    "Order not found."
                )

            } else {

                showOrder(
                    order
                )
            }
        }
    }


    private fun showOrder(
        order: OrderData
    ) {

        tvOrderId.text =
            order.orderId


        tvStatus.text =
            order.status


        tvVehicle.text =
            order.vehicleType


        tvPickup.text =
            order.pickupAddress


        tvDropoff.text =
            order.dropAddress


        tvFee.text =
            formatMoney(
                order.fee
            )


        updateCancelButton(
            order.status
        )
    }


    private fun updateCancelButton(
        status: String
    ) {

        if (status == "Pending") {

            btnCancelOrder.visibility =
                View.VISIBLE

        } else {

            btnCancelOrder.visibility =
                View.GONE
        }
    }


    private fun cancelOrder() {

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


        btnCancelOrder.isEnabled =
            false


        btnCancelOrder.text =
            "Cancelling..."


        customerOrder.cancelOrder(
            customer,
            orderId
        ) { success, message ->


            if (success) {

                showMessage(
                    "Order cancelled successfully."
                )


                btnCancelOrder.visibility =
                    View.GONE


                loadOrder()

            } else {

                showMessage(
                    message
                )


                btnCancelOrder.text =
                    "Cancel Order"


                btnCancelOrder.isEnabled =
                    true
            }
        }
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
package com.example.sulitpackgoui.uic.customer

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.example.sulitpackgoui.uic.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CustomerProfileActivity : ComponentActivity() {

    private var auth =
        FirebaseAuth.getInstance()

    private var database =
        FirebaseFirestore.getInstance()

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.customer_profile
        )

        setupViews()
        setupButtons()
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()

        loadProfile()
    }

    private fun setupViews() {

        tvName =
            findViewById(
                R.id.tvProfileName
            )

        tvEmail =
            findViewById(
                R.id.tvProfileEmail
            )
    }

    private fun loadProfile() {

        val user =
            auth.currentUser

        if (user == null) {
            showMessage(
                "Please log in again."
            )
            return
        }

        database
            .collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->

                tvName.text =
                    document.getString("name")
                        ?: "Customer"

                tvEmail.text =
                    document.getString("email")
                        ?: user.email
                                ?: ""
            }
            .addOnFailureListener { error ->

                showMessage(
                    error.message
                        ?: "Failed to load profile."
                )
            }
    }

    private fun setupButtons() {

        val btnEditProfile =
            findViewById<MaterialButton>(
                R.id.btnEditProfile
            )

        val btnChangePassword =
            findViewById<TextView>(
                R.id.btnChangePassword
            )

        val btnLogout =
            findViewById<MaterialButton>(
                R.id.btnLogout
            )

        btnEditProfile.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    CustomerEditProfileActivity::class.java
                )
            )
        }

        btnChangePassword.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    CustomerChangePasswordActivity::class.java
                )
            )
        }

        btnLogout.setOnClickListener {
            logout()
        }
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

            val homeIntent =
                Intent(
                    this,
                    CustomerHomeActivity::class.java
                )

            homeIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP

            startActivity(
                homeIntent
            )

            finish()
        }

        navOrders.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    CustomerOrdersActivity::class.java
                )
            )

            finish()
        }

        navProfile.setOnClickListener {
            // Already on Profile
        }
    }

    private fun logout() {

        auth.signOut()

        val loginIntent =
            Intent(
                this,
                MainActivity::class.java
            )

        loginIntent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(
            loginIntent
        )

        finish()
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
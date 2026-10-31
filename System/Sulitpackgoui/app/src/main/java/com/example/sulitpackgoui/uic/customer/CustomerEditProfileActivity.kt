package com.example.sulitpackgoui.uic.customer

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.sulitpackgoui.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CustomerEditProfileActivity : ComponentActivity() {

    private var auth =
        FirebaseAuth.getInstance()

    private var database =
        FirebaseFirestore.getInstance()

    private lateinit var etName: EditText
    private lateinit var tvEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.customer_edit_profile)

        setupViews()
        loadProfile()
        setupButtons()
    }

    private fun setupViews() {

        etName =
            findViewById(R.id.etName)

        tvEmail =
            findViewById(R.id.tvEmail)
    }

    private fun loadProfile() {

        var user =
            auth.currentUser

        if (user == null) {
            showMessage("Please log in again.")
            finish()
            return
        }

        tvEmail.text =
            user.email ?: ""

        database
            .collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->

                etName.setText(
                    document.getString("name") ?: ""
                )
            }
            .addOnFailureListener { error ->

                showMessage(
                    error.message
                        ?: "Failed to load profile."
                )
            }
    }

    private fun setupButtons() {

        var btnSave =
            findViewById<MaterialButton>(
                R.id.btnSave
            )

        var btnCancel =
            findViewById<MaterialButton>(
                R.id.btnCancel
            )

        btnSave.setOnClickListener {
            saveProfile()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveProfile() {

        var user =
            auth.currentUser

        if (user == null) {
            showMessage("Please log in again.")
            return
        }

        var name =
            etName.text
                .toString()
                .trim()

        if (name.isBlank()) {
            showMessage(
                "Please enter your name."
            )
            return
        }

        database
            .collection("users")
            .document(user.uid)
            .update(
                "name",
                name
            )
            .addOnSuccessListener {

                showMessage(
                    "Profile updated successfully."
                )

                finish()
            }
            .addOnFailureListener { error ->

                showMessage(
                    error.message
                        ?: "Failed to update profile."
                )
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
package com.example.sulitpackgoui

import android.content.Intent
import android.os.Bundle
<<<<<<< HEAD
import android.widget.Button
=======
>>>>>>> 0bdea9a5201e438df146abee93c8e116da7adbcb
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
<<<<<<< HEAD
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
=======

        setContent {

            Text(
                text = "SulitPackGo - Backend Testing"
            )
>>>>>>> 0bdea9a5201e438df146abee93c8e116da7adbcb
        }
    }
}
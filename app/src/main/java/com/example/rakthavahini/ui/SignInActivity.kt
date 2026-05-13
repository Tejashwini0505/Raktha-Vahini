package com.example.rakthavahini

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        supportActionBar?.hide()

        val etPhone = findViewById<EditText>(R.id.et_phone)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnSignIn = findViewById<Button>(R.id.btn_signin)
        val tvSignUp = findViewById<TextView>(R.id.tv_signup)

        btnSignIn.setOnClickListener {
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                    "Please enter phone and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check credentials from SharedPreferences
            val prefs = getSharedPreferences("donor_prefs", Context.MODE_PRIVATE)
            val savedPhone = prefs.getString("phone", "")
            val savedPassword = prefs.getString("password", "")

            if (phone == savedPhone && password == savedPassword) {
                // Login success
                prefs.edit().putBoolean("is_logged_in", true).apply()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this,
                    "Invalid phone or password", Toast.LENGTH_SHORT).show()
            }
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}